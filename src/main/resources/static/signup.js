// src/main/resources/static/script.js
document.getElementById('signup-section').addEventListener('submit', function (event) {
    event.preventDefault();
  
    const username = document.getElementById('username').value;
    const errorElement = document.getElementById('usernameError');
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    const parentId = document.getElementById('parentId').value;
    const role = document.getElementById('role').value;
    const usernamePattern = /^[a-zA-Z0-9!@#\$%\^\&*\)\(+=._-]+$/;

    if (!usernamePattern.test(username)) {
            errorElement.textContent = 'Invalid username. No spaces allowed';
        } else {
            errorElement.textContent = ''; // Clear any previous error message
            // Proceed with form submission or further processing
            alert('Username valid!');
        }
  
    fetch('http://arrowenterprise.co.in/api/v1/register', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        username,
        email,
        password,
        parentId,
        role
      }),
    })
    .then(response => {
        return response.json();
    })
    .then(data => {
        alert("Hi, "+ data.username+". Welcome to Arrow Enterprise :D")
        window.location.href='/login';
    })
    .catch(error => {
      console.error('Error:', error);
    });
  });
  
  document.getElementById('login-section').addEventListener('submit', function (event) {
    event.preventDefault();
  
    const loginUsername = document.getElementById('loginUsername').value;
    const loginPassword = document.getElementById('loginPassword').value;
  

    try {
       fetch('/login', {
             method: 'POST',
             headers: {
               'Content-Type': 'application/json',
             },
             body: JSON.stringify({
               username: loginUsername,
               password: loginPassword,
             }),
           })

       .then(response => response.json())
       .then(data => {
             console.log(data.message);
             window.location.href = '/userdashboard.html';
       })

      if (!response.ok) {
      throw new Error(data.message || 'Failed to login');
      }
      localStorage.setItem('token', data.token); // Store token in localStorage
      window.location.href = 'userdashboard.html'; // Redirect to dashboard page after successful login
    } catch (error) {
      document.getElementById('message').textContent = error.message;
    }

  });
  