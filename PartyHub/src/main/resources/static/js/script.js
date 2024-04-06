document.addEventListener('DOMContentLoaded', function() {
    // Extract the token from the URL
    const urlPath = window.location.pathname;
    const token = urlPath.substring(urlPath.lastIndexOf('/') + 1);

    const resetPasswordForm = document.getElementById('resetPasswordForm');
    const passwordInput = document.getElementById('password');
    const confirmPasswordInput = document.getElementById('confirm-password');
    const submitButton = document.getElementById('submitBtn');
    const successMessage = document.getElementById('successMessage');
    const errorMessage = document.getElementById('errorMessage');
    const errorText = document.createElement('div'); // Create a div for error messages

    // Initialize errorText properties
    errorText.style.color = 'red';
    errorText.style.display = 'none'; // Hide initially
    resetPasswordForm.insertBefore(errorText, submitButton); // Insert before the submit button

    // Function to reset styles and hide error messages
    function resetFormStyles() {
        passwordInput.style.border = '';
        confirmPasswordInput.style.border = '';
        errorText.style.display = 'none'; // Hide error text
        successMessage.style.display = 'none';
        errorMessage.style.display = 'none';
    }

    // Function to indicate error on input fields
    function indicateInputError(inputFields, message) {
        inputFields.forEach(field => {
            field.style.border = '2px solid red';
        });
        errorText.textContent = message; // Set the error message
        errorText.style.display = 'block'; // Show error text
    }

    resetPasswordForm.addEventListener('submit', function(event) {
        event.preventDefault(); // Prevents form submission

        const password = passwordInput.value;
        const confirmPassword = confirmPasswordInput.value;

        resetFormStyles(); // Reset styles on new submission

        // Validate password length
        if (password.length < 8) {
            indicateInputError([passwordInput, confirmPasswordInput], 'Password must be at least 8 characters long.');
            return; // Stop here if validation fails
        }

        // Check if the entered passwords match
        if (password !== confirmPassword) {
            indicateInputError([passwordInput, confirmPasswordInput], 'The passwords do not match.');
            return; // Stop here if validation fails
        }

        // Perform the API call for password reset
        fetch(`/reset-password/${token}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ newPassword: password }),
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    // Operation successful
                    successMessage.textContent = data.message;
                    successMessage.style.display = 'block';
                } else {
                    // Handle specific API error responses
                    errorMessage.textContent = data.message;
                    errorMessage.style.display = 'block';
                }
            })
            .catch(error => {
                errorMessage.textContent = 'An error occurred while resetting the password. Please try again.';
                errorMessage.style.display = 'block';
            });
    });
});