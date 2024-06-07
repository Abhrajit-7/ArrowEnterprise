function validateSignUp() {
    var username = document.getElementById('username').value;
    var email = document.getElementById('email').value;
    var password = document.getElementById('password').value;

    if (username === '' || email === '' || password === '') {
        alert('Please fill in all fields.');
        return false;
    }

    // Add more advanced validation if needed

    return true;
}

function validateLogin() {
    var username = document.getElementById('username').value;
    var password = document.getElementById('password').value;

    if (username === '' || password === '') {
        alert('Please fill in all fields.');
        return false;
    }

    // Add more advanced validation if needed

    return true;
}
