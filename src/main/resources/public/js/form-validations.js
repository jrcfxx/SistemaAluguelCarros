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

document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('[data-cpf-mask]').forEach((field) => {
        field.value = formatCpf(field.value);
        validateCpfField(field);

        field.addEventListener('input', function () {
            field.value = formatCpf(field.value);
            validateCpfField(field);
        });

        field.addEventListener('blur', function () {
            field.value = formatCpf(field.value);
            validateCpfField(field);
        });
    });

    document.querySelectorAll('form[data-validate-form]').forEach((form) => {
        form.addEventListener('submit', function (event) {
            let valid = true;

            form.querySelectorAll('[data-cpf-mask]').forEach((field) => {
                if (!validateCpfField(field)) {
                    valid = false;
                }
            });

            if (!validatePasswordConfirmation(form)) {
                valid = false;
            }

            if (!valid || !form.checkValidity()) {
                event.preventDefault();
                form.reportValidity();
            }
        });
    });
});
