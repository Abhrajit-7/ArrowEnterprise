document.addEventListener('DOMContentLoaded', () => {
    const numberButtonsContainer = document.getElementById('number-buttons');
    const amountInput = document.getElementById('amount');
    const submitButton = document.getElementById('submit-button');
    const balanceDisplay = document.getElementById('balance-display');
    const userName = document.getElementById('userName');
    const dataContainer = document.getElementById('data-container');

    let selectedNumbers = [];
    let balance = 1000; // Starting balance
    let username = 'Unison07';

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
            } else {
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
                balance -= totalAmount;
                balanceDisplay.textContent = balance.toFixed(2);
                console.log(`Bet ID: ${bet_number}, Selected numbers: ${selectedNumbers.join(',')}, Amount per number: Rs.${betAmount}, Total amount: Rs.${totalAmount.toFixed(2)}, New balance: Rs. ${balance.toFixed(2)}`);

                // Send data to backend
                if (betAmount !== 0) {
                    const data = {
                        bet_number: bet_number,
                        selected_numbers: selectedNumbers,
                        bet_amount: betAmount,
                        username: username
                    };

                    fetch('http://localhost:8080/api/v1/teer/' + username + '/1/submitNumber', {
                            method: 'POST',
                            headers: {
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
            datalist.appendChild(listItem);
        });
        dataContainer.appendChild(datalist);

        // Retrieve existing data from local storage
        const existingData = JSON.parse(localStorage.getItem('renderedData')) || [];

        // Append new data to existing data
        const newData = existingData.concat(data);

        // Store updated data in local storage
        localStorage.setItem('renderedData', JSON.stringify(newData));
    }

    function generateBetId() {
        const timestamp = new Date().getTime(); // Current timestamp
        return `${timestamp} `;
    }

    // Load and render stored rendered data on page load
    const storedRenderedData = JSON.parse(localStorage.getItem('renderedData'));
    if (storedRenderedData) {
        renderData(storedRenderedData);
    }
});
