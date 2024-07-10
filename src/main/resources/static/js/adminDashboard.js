var stompClient = null;
        var privateStompClient = null;

        var socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, function(frame) {
            console.log(frame);
            stompClient.subscribe('/all/messages', function(result) {
                show(JSON.parse(result.body));
            });
        });

        socket = new SockJS('/ws');
        privateStompClient = Stomp.over(socket);
        privateStompClient.connect({}, function(frame) {
            console.log(frame);
            privateStompClient.subscribe('/all/slot2', function(result2) {
                console.log(result2.body)
                show(JSON.parse(result2.body));
            });
        });


        function sendMessage() {
            var text = document.getElementById('text').value;
            stompClient.send("/app/application", {},
                JSON.stringify({'text':text}));
        }

        function sendMessage2() {
            var text2 = document.getElementById('text2').value;
            stompClient.send("/app/applicationSlot2", {},
                JSON.stringify({'text':text2}));
        }

        function show(message) {
            var response = document.getElementById('messages');
            var p = document.createElement('p');
            p.innerHTML= "message: "  + message.text;
            response.appendChild(p);
            // Add congratulatory animation class to the message
            p.classList.add('congratulatory');
        }

document.addEventListener('DOMContentLoaded', function() {
            // Fetch and display withdrawal transactions

            fetch('http://arrowenterprise.co.in/transaction/withdrawals')
                .then(response => response.json())
                .then(transactions => {
                    const transactionsDiv = document.getElementById('transactions');
                    transactions.forEach(transaction => {
                        const transactionElement = document.createElement('div');
                        transactionElement.classList.add('transaction');

                        const transactionHeader = document.createElement('div');
                        transactionHeader.classList.add('transaction-header');
                        transactionHeader.textContent = `Transaction ID: ${transaction.trans_id}`;
                        transactionElement.appendChild(transactionHeader);

                        const transactionRows = [
                                                { label: 'Amount:', value: `Rs. ${transaction.amount.toFixed(2)}` },
                                                { label: 'Date:', value: transaction.dateTime },
                                                { label: 'Status:', value: transaction.status },
                                                { label: 'Full Name:', value: transaction.fullName },
                                                { label: 'Email:', value: transaction.email },
                                                { label: 'PAN:', value: transaction.pan },
                                                { label: 'Bank Account:', value: transaction.bankAcctNumber },
                                                { label: 'IFSC:', value: transaction.ifscCode },
                                                { label: 'Aadhaar:', value: transaction.aadhaar }
                                            ];

                        transactionRows.forEach(row => {
                            const rowElement = document.createElement('div');
                            rowElement.classList.add('transaction-row');

                            const labelElement = document.createElement('div');
                            labelElement.classList.add('transaction-label');
                            labelElement.textContent = row.label;
                            rowElement.appendChild(labelElement);

                            const valueElement = document.createElement('div');
                            valueElement.classList.add('transaction-value');
                            valueElement.textContent = row.value;
                            rowElement.appendChild(valueElement);

                            transactionElement.appendChild(rowElement);
                        });
                        // Confirm button
                        const confirmButton = document.createElement('button');
                        confirmButton.textContent = 'Confirm';
                        confirmButton.classList.add('confirm-button');
                        confirmButton.addEventListener('click', () => confirmTransaction(parseInt(transaction.trans_id, 10), parseFloat(transaction.amount, 10), transactionElement));
                        transactionElement.appendChild(confirmButton);

                        transactionsDiv.appendChild(transactionElement);
                    });
                })
                .catch(error => {
                    console.error('Error fetching transactions:', error);
                    const transactionsDiv = document.getElementById('transactions');
                    transactionsDiv.innerHTML = '<p>Failed to fetch transactions. Please try again later.</p>';
                });

            // Handle form submission for fetching winners
            document.getElementById('winnerForm').addEventListener('submit', function(event) {
                event.preventDefault();
                const winner = document.getElementById('winner').value;
                const slot = document.getElementById('slot').value;

                fetch('http://arrowenterprise.co.in/api/v1/teer/winner', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: new URLSearchParams({
                        winner: winner,
                        slot: slot
                    })

                })
                .then(response => response.json())
                .then(winners => {
                    const winnersDiv = document.getElementById('winners');
                    winnersDiv.innerHTML = '';
                    winners.forEach(winner => {
                        const winnerElement = document.createElement('div');
                        winnerElement.classList.add('winner');

                        const winnerRows = [
                            { label: 'Username:', value: winner.username },
                            { label: 'Total Deposit:', value: `Rs. ${winner.totalInvested.toFixed(2)}` },
                            { label: 'Selected Numbers:', value: winner.selectedNumbers }
                        ];

                        winnerRows.forEach(row => {
                            const rowElement = document.createElement('div');
                            rowElement.classList.add('winner-row');

                            const labelElement = document.createElement('div');
                            labelElement.classList.add('winner-label');
                            labelElement.textContent = row.label;
                            rowElement.appendChild(labelElement);

                            const valueElement = document.createElement('div');
                            valueElement.classList.add('winner-value');
                            valueElement.textContent = row.value;
                            rowElement.appendChild(valueElement);

                            winnerElement.appendChild(rowElement);
                        });

                        winnersDiv.appendChild(winnerElement);
                    });
                })
                .catch(error => {
                    console.error('Error fetching winners:', error);
                    const winnersDiv = document.getElementById('winners');
                    winnersDiv.innerHTML = '<p>Failed to fetch winners. Please try again later.</p>';
                });
            });

            document.getElementById('refresh-button').addEventListener('click', function() {
                        window.location.reload(true);
        });
});
function confirmTransaction(transactionId, amount, transactionElement) {
    const confirmButton = transactionElement.querySelector('.confirm-button');
    confirmButton.disabled = true;
    confirmButton.textContent = 'Loading...';
    console.log(transactionId);
    console.log(amount);
    fetch('http://arrowenterprise.co.in/transaction/updateStatus', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            trans_id: transactionId,
            amount: amount
        })
    })
    .then(response =>
    {
        if(response.ok){
        // Assuming the backend returns a success message on confirmation
        const statusRow = document.querySelector('.transaction-row .transaction-label[value="Status:"] + .transaction-value');
        //const statusRow = transactionRows.find(row => row.label === 'Status:');
        if (statusRow) {
            // Update the value of 'Status:' to 'PAID'
            statusRow.textContent = 'SUCCESS';
        }
        confirmButton.textContent = 'PAID';
        confirmButton.classList.add('confirmed');
        }else{
            //return response.text().then(text => { throw new Error(text); });
            // Handle non-OK responses
            console.error('Error confirming transaction:', response.statusText);
        }
    })
    .catch(error => {
        console.error('Error confirming transaction:', error);
        alert('Error confirming the transaction. Please try again later.');
        confirmButton.textContent = 'Confirm';
        confirmButton.disabled = false;
    });
}
