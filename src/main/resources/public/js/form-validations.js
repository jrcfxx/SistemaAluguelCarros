function digitsOnly(value) {
    return (value || '').replace(/\D/g, '');
}

function formatCpf(value) {
    const digits = digitsOnly(value).slice(0, 11);
    return digits
        .replace(/^(\d{3})(\d)/, '$1.$2')
        .replace(/^(\d{3})\.(\d{3})(\d)/, '$1.$2.$3')
        .replace(/\.(\d{3})(\d)/, '.$1-$2');
}

function isRepeatedDigits(value) {
    return /^(\d)\1{10}$/.test(value);
}

function calculateCpfDigit(cpf, baseLength, initialWeight) {
    let sum = 0;
    for (let i = 0; i < baseLength; i += 1) {
        sum += Number(cpf.charAt(i)) * (initialWeight - i);
    }
    const remainder = 11 - (sum % 11);
    return remainder > 9 ? 0 : remainder;
}

function isValidCpf(value) {
    const digits = digitsOnly(value);
    if (digits.length !== 11 || isRepeatedDigits(digits)) {
        return false;
    }

    const firstDigit = calculateCpfDigit(digits, 9, 10);
    const secondDigit = calculateCpfDigit(digits, 10, 11);
    return firstDigit === Number(digits.charAt(9)) && secondDigit === Number(digits.charAt(10));
}

function validateCpfField(field) {
    if (!field) {
        return true;
    }

    if (!field.value) {
        field.setCustomValidity('');
        return true;
    }

    if (!/^\d{3}\.\d{3}\.\d{3}-\d{2}$/.test(field.value)) {
        field.setCustomValidity('Informe o CPF no formato 000.000.000-00.');
        return false;
    }

    if (!isValidCpf(field.value)) {
        field.setCustomValidity('Informe um CPF válido.');
        return false;
    }

    field.setCustomValidity('');
    return true;
}

function validatePasswordConfirmation(form) {
    const confirmationFields = form.querySelectorAll('[data-password-confirm-for]');
    let allValid = true;

    confirmationFields.forEach((field) => {
        const sourceId = field.getAttribute('data-password-confirm-for');
        const source = form.querySelector(`#${sourceId}`);
        if (!source || !field.value) {
            field.setCustomValidity('');
            return;
        }

        if (field.value !== source.value) {
            field.setCustomValidity('A confirmação de senha deve ser igual à senha informada.');
            allValid = false;
            return;
        }

        field.setCustomValidity('');
    });

    return allValid;
}

function ensureErrorEl(field) {
    const wrapper = field.closest('.field') || field.parentElement;
    if (!wrapper) {
        return null;
    }

    let error = wrapper.querySelector('.field-error');
    if (!error) {
        error = document.createElement('div');
        error.className = 'field-error';
        error.setAttribute('aria-live', 'polite');
        wrapper.appendChild(error);
    }
    return error;
}

function setFieldError(field, message) {
    const error = ensureErrorEl(field);
    if (error) {
        error.textContent = message || '';
    }
}

function clearFieldError(field) {
    setFieldError(field, '');
}

function ensureFormSummary(form) {
    let summary = form.querySelector('.form-summary');
    if (!summary) {
        summary = document.createElement('div');
        summary.className = 'form-summary';
        summary.hidden = true;
        summary.innerHTML = '<p class="form-summary-title">Revise os campos abaixo</p><ul class="form-summary-list"></ul>';
        form.prepend(summary);
    }
    return summary;
}

function setFormSummary(form, items) {
    const summary = ensureFormSummary(form);
    const list = summary.querySelector('.form-summary-list');
    if (!items || items.length === 0) {
        summary.hidden = true;
        if (list) list.innerHTML = '';
        return;
    }
    if (list) {
        list.innerHTML = '';
        items.forEach((text) => {
            const li = document.createElement('li');
            li.textContent = text;
            list.appendChild(li);
        });
    }
    summary.hidden = false;
}

function setSubmitLoading(form, isLoading) {
    const submit = form.querySelector('button[type="submit"], input[type="submit"]');
    if (!submit) return;

    if (isLoading) {
        if (!submit.dataset.originalText) {
            submit.dataset.originalText = (submit.tagName === 'INPUT') ? submit.value : submit.textContent;
        }
        if (submit.tagName === 'INPUT') {
            submit.value = 'Enviando...';
        } else {
            submit.textContent = 'Enviando...';
        }
        submit.disabled = true;
        form.querySelectorAll('input, textarea, select, button').forEach((el) => {
            if (el !== submit) el.setAttribute('aria-disabled', 'true');
        });
        return;
    }

    const original = submit.dataset.originalText;
    if (original) {
        if (submit.tagName === 'INPUT') submit.value = original;
        else submit.textContent = original;
    }
    submit.disabled = false;
    form.querySelectorAll('input, textarea, select, button').forEach((el) => el.removeAttribute('aria-disabled'));
}

document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('[data-cpf-mask]').forEach((field) => {
        field.value = formatCpf(field.value);
        validateCpfField(field);
        clearFieldError(field);

        field.addEventListener('input', function () {
            field.value = formatCpf(field.value);
            if (!validateCpfField(field)) {
                setFieldError(field, field.validationMessage);
            } else {
                clearFieldError(field);
            }
        });

        field.addEventListener('blur', function () {
            field.value = formatCpf(field.value);
            if (!validateCpfField(field)) {
                setFieldError(field, field.validationMessage);
            } else {
                clearFieldError(field);
            }
        });
    });

    document.querySelectorAll('form[data-validate-form]').forEach((form) => {
        form.addEventListener('submit', function (event) {
            let valid = true;
            const issues = [];

            form.querySelectorAll('[data-cpf-mask]').forEach((field) => {
                if (!validateCpfField(field)) {
                    valid = false;
                    setFieldError(field, field.validationMessage);
                    issues.push('CPF inválido ou incompleto.');
                } else {
                    clearFieldError(field);
                }
            });

            if (!validatePasswordConfirmation(form)) {
                valid = false;
                issues.push('A confirmação de senha deve ser igual à senha informada.');
            }

            if (!form.checkValidity()) {
                event.preventDefault();
                // Erros nativos: refletir no inline quando possível
                const firstInvalid = form.querySelector(':invalid');
                if (firstInvalid) {
                    setFieldError(firstInvalid, firstInvalid.validationMessage);
                    firstInvalid.focus();
                }
                issues.push('Existem campos obrigatórios não preenchidos.');
                setFormSummary(form, issues);
                return;
            }

            if (!valid) {
                event.preventDefault();
                setFormSummary(form, issues);
                return;
            }

            setFormSummary(form, []);
            setSubmitLoading(form, true);
        });

        // Limpa erro inline ao digitar
        form.querySelectorAll('input, textarea, select').forEach((field) => {
            field.addEventListener('input', function () {
                if (field.checkValidity()) {
                    clearFieldError(field);
                }
            });
        });
    });
});
