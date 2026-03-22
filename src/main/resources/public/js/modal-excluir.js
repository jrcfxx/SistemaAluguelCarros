(function() {
    var modal = document.getElementById('modal-excluir');
    if (!modal) return;

    var form = document.getElementById('form-excluir');
    var nomeEl = document.getElementById('modal-excluir-nome');

    function abrir(action, nome) {
        if (nomeEl) nomeEl.textContent = nome || '';
        if (form) form.action = action || '#';
        modal.hidden = false;
        document.body.style.overflow = 'hidden';
    }

    function fechar() {
        modal.hidden = true;
        document.body.style.overflow = '';
    }

    document.addEventListener('click', function(e) {
        var trigger = e.target.closest('[data-modal-excluir]');
        if (trigger) {
            e.preventDefault();
            abrir(trigger.dataset.action, trigger.dataset.nome);
        }
        if (e.target.closest('.modal-excluir-fechar')) {
            fechar();
        }
    });
})();
