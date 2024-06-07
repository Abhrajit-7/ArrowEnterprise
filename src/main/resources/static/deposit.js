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
                // If request is successful, redirect user to Razorpay payment page
                return response.json();
            } else {
                throw new Error('Failed to submit deposit request');
            }
        })

        .then(data => {
                    console.log(data);
                    var orderRequest = {
                        key: "rzp_test_jCudEyVaIv2x0H",
                        "amount": data.amount,
                        "currency": "INR",
                        "name": "Arrow",
                        "description": "Test payment",
                        "order_id": data.id,
                         config: {
                            display: {
                              blocks: {
                                banks: {
                                  name: 'Most Used Methods',
                                  instruments: [
                                    {
                                      method: 'wallet',
                                      wallets: ['freecharge']
                                    },
                                    {
                                        method: 'app',
                                        app: 'cred'
                                    },
                                    ],
                                },
                              },
                              sequence: ['block.banks'],
                              preferences: {
                                show_default_blocks: false,
                              },
                            },

                        },
                        "handler": function (response){
                            console.log(response);
                            alert('Payment successful');
                        },
                        "modal": {
                              "ondismiss": function () {
                                if (confirm("Are you sure, you want to close the form?")) {
                                  txt = "You pressed OK!";
                                  console.log("Checkout form closed by the user");
                                } else {
                                  txt = "You pressed Cancel!";
                                  console.log("Complete the Payment")
                                }
                              }
                        }
                    };
                    var rzp = new Razorpay(orderRequest);
                    rzp.on("payment.failed", function (response) {
                    alert(response.error.code);
                    alert(response.error.description);
                    alert(response.error.source);
                    alert(response.error.step);
                    alert(response.error.reason);
                    alert(response.error.metadata.order_id);
                    alert(response.error.metadata.payment_id);
          });
                    rzp.open();
        })

        /*.then(paymentPageUrl => {
            window.location.href = paymentPageUrl;
        })*/
        .catch(error => {
            console.error('Error:', error);
            // Handle error - show error message to user or perform other actions
        });
    }