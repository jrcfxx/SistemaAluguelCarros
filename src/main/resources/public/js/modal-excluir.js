(function() {
    var modal = document.getElementById('modal-excluir');
    if (!modal) return;

    var form = document.getElementById('form-excluir');
    var nomeEl = document.getElementById('modal-excluir-nome');
    var ultimoGatilho = null;
    var card = modal.querySelector('.modal-card');
    var firstFocusableSelector = 'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])';

    function abrir(action, nome) {
        if (nomeEl) nomeEl.textContent = nome || '';
        if (form) form.action = action || '#';
        modal.hidden = false;
        document.body.style.overflow = 'hidden';
        if (card) {
            var first = card.querySelector(firstFocusableSelector);
            if (first) first.focus();
        }
    }

    function fechar() {
        modal.hidden = true;
        document.body.style.overflow = '';
        if (form) form.action = '#';
        if (nomeEl) nomeEl.textContent = '';
        if (ultimoGatilho) ultimoGatilho.focus();
    }

    document.addEventListener('click', function(e) {
        var trigger = e.target.closest('[data-modal-excluir]');
        if (trigger) {
            e.preventDefault();
            ultimoGatilho = trigger;
            abrir(trigger.dataset.action, trigger.dataset.nome);
            return;
        }
        if (e.target.closest('.modal-excluir-fechar')) {
            fechar();
        }
        if (!modal.hidden && e.target === modal) {
            fechar();
        }
    });

    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape' && !modal.hidden) {
            fechar();
        }

        if (e.key === 'Tab' && !modal.hidden && card) {
            var focusables = card.querySelectorAll(firstFocusableSelector);
            if (!focusables || focusables.length === 0) return;
            var first = focusables[0];
            var last = focusables[focusables.length - 1];

            if (e.shiftKey && document.activeElement === first) {
                e.preventDefault();
                last.focus();
            } else if (!e.shiftKey && document.activeElement === last) {
                e.preventDefault();
                first.focus();
            }
        }
    });
})();
