/**
 * ProblemHub - Main Client-Side Scripts
 */

document.addEventListener('DOMContentLoaded', function () {
    // Auto-dismiss alerts after 5 seconds
    const alerts = document.querySelectorAll('.alert-dismissible');
    alerts.forEach(function (alert) {
        setTimeout(function () {
            const bsAlert = bootstrap.Alert.getOrCreateInstance(alert);
            if (bsAlert) {
                bsAlert.close();
            }
        }, 6000);
    });

    // Copy Share Link Helper
    const shareBtns = document.querySelectorAll('.btn-share-link');
    shareBtns.forEach(function (btn) {
        btn.addEventListener('click', function () {
            const url = window.location.href;
            navigator.clipboard.writeText(url).then(function () {
                const originalHtml = btn.innerHTML;
                btn.innerHTML = '<i class="bi bi-check2"></i> Copied!';
                setTimeout(function () {
                    btn.innerHTML = originalHtml;
                }, 2000);
            }).catch(function () {
                prompt("Copy link to share:", url);
            });
        });
    });

    // Confirmation for destructive or moderation actions
    const confirmButtons = document.querySelectorAll('[data-confirm]');
    confirmButtons.forEach(function (btn) {
        btn.addEventListener('click', function (e) {
            const message = btn.getAttribute('data-confirm') || 'Are you sure you want to perform this action?';
            if (!confirm(message)) {
                e.preventDefault();
            }
        });
    });
});
