// Authentication handling

class AuthManager {
    constructor() {
        this.checkAuthStatus();
        this.initEventListeners();
    }

    initEventListeners() {
        // Tab switching
        const tabs = document.querySelectorAll('.auth-tab');
        tabs.forEach(tab => {
            tab.addEventListener('click', () => this.switchTab(tab.dataset.tab));
        });

        // Login form
        const loginForm = document.getElementById('login-form-element');
        if (loginForm) {
            loginForm.addEventListener('submit', (e) => this.handleLogin(e));
        }

        // Register form
        const registerForm = document.getElementById('register-form-element');
        if (registerForm) {
            registerForm.addEventListener('submit', (e) => this.handleRegister(e));
        }

        // Logout buttons
        const logoutBtns = document.querySelectorAll('#logout-btn, #dashboard-logout');
        logoutBtns.forEach(btn => {
            if (btn) btn.addEventListener('click', (e) => this.handleLogout(e));
        });
    }

    switchTab(tabName) {
        // Update tabs
        document.querySelectorAll('.auth-tab').forEach(tab => {
            tab.classList.toggle('active', tab.dataset.tab === tabName);
        });

        // Update forms
        document.getElementById('login-form').classList.toggle('active', tabName === 'login');
        document.getElementById('register-form').classList.toggle('active', tabName === 'register');

        // Clear messages
        document.getElementById('login-message').innerHTML = '';
        document.getElementById('register-message').innerHTML = '';
    }

    async handleLogin(e) {
        e.preventDefault();
        const username = document.getElementById('login-username').value;
        const password = document.getElementById('login-password').value;
        const messageDiv = document.getElementById('login-message');

        messageDiv.innerHTML = '<span style="color:#00ff00;">⏳ Logging in...</span>';

        try {
            const response = await fetch('/api/auth/login', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({username, password})
            });

            const data = await response.json();

            if (data.success) {
                messageDiv.innerHTML = '<span class="success">✅ Login successful! Redirecting...</span>';
                setTimeout(() => {
                    window.location.href = '/dashboard.html';
                }, 1000);
            } else {
                messageDiv.innerHTML = '<span class="error">❌ ' + (data.error || 'Login failed') + '</span>';
            }
        } catch (error) {
            messageDiv.innerHTML = '<span class="error">❌ Connection error</span>';
        }
    }

    async handleRegister(e) {
        e.preventDefault();
        const username = document.getElementById('register-username').value;
        const email = document.getElementById('register-email').value;
        const password = document.getElementById('register-password').value;
        const messageDiv = document.getElementById('register-message');

        if (password.length < 6) {
            messageDiv.innerHTML = '<span class="error">❌ Password must be at least 6 characters</span>';
            return;
        }

        messageDiv.innerHTML = '<span style="color:#00ff00;">⏳ Creating account...</span>';

        try {
            const response = await fetch('/api/auth/register', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({username, email, password})
            });

            const data = await response.json();

            if (data.success) {
                messageDiv.innerHTML = '<span class="success">✅ Registration successful! Redirecting to login...</span>';
                setTimeout(() => {
                    this.switchTab('login');
                    document.getElementById('login-username').value = username;
                    document.getElementById('register-username').value = '';
                    document.getElementById('register-email').value = '';
                    document.getElementById('register-password').value = '';
                }, 1500);
            } else {
                messageDiv.innerHTML = '<span class="error">❌ ' + (data.error || 'Registration failed') + '</span>';
            }
        } catch (error) {
            messageDiv.innerHTML = '<span class="error">❌ Connection error</span>';
        }
    }

    async handleLogout(e) {
        e.preventDefault();

        try {
            await fetch('/api/logout', {method: 'POST'});
            window.location.href = '/login.html';
        } catch (error) {
            window.location.href = '/login.html';
        }
    }

    async checkAuthStatus() {
        try {
            const response = await fetch('/api/auth/check');
            const data = await response.json();

            if (!data.authenticated && window.location.pathname === '/dashboard.html') {
                window.location.href = '/login.html';
            }

            if (data.authenticated && document.getElementById('username-display')) {
                document.getElementById('username-display').textContent = data.username;
                this.loadUserDetails();
            }
        } catch (error) {
            if (window.location.pathname === '/dashboard.html') {
                window.location.href = '/login.html';
            }
        }
    }

    async loadUserDetails() {
        try {
            const response = await fetch('/api/auth/check');
            const data = await response.json();

            if (data.authenticated && document.getElementById('dashboard-username')) {
                document.getElementById('dashboard-username').innerHTML = `👤 Username: ${data.username}`;
            }
        } catch (error) {}
    }
}

// Initialize when DOM is ready
document.addEventListener('DOMContentLoaded', () => {
    window.authManager = new AuthManager();
});