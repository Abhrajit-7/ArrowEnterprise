const username=localStorage.getItem('username');
    // Initial profile data (empty)
    let profileData = {
        fullName: '',
        email: '',
        phone: '',
        pan: '',
        bankAccountNumber: '',
        ifsc: '',
        aadhaar: ''
    };
    let initialPostDone = false;

        function renderProfile(profile) {
            const profileDetails = document.getElementById('profileDetails');
            profileDetails.innerHTML = `
                <h2>Profile</h2>
                <p>Full Name<span class="required"></span>: ${profile.fullName}</p>
                <p>Email<span class="required"></span>: ${profile.email}</p>
                <p>Contact No.<span class="required"></span>: ${profile.phone}</p>
                <p>PAN<span class="required"></span>: ${profile.pan}</p>
                <p>Bank Account Number<span class="required"></span>: ${profile.bankAccountNumber}</p>
                <p>IFSC<span class="required"></span>: ${profile.ifsc}</p>
                <p>Aadhaar<span class="required"></span>: ${profile.aadhaar}</p>
            `;
        }

        function renderEditForm(profile) {
            const profileDetails = document.getElementById('profileDetails');
            profileDetails.innerHTML = `
                <form id="editForm">
                    <label for="fullName">Full Name<span class="required"></span>:</label>
                    <input type="text" id="fullName" value="${profile.fullName}" required><br>
                    <label for="email">Email<span class="required"></span>:</label>
                    <input type="email" id="email" value="${profile.email}" required><br>
                    <label for="phone">Contact No.<span class="required"></span>:</label>
                    <input type="tel" id="phone" value="${profile.phone}" required><br>
                    <label for="pan">PAN<span class="required"></span>:</label>
                    <input type="text" id="pan" value="${profile.pan}" required><br>
                    <label for="bankAccountNumber">Bank Account Number<span class="required"></span>:</label>
                    <input type="text" id="bankAccountNumber" value="${profile.bankAccountNumber}" required><br>
                    <label for="ifsc">IFSC<span class="required"></span>:</label>
                    <input type="text" id="ifsc" value="${profile.ifsc}" required><br>
                    <label for="aadhaar">Aadhaar<span class="required"></span>:</label>
                    <input type="text" id="aadhaar" value="${profile.aadhaar}" required><br>

                    <input type="hidden" id="username" value="${profile.username}">
                    <button type="submit">Save</button>
                </form>
            `;
        }

        function handleFormSubmit(event) {
            event.preventDefault();
            profileData = {
                fullName: document.getElementById('fullName').value,
                email: document.getElementById('email').value,
                phone: document.getElementById('phone').value,
                pan: document.getElementById('pan').value,
                bankAccountNumber: document.getElementById('bankAccountNumber').value,
                ifsc: document.getElementById('ifsc').value,
                aadhaar: document.getElementById('aadhaar').value
            };

            const method = initialPostDone ? 'PUT' : 'POST'; // Determine the HTTP method
            const username=localStorage.getItem('username');
            fetch('http://arrowenterprise.co.in/api/v1/profiles/' + username, {
                method: method,
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(profileData)
            })
            .then(response => response.json())
            .then(data => {
                profileData = data;
                initialPostDone = true; // Set the flag to true after initial POST request
                renderProfile(data);
            })
            .catch(error => console.error('Error:', error));
        }

        function fetchProfile() {
            fetch('http://arrowenterprise.co.in/api/v1/profiles/' + username,
            {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
                }
            })
            .then(response => response.json())
            .then(data => {
                if (data) {
                    profileData = data;
                    if(data!==null)
                    {
                        initialPostDone = true;
                        renderProfile(data);
                    }
                    else{profileData = {
                            fullName: '',
                            email: '',
                            phone: '',
                            pan: '',
                            bankAccountNumber: '',
                            ifsc: '',
                            aadhaar: ''
                        };
                        }
                }
            })
            .catch(error => console.error('Error:', error));
        }

        document.getElementById('editButton').addEventListener('click', function() {
            renderEditForm(profileData);
            document.getElementById('editForm').addEventListener('submit', handleFormSubmit);
        });
        window.onload = function() {
        fetchProfile();
    }