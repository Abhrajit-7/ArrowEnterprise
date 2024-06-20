function submitDepositRequest() {
        const jwtToken = localStorage.getItem('jwtToken');
        var amount = document.getElementById("amount").value;
        const username = localStorage.getItem('username');

        // Make a POST request to your Spring Boot endpoint to submit deposit request
        fetch('http://arrowenterprise.co.in/transaction/deposit', {
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
                return response.json();
            } else {
                throw new Error('Failed to submit deposit request');
            }
        })
        .then(data => {
            console.log(data);
            var options = {
                "key": "rzp_live_wmzBt6pdhvwbYB", // Replace with your Razorpay Key ID
                "amount": amount * 100, // Convert to subunits
                "currency": "INR",
                "name": "Arrow",
                "description": "Live payments",
                "order_id": data.id, // This should be the order ID received from your backend
                "handler": function (response){
                    console.log(response);
                    alert('Payment successful');
                    var acctBal=sessionStorage.getItem('acctBal');
                    var updatedBal=acctBal + amount;
                    document.getElementById('balance').textContent = 'Rs.' + updatedBal.toFixed(2);
                    sessionStorage.setItem('acctBal',updatedBal);
                    window.location.href='/redirectToDashBoard';
                    // Optionally, send the payment success response to your server
                },
                "prefill": {
                    "name": username // Optional: Prefill user's name
                },
                "notes": {
                    "address": "Razorpay Corporate Office"
                },
                "theme": {
                    "color": "#F37254"
                },
                "modal": {
                    "ondismiss": function () {
                        if (confirm("Are you sure you want to close the form?")) {
                            console.log("Checkout form closed by the user");
                        } else {
                            console.log("Complete the Payment");
                        }
                    }
                }
            };
            var rzp = new Razorpay(options);
            rzp.on("payment.failed", function (response) {
                alert("Payment failed: " + response.error.description);
                console.error(response.error);
            });
            rzp.open();
        })
        .catch(error => {
            console.error('Error:', error);
            alert("Failed to submit deposit request.");
        });
}

function preventBack() {
                history.pushState(null, null, location.href);
            }