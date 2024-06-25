        const username = localStorage.getItem('username');
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
                <p>Full Name: ${profile.fullName || ''}</p>
                <p>Email: ${profile.email || ''}</p>
                <p>Contact No.: ${profile.phone || ''}</p>
                <p>PAN: ${profile.pan || ''}</p>
                <p>Bank Account Number: ${profile.bankAccountNumber || ''}</p>
                <p>IFSC: ${profile.ifsc || ''}</p>
                <p>Aadhaar: ${profile.aadhaar || ''}</p>
            `;
        }

        function renderEditForm(profile) {
            const profileDetails = document.getElementById('profileDetails');
            profileDetails.innerHTML = `
                <form id="editForm">
                    <label for="fullName">Full Name:</label>
                    <input type="text" id="fullName" value="${profile.fullName || ''}" required><br>
                    <label for="email">Email:</label>
                    <input type="email" id="email" value="${profile.email || ''}" required><br>
                    <label for="phone">Contact No.:</label>
                    <input type="tel" id="phone" value="${profile.phone || ''}" required><br>
                    <label for="pan">PAN:</label>
                    <input type="text" id="pan" value="${profile.pan || ''}" pattern="[A-Z]{5}[0-9]{4}[A-Z]{1}" placeholder="ABCDE1234F" required><br>
                    <label for="bankAccountNumber">Bank Account Number:</label>
                    <input type="text" id="bankAccountNumber" value="${profile.bankAccountNumber || ''}" pattern="[0-9]{9,18}" placeholder="123456789" required><br>
                    <label for="ifsc">IFSC:</label>
                    <input type="text" id="ifsc" value="${profile.ifsc || ''}" pattern="^[A-Za-z]{4}0[A-Za-z0-9]{6}$" placeholder="ABCD0123456" required><br>
                    <label for="aadhaar">Aadhaar:</label>
                    <input type="text" id="aadhaar" value="${profile.aadhaar || ''}" pattern="[0-9]{12}" placeholder="123412341234" required><br>
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

            const method = initialPostDone ? 'PUT' : 'POST';
            fetch('http://arrowenterprise.co.in/api/v1/profiles/'+username, {
                method: method,
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(profileData)
            })
            .then(response => response.json())
            .then(data => {
                profileData = data;
                initialPostDone = true;
                renderProfile(data);
            })
            .catch(error => console.error('Error:', error));
        }

        function fetchProfile() {
            fetch('http://arrowenterprise.co.in/api/v1/profiles/'+username, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            })
            .then(response => response.json())
            .then(data => {
                if (data) {
                    profileData = data;
                    initialPostDone = true;
                    renderProfile(data);
                } else {
                    profileData = {
                        fullName: '',
                        email: '',
                        phone: '',
                        pan: '',
                        bankAccountNumber: '',
                        ifsc: '',
                        aadhaar: ''
                    };
                    renderEditForm(profileData);
                    document.getElementById('editForm').addEventListener('submit', handleFormSubmit);
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
        };