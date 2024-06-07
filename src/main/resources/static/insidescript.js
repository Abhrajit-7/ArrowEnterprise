const username = "USER1";
const balance = 0;
const numbers = Array.from({ length: 99 }, (_, i) => i + 1);
//const globalList = ["User1"];

// Update UI with user-specific data
document.getElementById("username").innerText = username;
document.getElementById("balance").innerText = balance.toFixed(2);

// Display numbers 1-10
/*const buttonsContainer = document.getElementById("buttonsContainer");
numbers.forEach(number => {
    const button = document.createElement("button");
    button.innerText = `Number ${number}`;
    button.addEventListener("click", () => alert(`Button ${number} selected!`));
    buttonsContainer.appendChild(button);
});*/

// Display numbers in the select dropdown
const numberSelect = document.getElementById("numberSelect");
numbers.forEach(number => {
    const option = document.createElement("option");
    option.value = number;
    option.innerText = `Number ${number}`;
    numberSelect.appendChild(option);
});



// Display global list
const globalListElement = document.getElementById("${number}");
globalList.forEach(user => {
    const li = document.createElement("li");
    li.innerText = user;
    globalListElement.appendChild(li);
});




// Function to handle number submission
function submitNumber() {
    const selectedNumber = numberSelect.value;


    // Create a FormData object to send data in the request body
    const formData = new FormData();
    formData.append('selectedNumber', selectedNumber);

    // Send a POST request to the backend
    fetch('http://localhost:8081/teer/numbers/submitNumber', {
        method: 'POST',
        body: formData,
    })
    .then(response => response.json())
    .then(data => {
        alert(`Number ${selectedNumber} submitted successfully!`);
    })
    .catch(error => {
        console.error("Error submitting number:", error);
        alert("An error occurred while submitting the number.");
    });
}


function deposit() {
    const amountInput = document.getElementById('amount');
    const amount = parseFloat(amountInput.value);

    if (isNaN(amount) || amount <= 0) {
        alert('Please enter a valid positive amount.');
        return;
    }

    balance += amount;
    updateBalance();
    amountInput.value = '';
}

function updateBalance() {
    const balanceElement = document.getElementById('balance');
    balanceElement.innerText = balance.toFixed(2);
}



// Toggle navigation menu visibility on hamburger menu click


document.body.addEventListener('click', (event) => {
    if ( !hamburger-lines.contains(event.target)) {
        hamburger-lines.classList.remove('show');
    }
});