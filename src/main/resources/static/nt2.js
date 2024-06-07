document.addEventListener('DOMContentLoaded', () => {
    const jwtToken = localStorage.getItem('jwtToken');
    const numberButtonsContainer = document.getElementById('number-buttons');
    const amountInput = document.getElementById('amount');
    const submitButton = document.getElementById('submit-button');
    //const balanceDisplay = document.getElementById('balance-display');
    var balance = sessionStorage.getItem('acctBal');
    // Update the content of the div with the retrieved account balance
    //document.getElementById('balance').textContent = 'Rs.' + balance.toFixed(2); // Format the balance as currency
    const username = getUsernameFromLocalStorage();
    const dataContainer = document.getElementById('data-container');

    if(!jwtToken){
        window.location.href='/login';
    }
    console.log(balance);
    let selectedNumbers = [];
    balance =parseFloat(balance) ;
    document.getElementById('balance').textContent = 'Rs.' + balance.toFixed(2);
    // Starting balance
    console.log(balance);
    //let username = 'user2';

    // Create number buttons dynamically
    for (let i = 1; i <= 99; i++) {
        const button = document.createElement('button');
        button.textContent = i;
        button.addEventListener('click', () => toggleNumberSelection(i, button));
        numberButtonsContainer.appendChild(button);
    }

    // Function to handle number selection
    function toggleNumberSelection(number, button) {
        const index = selectedNumbers.indexOf(number);
        if (index === -1) {
            selectedNumbers.push(number);
            button.classList.add('selected');
        } else {
            selectedNumbers.splice(index, 1);
            button.classList.remove('selected');
        }
        // Reset amount when selecting a new number
        updateButtonStyles();
    }

    // Function to update button styles based on selection
    function updateButtonStyles() {
        const buttons = numberButtonsContainer.querySelectorAll('button');
        buttons.forEach(button => {
            const number = parseInt(button.textContent);
            if (selectedNumbers.includes(number)) {
                button.classList.add('selected');
            }else {
                button.classList.remove('selected');
            }
        });
    }

    // Event listener for amount input change
    amountInput.addEventListener('change', () => {
        // Ensure amount is a positive number
        if (amountInput.value < 0) {
            amountInput.value = 0;
        }
    });

    // Event listener for submit button
    submitButton.addEventListener('click', () => {

        if (selectedNumbers.length === 0 || amountInput.value.trim() === '' || parseFloat(amountInput.value) === 0) {
            alert('Please select at least one number / Enter a valid amount');
            return;
        }

        if (selectedNumbers.length !== 0) {
            const bet_number = generateBetId();
            const betAmount = parseFloat(amountInput.value);
            const totalAmount = betAmount * selectedNumbers.length;
            if (totalAmount <= balance) {

                // Send data to backend
                if (betAmount !== 0) {
                    const data = {
                        bet_number: bet_number,
                        selected_numbers: selectedNumbers.toString(),
                        bet_amount: betAmount,
                        username: username
                    };

                    fetch('http://localhost:8081/api/v1/teer/' + username + '/submitNumber/', {
                            method: 'POST',
                            headers: {
                                'Authorization': `Bearer ${jwtToken}`,
                                'Content-Type': 'application/json'
                            },
                            body: JSON.stringify(data),
                        })
                        .then(response => {
                            if (!response.ok) {
                                throw new Error('Network response was not ok');

                            }
                            return response.json();
                        })
                        .then(data => {
                            console.log('Data sent successfully:', data);
                            balance -= totalAmount;
                            console.log("Balance after deduction: " + balance);
                            balance.textContent = balance.toFixed(2);
                            console.log("Balance after deduction: " + balance);
                            document.getElementById('balance').textContent = 'Rs.' + balance.toFixed(2);
                            console.log(`Bet ID: ${bet_number}, Selected numbers: ${selectedNumbers.join(',')}, Amount per number: Rs.${betAmount}, Total amount: Rs.${totalAmount.toFixed(2)}, New balance: Rs. ${balance.toFixed(2)}`);
                            //document.getElementById('username').innerText = username;
                            console.log(`Username: ${username}`)
                            // You can perform further actions here, such as showing a success message to the user
                            alert('Your selections were successfully submitted.');
                            renderData(data);
                        })
                        .catch(error => {
                            console.error('There was a problem with your fetch operation:', error);
                            // You can handle errors here, such as showing an error message to the user
                            alert('Oops! Something went wrong. Please try again later..');
                            dataContainer.innerHTML = '<p>Failed to fetch data. Please try again later.</p>';
                        });
                }


                // Reset selections
                selectedNumbers = [];
                const buttons = numberButtonsContainer.querySelectorAll('button');
                buttons.forEach(button => {
                    button.classList.remove('selected');
                });

            } else {
                alert('Insufficient balance');
                return false;
            }
            // You can perform further actions here, such as sending the selected number and amount to the server
        }
    });

    function renderData(data) {
        const datalist = document.createElement('ul');
        data.forEach(item => {
            const listItem = document.createElement('li');
            listItem.textContent = item;
            listItem.style.display = 'block';
            datalist.appendChild(listItem);
        });

            // Retrieve existing data from local storage
            const storedData = JSON.parse(localStorage.getItem('storedData')) || [];

            // Add new data to the beginning of the array (stack)
            storedData.unshift(data);

            // Limit the stack size if needed
            const maxSize = 99999; // Adjust the maximum size as needed
            if (storedData.length > maxSize) {
                storedData.splice(maxSize); // Remove excess items
            }

            // Store the updated stack in local storage
            localStorage.setItem('storedData', JSON.stringify(storedData));
            console.log(storedData);

            // Clear the container before appending new data
            //dataContainer.innerHTML = '';

            // Append data to the container in the DOM
            dataContainer.appendChild(datalist);
    }

    function generateBetId() {
        const timestamp = new Date().getTime(); // Current timestamp
        return `${timestamp} `;
    }

    // Load and render stored rendered data on page load
    const storedRenderedData = JSON.parse(localStorage.getItem('storedData'));
    if (storedRenderedData) {
        renderData(storedRenderedData);
    }
});

function getUsernameFromLocalStorage() {
      return localStorage.getItem("username");
    }

    // Function to set username in HTML content
    function setUsernameInPage() {
      const username = getUsernameFromLocalStorage();
      if (username) {
        document.getElementById('username').innerText = username;
      } else {
        // Handle the case when username is not found in localStorage
        document.getElementById('username').innerText = "Guest";
      }
    }

    // Call function to set username when page loads
    window.onload = function() {
      setUsernameInPage();
    };
