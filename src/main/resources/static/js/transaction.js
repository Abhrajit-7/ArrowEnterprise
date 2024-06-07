document.addEventListener("DOMContentLoaded", function() {
    const username=localStorage.getItem("username");
    fetchTransactions();
});

function fetchTransactions() {
    fetch('http://arrowenterprise.co.in/api/v1/' + username + '/transactions',{
    method: 'GET',
    headers: {
        'Authorization': `Bearer ${jwtToken}`,
        'Content-Type': 'application/json'
    },
    })
        .then(response => response.json())
        .then(transactions => displayTransactions(transactions))
        .catch(error => console.error('Error fetching transactions:', error));
}

function displayTransactions(transactions) {
    const transactionsList = document.getElementById('transactionsList');
    //transactionsList.innerHTML = ''; // Clear previous transactions

    transactions.forEach(transaction => {
        const transactionItem = document.createElement('div');
        transactionItem.classList.add('transaction-item');

        const transactionId = document.createElement('p');
        transactionId.classList.add('transaction-id');
        transactionId.textContent = `Transaction ID: ${transaction.transactionId}`;
        transactionItem.appendChild(transactionId);

        const amount = document.createElement('p');
        amount.textContent = `Amount: ${transaction.amount}`;
        transactionItem.appendChild(amount);

        const type = document.createElement('p');
        type.textContent = `Type: ${transaction.transactionType}`;
        transactionItem.appendChild(type);

        transactionsList.appendChild(transactionItem);
    });
}