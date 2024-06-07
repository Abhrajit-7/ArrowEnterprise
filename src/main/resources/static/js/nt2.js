document.addEventListener('DOMContentLoaded', () => {
    const jwtToken = localStorage.getItem('jwtToken');
    const numberButtonsContainer = document.getElementById('number-buttons');
    const amountInput = document.getElementById('amount');
    const submitButton = document.getElementById('submit-button');
    var balance = sessionStorage.getItem('acctBal');
    const username = getUsernameFromLocalStorage();
    const id=localStorage.getItem('myId');
    const dataContainer = document.getElementById('data-container');


    //Adding toggle buttons
    const slotButtons = document.querySelectorAll('.slot-button');
    const switchSlider = document.querySelector('.switch-slider');
    const switchWidth = switchSlider.offsetWidth;
    const containerContents = document.querySelectorAll('.slot-content');

    let currentSlotIndex = 0;

    /*if(!jwtToken){
        window.location.href='/login';
    }*/
    console.log(balance);
    let selectedNumbers = [];
    balance = parseFloat(balance);
    document.getElementById('balance').textContent = 'Rs.' + balance.toFixed(2);
    
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
                // Send data to backend
                if (betAmount !== 0) {
                    const data = {
                        bet_number: bet_number,
                        selected_numbers: selectedNumbers.toString(),
                        bet_amount: betAmount,
                        username: username,
                        slot: "Slot-"+(currentSlotIndex+1)
                    };

                    // Function to fetch data for the selected slot

                        let endpoint = '';
                        if (currentSlotIndex === 0) {
                            endpoint = 'http://arrowenterprise.co.in/api/v1/teer/submit/slot1/'+username;
                        } else if (currentSlotIndex === 1) {
                            endpoint = 'http://arrowenterprise.co.in/api/v1/teer/submit/slot2/'+username;
                        }

                        fetch(endpoint, {
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
                            // Updated balance
                            balance -= totalAmount;
                            // Set the balance in a session
                            sessionStorage.setItem('acctBal', balance);
                            console.log("Balance after deduction: " + balance);
                            balance.textContent = balance.toFixed(2);
                            console.log("Balance after deduction: " + balance);
                            document.getElementById('balance').textContent = 'Rs.' + balance.toFixed(2);
                            console.log(`ID: ${bet_number}, Selected numbers: ${selectedNumbers.join(',')}, Amount per number: Rs.${betAmount}, Total amount: Rs.${totalAmount.toFixed(2)}, New balance: Rs. ${balance.toFixed(2)}`);
                            console.log(`Username: ${username}`);
                            // You can perform further actions here, such as showing a success message to the user
                            alert('Your selections were successfully submitted.');
                            console.log(currentSlotIndex+1);
                            addNewEntry(data, currentSlotIndex+1);

                        })
                        .catch(error => {
                            console.error('There was a problem with your fetch operation:', error);
                            // Handling errors here
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
        }
});

    // On loading the page, set the username and render the data
    window.onload = function() {
        setUsernameInPage();
        // localStorage.getItem('cachedData');
        let onLoadData = JSON.parse(localStorage.getItem('cachedData1'));
        if(onLoadData !== null){
        renderData(onLoadData, currentSlotIndex+1);
        }
    };

       // Function to add a new entry
    function addNewEntry(newEntry, slotName) {
        console.log('Slot: ', slotName);
        // Update client-side cache (localStorage) with new entry
        let cachedData = JSON.parse(localStorage.getItem('cachedData'+slotName)) || [];
        console.log('Cached Data:', cachedData);
        cachedData.push(newEntry);
        localStorage.setItem('cachedData'+slotName, JSON.stringify(cachedData));
        renderData(cachedData, slotName); // Update UI with new entry

    }

       // Function to render data to the UI
    function renderData(data, slotName) {
        const dataList = document.getElementById(`data-container-slot-${slotName}`);
        dataList.innerHTML = ''; // Clear previous content
        // Render data to the UI
        data.slice().reverse().slice(0, 10).forEach(entry => {
            const listItem = document.createElement('li');
            listItem.textContent = entry;
            dataList.appendChild(listItem);
        });
    }

    // Function for toggling
    function updateSlot(index) {
        const currentSlot = parseInt(slotButtons[index].dataset.slot);
        console.log('Current Slot:', currentSlot);
        //alert("Slot " + currentSlot);

        // Highlight the selected slot and dim the other slot buttons
        slotButtons.forEach((button, idx) => {
            if (idx === index) {
                button.classList.add('active');
            } else {
                button.classList.remove('active');
            }
      });

        // Hide all containers
        containerContents.forEach(container => container.style.display = 'none');

        // Show the container corresponding to the selected slot
        const containerId = `slot-${currentSlot}`;
        const selectedContainer = document.getElementById(containerId);
        if (selectedContainer) {
            selectedContainer.style.display = 'block';
            // Fetch and render data for the selected slot
            //fetchData(currentSlot);
        }
    }

    // Initial update of the slot
    updateSlot(currentSlotIndex);

    slotButtons.forEach((button, index) => {
        button.addEventListener('click', function() {
        const buttonIndex = Array.from(slotButtons).indexOf(this);
        const buttonWidth = this.offsetWidth;
        switchSlider.style.transform = `translateX(${buttonIndex * buttonWidth}px)`;
        currentSlotIndex = buttonIndex;
        updateSlot(currentSlotIndex);
        });
    });

    function generateBetId() {
        const timestamp = new Date().getTime(); // Current timestamp
        return `${timestamp} `;
    }
});

function getUsernameFromLocalStorage() {
    return localStorage.getItem("username");
}

// Function to set username in HTML content
function setUsernameInPage() {
    const username = getUsernameFromLocalStorage();
    const id=localStorage.getItem("myId");
    if (username) {
        document.getElementById('username').innerText = username;
        document.getElementById('id').innerText = id;
    } else {
        // Handle the case when username is not found in localStorage
        document.getElementById('username').innerText = "Guest";
    }
}

// Call function to set username when page loads
window.onload = function() {
    setUsernameInPage();
    // localStorage.getItem('cachedData');
    let onLoadData = JSON.parse(localStorage.getItem('cachedData1'));
    renderData(onLoadData, currentSlotIndex+1);
};

// Function to handle logout
function logout() {
    fetch('http://arrowenterprise.co.in/logout', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => {
        if (response.ok) {
            localStorage.removeItem('jwtToken');
            localStorage.removeItem('cachedData1');
            localStorage.removeItem('cachedData2');
            // Redirect to the login page after successful logout
            window.location.href = '/login?logout';
        } else{
            console.error('Logout failed');
        }
    })
    .catch(error => {
        console.error('Error during logout:', error);
    });
};

// Connect to WebSocket server
var stompClient = null;
var socket = new SockJS('/ws');
stompClient = Stomp.over(socket);
stompClient.connect({}, function(frame) {
    console.log(frame);
    stompClient.subscribe('/all/messages', function(result) {
        showMessage(JSON.parse(result.body));
    });
    stompClient.subscribe('/all/slot2', function(result2) {
        showMessage(JSON.parse(result2.body));
    });
});


/*
//The WINNER PROMPT// -- Can use Later for other things
// Function to display message in modal dialog
function showMessage(message) {
    var messageContent = document.getElementById('messageContent');
    messageContent.innerHTML = message.text;

    // Show the modal dialog
    $('#messageModal').modal('show');

    // Add congratulatory animation class to the message
    messageContent.classList.add('congratulatory');

    // Add blast unbox animation class to the modal body
    messageContent.classList.add('blast-unbox');

    // Create confetti pieces
    var confettiContainer = document.querySelector('.confetti-container');
    for (var i = 0; i < 100; i++) {
        var confettiPiece = document.createElement('div');
        confettiPiece.classList.add('confetti-piece');
        confettiPiece.style.left = Math.random() * 100 + '%';
        confettiPiece.style.backgroundColor = randomColor();
        confettiPiece.style.animationDelay = Math.random() * 2 + 's';
        confettiContainer.appendChild(confettiPiece);
    }

    // Remove confetti after animation
    setTimeout(function() {
        confettiContainer.innerHTML = ''; // Clear confetti pieces
    }, 5000); // Adjust duration as needed

    // Store the number in local storage
    localStorage.setItem('winningNumber', message.text);
}

// Function to generate random color
function randomColor() {
    var letters = '0123456789ABCDEF';
    var color = '#';
    for (var i = 0; i < 6; i++) {
        color += letters[Math.floor(Math.random() * 16)];
    }
    return color;
}*/


