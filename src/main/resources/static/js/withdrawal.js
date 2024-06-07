function submitDepositRequest() {
        const jwtToken = localStorage.getItem('jwtToken');
        var amount = document.getElementById("amount").value;
        const username = localStorage.getItem('username');
        // Make a POST request to your Spring Boot endpoint to submit withdraw request
        fetch('http://arrowenterprise.co.in/transaction/withdrawPage', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${jwtToken}`
            },
            body: JSON.stringify({
            amount: amount,
            username: username
            })
        })
        .then(response => {
            if (response.ok) {
                // If request is successful, redirect user to Razorpay payment page
                return response.json();
            } else {
                throw new Error('Failed to submit deposit request');
            }
        })
        .then(data => {
                    alert( "Request submitted: \n" + data.fullName
                                                  + ","
                                                  + data.bankAcctNumber
                                                  + ","
                                                  + data.ifscCode
                                                  + ","
                                                  + data.amount );
                    console.log(data);
        })
        .catch(error => {
            console.error('Error:', error);
            // Handle error - show error message to user or perform other actions
        });
    }