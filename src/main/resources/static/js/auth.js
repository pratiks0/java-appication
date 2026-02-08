// auth.js - JWT Authentication Handler with Role-Based UI Controls

(function() {
    'use strict';

    // Check if user is authenticated
    function isAuthenticated() {
        const token = localStorage.getItem('token');
        return token !== null && token !== '';
    }

    // Get JWT token
    function getToken() {
        return localStorage.getItem('token');
    }

    // Get current user info
    function getCurrentUser() {
        return {
            token: localStorage.getItem('token'),
            username: localStorage.getItem('username'),
            userId: localStorage.getItem('userId'),
            roles: JSON.parse(localStorage.getItem('roles') || '[]')
        };
    }

    // Check if user has specific role
    function hasRole(role) {
        const roles = JSON.parse(localStorage.getItem('roles') || '[]');
        return roles.includes(role);
    }

    // Check if user is admin
    function isAdmin() {
        return hasRole('ADMIN');
    }

    // Logout function
    function logout() {
        localStorage.removeItem('token');
        localStorage.removeItem('username');
        localStorage.removeItem('userId');
        localStorage.removeItem('roles');
        window.location.href = '/login';
    }

    // Fetch with authentication
    async function authenticatedFetch(url, options = {}) {
        const token = getToken();
        
        if (!token) {
            window.location.href = '/login';
            return;
        }

        // Add Authorization header
        const headers = {
            ...options.headers,
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        };

        try {
            const response = await fetch(url, {
                ...options,
                headers
            });

            // If unauthorized, redirect to login
            if (response.status === 401 || response.status === 403) {
                // Check if it's a permission issue
                if (response.status === 403) {
                    alert('Access Denied: You do not have permission to perform this action.');
                } else {
                    logout();
                }
                return;
            }

            return response;
        } catch (error) {
            console.error('Fetch error:', error);
            throw error;
        }
    }

    // Check authentication on page load
    function checkAuth() {
        const publicPages = ['/login', '/register'];
        const currentPath = window.location.pathname;

        // If on public page, allow access
        if (publicPages.includes(currentPath)) {
            // If already authenticated and on login/register, redirect to students
            if (isAuthenticated()) {
                window.location.href = '/students';
            }
            return;
        }

        // For protected pages, check if authenticated
        if (!isAuthenticated()) {
            window.location.href = '/login';
            return;
        }

        // Display user info and control UI based on role
        displayUserInfo();
        controlUIByRole();
    }

    // Display user info in UI
    function displayUserInfo() {
        const user = getCurrentUser();
        
        // Add user info to navbar (if navbar exists)
        const navbarUser = document.getElementById('navbar-user');
        if (navbarUser && user.username) {
            const roleDisplay = isAdmin() ? '<span style="color: #ffd700;">👑 ADMIN</span>' : '<span>👤 USER</span>';
            navbarUser.innerHTML = `
                <span style="margin-right: 10px;">${roleDisplay}</span>
                <span>Welcome, <strong>${user.username}</strong></span>
                <button onclick="auth.logout()" style="margin-left: 15px; padding: 8px 15px; 
                        background: #dc3545; color: white; border: none; border-radius: 5px; 
                        cursor: pointer;">
                    Logout
                </button>
            `;
        }
    }

    // Control UI elements based on user role
    function controlUIByRole() {
        const currentPath = window.location.pathname;
        
        // If user is not admin, hide/disable certain UI elements
        if (!isAdmin()) {
            // On departments page
            if (currentPath === '/departments') {
                hideAdminButtons();
                showAccessDeniedMessage('departments');
            }
            
            // On courses page
            if (currentPath === '/courses') {
                hideAdminButtons();
                showAccessDeniedMessage('courses');
            }
        }
    }

    // Hide admin-only buttons (Add, Edit, Delete)
    function hideAdminButtons() {
        // Hide "Add New" button
        const addButtons = document.querySelectorAll('a[href*="/form"], a[href*="/new"]');
        addButtons.forEach(btn => {
            if (btn.textContent.includes('Add') || btn.textContent.includes('New')) {
                btn.style.display = 'none';
            }
        });

        // Hide Edit and Delete buttons
        const editButtons = document.querySelectorAll('a[href*="/edit"]');
        editButtons.forEach(btn => btn.style.display = 'none');

        const deleteButtons = document.querySelectorAll('a[href*="/delete"]');
        deleteButtons.forEach(btn => btn.style.display = 'none');

        // Also hide action column headers if they exist
        const actionHeaders = document.querySelectorAll('th');
        actionHeaders.forEach(th => {
            if (th.textContent.trim().toLowerCase() === 'actions') {
                th.style.display = 'none';
            }
        });

        // Hide action columns in table rows
        const actionCells = document.querySelectorAll('td:last-child');
        actionCells.forEach(cell => {
            const hasActions = cell.querySelector('a[href*="/edit"], a[href*="/delete"]');
            if (hasActions) {
                cell.style.display = 'none';
            }
        });
    }

    // Show access denied message
    function showAccessDeniedMessage(resource) {
        const container = document.querySelector('.container, .main-content');
        if (container) {
            const existingAlert = document.getElementById('admin-only-alert');
            if (!existingAlert) {
                const alert = document.createElement('div');
                alert.id = 'admin-only-alert';
                alert.style.cssText = `
                    background-color: #fff3cd;
                    border: 1px solid #ffc107;
                    color: #856404;
                    padding: 15px;
                    border-radius: 5px;
                    margin-bottom: 20px;
                    display: flex;
                    align-items: center;
                    gap: 10px;
                `;
                alert.innerHTML = `
                    <span style="font-size: 24px;">🔒</span>
                    <div>
                        <strong>View-Only Mode</strong><br>
                        You can view ${resource}, but only administrators can add, edit, or delete them.
                    </div>
                `;
                
                const header = container.querySelector('.header, h2');
                if (header) {
                    header.parentNode.insertBefore(alert, header.nextSibling);
                } else {
                    container.insertBefore(alert, container.firstChild);
                }
            }
        }
    }

    // Initialize auth check when DOM is ready
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', checkAuth);
    } else {
        checkAuth();
    }

    // Expose auth functions globally
    window.auth = {
        isAuthenticated,
        getToken,
        getCurrentUser,
        hasRole,
        isAdmin,
        logout,
        authenticatedFetch
    };
})();