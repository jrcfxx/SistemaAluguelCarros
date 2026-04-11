package sistemaaluguelcarros.validation;

import sistemaaluguelcarros.domain.Cliente;

import java.util.Optional;
import java.util.regex.Pattern;

public final class ValidationRules {

    public static final int NOME_MIN_LENGTH = 3;
    public static final int NOME_MAX_LENGTH = 120;
    public static final int ENDERECO_MIN_LENGTH = 8;
    public static final int ENDERECO_MAX_LENGTH = 255;
    public static final int RG_MIN_LENGTH = 5;
    public static final int RG_MAX_LENGTH = 20;
    public static final int PROFISSAO_MIN_LENGTH = 2;
    public static final int PROFISSAO_MAX_LENGTH = 100;
    public static final int SENHA_MIN_LENGTH = 6;
    public static final int SENHA_MAX_LENGTH = 60;
    public static final int DESCRICAO_PEDIDO_MIN_LENGTH = 15;
    public static final int DESCRICAO_PEDIDO_MAX_LENGTH = 1000;

    private static final String SOMENTE_DIGITOS_REGEX = "\\D";
    private static final Pattern NOME_PATTERN = Pattern.compile("^[A-Za-zÀ-ÿ]+(?:[ '\\-][A-Za-zÀ-ÿ]+)*$");
    private static final Pattern RG_PATTERN = Pattern.compile("^[0-9A-Za-z.\\-]{5,20}$");
    private static final Pattern PROFISSAO_PATTERN = Pattern.compile("^[A-Za-zÀ-ÿ]+(?:[A-Za-zÀ-ÿ '\\-/]*[A-Za-zÀ-ÿ]+)?$");
    private static final Pattern SENHA_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d).{6,60}$");

    private ValidationRules() {
    }

    public static Optional<String> validarCliente(Cliente cliente) {
        String nome = safeTrim(cliente.getNome());
        if (nome.isEmpty()) {
            return Optional.of("Nome é obrigatório.");
        }
        if (nome.length() < NOME_MIN_LENGTH || nome.length() > NOME_MAX_LENGTH) {
            return Optional.of("O nome deve ter entre 3 e 120 caracteres.");
        }
        if (!NOME_PATTERN.matcher(nome).matches()) {
            return Optional.of("O nome deve conter apenas letras, espaços, apóstrofo ou hífen.");
        }

        String cpf = safeTrim(cliente.getCpf());
        if (cpf.isEmpty()) {
            return Optional.of("CPF é obrigatório.");
        }
        if (!isCpfValido(cpf)) {
            return Optional.of("CPF inválido. Informe um CPF válido no formato 000.000.000-00.");
        }

        String endereco = safeTrim(cliente.getEndereco());
        if (endereco.isEmpty()) {
            return Optional.of("Endereço é obrigatório.");
        }
        if (endereco.length() < ENDERECO_MIN_LENGTH || endereco.length() > ENDERECO_MAX_LENGTH) {
            return Optional.of("O endereço deve ter entre 8 e 255 caracteres.");
        }

        String rg = safeTrim(cliente.getRg());
        if (!rg.isEmpty() && !RG_PATTERN.matcher(rg).matches()) {
            return Optional.of("RG inválido. Use de 5 a 20 caracteres com letras, números, ponto ou hífen.");
        }

        String profissao = safeTrim(cliente.getProfissao());
        if (!profissao.isEmpty()) {
            if (profissao.length() < PROFISSAO_MIN_LENGTH || profissao.length() > PROFISSAO_MAX_LENGTH) {
                return Optional.of("A profissão deve ter entre 2 e 100 caracteres.");
            }
            if (!PROFISSAO_PATTERN.matcher(profissao).matches()) {
                return Optional.of("A profissão deve conter apenas letras, espaços, barra, apóstrofo ou hífen.");
            }
        }

        return Optional.empty();
    }

    public static Optional<String> validarSenhaCadastro(String senhaPlana, String confirmacaoSenha) {
        if (senhaPlana == null || senhaPlana.isBlank()) {
            return Optional.of("Senha é obrigatória.");
        }
        if (!SENHA_PATTERN.matcher(senhaPlana).matches()) {
            return Optional.of("A senha deve ter entre 6 e 60 caracteres, com ao menos uma letra e um número.");
        }
        if (!senhaPlana.equals(confirmacaoSenha)) {
            return Optional.of("A confirmação de senha não confere.");
        }
        return Optional.empty();
    }

    public static Optional<String> validarDescricaoPedido(String descricaoSolicitacao) {
        String descricao = safeTrim(descricaoSolicitacao);
        if (descricao.isEmpty()) {
            return Optional.of("Descrição da solicitação é obrigatória.");
        }
        if (descricao.length() < DESCRICAO_PEDIDO_MIN_LENGTH
                || descricao.length() > DESCRICAO_PEDIDO_MAX_LENGTH) {
            return Optional.of("A descrição da solicitação deve ter entre 15 e 1000 caracteres.");
        }
        return Optional.empty();
    }

    public static String normalizarCpf(String cpf) {
        return cpf == null ? "" : cpf.replaceAll(SOMENTE_DIGITOS_REGEX, "").trim();
    }

    public static boolean isCpfValido(String cpf) {
        String cpfNormalizado = normalizarCpf(cpf);
        if (cpfNormalizado.length() != 11 || todosDigitosIguais(cpfNormalizado)) {
            return false;
        }

        int primeiroDigito = calcularDigitoVerificador(cpfNormalizado, 9, 10);
        int segundoDigito = calcularDigitoVerificador(cpfNormalizado, 10, 11);
        return primeiroDigito == Character.getNumericValue(cpfNormalizado.charAt(9))
                && segundoDigito == Character.getNumericValue(cpfNormalizado.charAt(10));
    }

    private static boolean todosDigitosIguais(String valor) {
        char primeiro = valor.charAt(0);
        for (int i = 1; i < valor.length(); i++) {
            if (valor.charAt(i) != primeiro) {
                return false;
            }
        }
        return true;
    }

    private static int calcularDigitoVerificador(String cpf, int tamanhoBase, int pesoInicial) {
        int soma = 0;
        for (int i = 0; i < tamanhoBase; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (pesoInicial - i);
        }
        int resto = 11 - (soma % 11);
        return resto > 9 ? 0 : resto;
    }

    private static String safeTrim(String valor) {
        return valor == null ? "" : valor.trim();
    }
}
