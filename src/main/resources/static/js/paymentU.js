document.getElementById('paymentForm').addEventListener('submit', async function(event) {
            event.preventDefault();

            const formData = {
                amount: document.getElementById('amount').value,
                productInfo: document.getElementById('productInfo').value,
                firstname: document.getElementById('firstname').value,
                email: document.getElementById('email').value,
                phone: document.getElementById('phone').value
            };

            try {
                const response = await fetch('http://localhost:8081/payment/initiate', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(formData)
                });

                if (response.ok) {
                    const responseData = await response.json();
                    redirectToPayU(responseData);
                } else {
                    console.error('Failed to initiate payment');
                }
            } catch (error) {
                console.error('Error:', error);
            }
        });

        function redirectToPayU(data) {
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = 'https://test.payu.in/_payment';
            //form.action = data.action;

            for (const key in data) {
                if (data.hasOwnProperty(key)) {
                    const input = document.createElement('input');
                    input.type = 'hidden';
                    input.name = key;
                    input.value = data[key];
                    form.appendChild(input);
                }
            }

            document.body.appendChild(form);
            form.submit();
        }