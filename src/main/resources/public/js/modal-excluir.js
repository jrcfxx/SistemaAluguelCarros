(function() {
    var modal = document.getElementById('modal-excluir');
    if (!modal) return;

    var form = document.getElementById('form-excluir');
    var nomeEl = document.getElementById('modal-excluir-nome');
    var ultimoGatilho = null;

    function abrir(action, nome) {
        if (nomeEl) nomeEl.textContent = nome || '';
        if (form) form.action = action || '#';
        modal.hidden = false;
        document.body.style.overflow = 'hidden';
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
    });

    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape' && !modal.hidden) {
            fechar();
        }
    });
})();
