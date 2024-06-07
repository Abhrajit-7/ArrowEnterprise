document.getElementById('login-section').addEventListener('submit', function (event) {
    event.preventDefault();

    const loginUsername = document.getElementById('loginUsername').value;
    const loginPassword = document.getElementById('loginPassword').value;


    try {
       fetch('http://arrowenterprise.co.in/api/v1/login', {
             method: 'POST',
             headers: {
               'Content-Type': 'application/json',
             },
             body: JSON.stringify({
               username: loginUsername,
               password: loginPassword,
             }),
           })
       .then(response => {
            if (response.status === 401) {
                        throw new Error('Invalid password!');
                    } else if (response.status === 204) {
                        throw new Error('User not Registered. Please Sign up First!');
                    }else if (response.status === 500) {
                        throw new Error('Internal server err');
                    }
            return response.json();
       })
       .then(data => {
             console.log('JWT Token:', data.jwttoken);
             localStorage.setItem('jwtToken', data.jwttoken);
             localStorage.setItem('myId',data.id);
             localStorage.setItem('username', data.username);
             sessionStorage.setItem('acctBal',data.accountBal);
             localStorage.setItem('role', data.role);
             console.log(data.jwttoken);
             redirectToAuthenticatedDashboard();
             })
       .catch(error => {
             console.error('Error:', error);
             alert('Error: ' + error.message);
       })
       /*.then(jwttoken => {

             // Store the JWT token in localStorage
                console.log(jwttoken);
                 //localStorage.setItem('jwttoken', jwttoken);

                 // Redirect to the authenticated dashboard page
                 redirectToAuthenticatedDashboard();

       })*/
      //localStorage.setItem('token', data.token); // Store token in localStorage
      //window.location.href = '/api/v1/userDashboard'; // Redirect to dashboard page after successful login
    } catch (error) {
      document.getElementById('message').textContent = error.message;
      console.error('error ::: ',error)
      alert('Login failed..');
    }
  });

  function redirectToAuthenticatedDashboard() {
              // Retrieve the JWT token from localStorage
              const jwtToken = localStorage.getItem('jwtToken');

              // Make a fetch request to the authenticated dashboard endpoint
              fetch('http://arrowenterprise.co.in/api/v1/userDashboard', {
                  method: 'GET',
                  headers: {
                      'Authorization': `Bearer ${jwtToken}`
                  }
              })
              .then(response => {
                  // Handle the response (e.g., redirect to the dashboard page)
                  if (response.ok) {
                      window.location.href = '/api/v1/userDashboard';
                  } else {
                      throw new Error('Authentication failed');
                  }
              })
              .catch(error => {
                  console.error('Error:', error);
                  alert('Authentication failed');
              });
          }