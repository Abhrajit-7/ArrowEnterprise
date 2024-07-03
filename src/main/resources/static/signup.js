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

    const usernameInput = document.getElementById('username');
    const usernameMessage = document.getElementById('usernameError');

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
        if (response.ok) {
            usernameInput.classList.remove('input-error');
            usernameInput.classList.add('input-success');
            usernameMessage.textContent = 'Username is available.';
            usernameMessage.style.color = 'green';
            //message.textContent = 'User created successfully.';
            //message.style.color = 'green';
            alert(`Hi, ${response.username}! Welcome to Arrow Enterprise :D`);
            window.location.href='/login';
        } else {
            usernameInput.classList.remove('input-success');
            usernameInput.classList.add('input-error');
            usernameMessage.textContent = 'User already present';
            usernameMessage.style.color = 'red';
            //message.textContent = '';
        }
    })
    .catch (error => {
            usernameInput.classList.remove('input-success');
            usernameInput.classList.add('input-error');
            usernameMessage.textContent = 'An error occurred: ' + error.message;
            usernameMessage.style.color = 'red';
            //message.textContent = '';
    });
  });