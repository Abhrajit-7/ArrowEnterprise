document.getElementById('login-section').addEventListener('submit', function(event) {
    event.preventDefault(); // Prevent default form submission behavior

    // Get username and password input values
    const username = document.getElementById('loginUsername').value;
    const password = document.getElementById('loginPassword').value;

    // Call loginAndRedirect function with username, password, and redirect URL
    loginAndRedirect(username, password, '/numberselect.html');
});

function loginAndRedirect(loginUsername, loginPassword, redirectUrl) {
    fetch('http://localhost:8081/api/v1/login/', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ username: loginUsername, password: loginPassword })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.json(); // Parse response as JSON
    })
    .then(data => {
        // Store token in local storage
        localStorage.setItem('token', data.jwttoken);
        // Redirect to specified page
        window.location.href = redirectUrl;
    })
    .catch(error => {
        console.error('There was a problem with the fetch operation:', error);
    });
}

// Check if token exists in local storage
const token = localStorage.getItem('token');
if (token) {
    // Set token as authorization header for subsequent requests
    const headers = new Headers();
    headers.append('Authorization', `Bearer ${token}`);

    // Example: Fetch data from a protected endpoint
    fetch('http://localhost:8081/api/v1/userDashboard', {
        method: 'GET',
        headers: headers
    })
    .then(response => response.json())
    .then(data => console.log(data))
    .catch(error => console.error('Error fetching data:', error));
} else {
    console.error('Token not found in local storage');
}
