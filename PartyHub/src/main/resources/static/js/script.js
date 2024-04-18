// Function to validate password length
function isPasswordValid(password) {
    return password.length >= 8;
}

// Function to validate password match
function doPasswordsMatch(password, confirmPassword) {
    return password === confirmPassword;
}

// Function to update UI based on validation results
function updateValidationUI(passwordValid, passwordsMatch) {
    const passwordInput = document.getElementById('password');
    const confirmPasswordInput = document.getElementById('confirm-password');
    const errorMessage = document.getElementById('errorMessage');

    // Reset borders and error message
    passwordInput.style.border = '';
    confirmPasswordInput.style.border = '';
    errorMessage.style.display = 'none';

    // Set borders and display error message if necessary
    if (!passwordValid) {
        passwordInput.style.border = '1px solid red';
    }
    if (!passwordsMatch) {
        confirmPasswordInput.style.border = '1px solid red';
        errorMessage.style.display = 'block';
    }
}

// Function to handle form submission
function handleFormSubmission(event) {
    event.preventDefault(); // Prevent form submission by default

    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirm-password').value;

    // Validate password length and match
    const passwordValid = isPasswordValid(password);
    const passwordsMatch = doPasswordsMatch(password, confirmPassword);

    // Update UI based on validation results
    updateValidationUI(passwordValid, passwordsMatch);

    // If both password is valid and passwords match, proceed with submission
    if (passwordValid && passwordsMatch) {
        const token = window.location.pathname.split('/').pop();
        const newPassword = password; // You can also get newPassword from a different source if needed
        const xhr = new XMLHttpRequest();
        const serverUrl1 = document.getElementById('serverUrl').value + "";
        const str = "http://localhost:8080/api/auth/reset-password/"
            // serverUrl1 + '/' + token;
        console.log(str)
        xhr.open('POST', str + token);
        xhr.setRequestHeader('Content-Type', 'application/json');
        xhr.onload = function() {
            const response = JSON.parse(xhr.responseText);
            if (xhr.status === 200 && response.success) {
                document.getElementById('successMessage').style.display = 'block';
                document.getElementById('saveButton').disabled = true;
            } else {
                console.error(response.message);
            }
        };
        xhr.send(JSON.stringify({ newPassword: newPassword }));
    }
}

// Add input event listeners to password fields
document.getElementById('password').addEventListener('input', function() {
    updateValidationUI(isPasswordValid(this.value), doPasswordsMatch(this.value, document.getElementById('confirm-password').value));
});

document.getElementById('confirm-password').addEventListener('input', function() {
    updateValidationUI(isPasswordValid(document.getElementById('password').value), doPasswordsMatch(document.getElementById('password').value, this.value));
});

// Add submit event listener to form
document.getElementById('resetPasswordForm').addEventListener('submit', handleFormSubmission);
