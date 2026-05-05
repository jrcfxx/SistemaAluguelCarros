package sistemaaluguelcarros.validation;

import sistemaaluguelcarros.domain.Cliente;

import java.math.BigDecimal;
import java.time.Year;
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
    public static final int MAX_RENDIMENTOS_POR_CLIENTE = 3;
    public static final int EMPREGADOR_NOME_MIN_LENGTH = 2;
    public static final int EMPREGADOR_NOME_MAX_LENGTH = 120;
    public static final int MARCA_MODELO_MIN_LENGTH = 2;
    public static final int MARCA_MODELO_MAX_LENGTH = 80;
    public static final int PLACA_NORMALIZADA_LENGTH = 7;
    public static final int FOTO_URL_MAX_LENGTH = 400;

    private static final String SOMENTE_DIGITOS_REGEX = "\\D";
    private static final Pattern NOME_PATTERN = Pattern.compile("^[A-Za-zÀ-ÿ]+(?:[ '\\-][A-Za-zÀ-ÿ]+)*$");
    private static final Pattern RG_PATTERN = Pattern.compile("^[0-9A-Za-z.\\-]{5,20}$");
    private static final Pattern PROFISSAO_PATTERN = Pattern.compile("^[A-Za-zÀ-ÿ]+(?:[A-Za-zÀ-ÿ '\\-/]*[A-Za-zÀ-ÿ]+)?$");
    private static final Pattern SENHA_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d).{6,60}$");
    /** Placa Mercosul ou antiga, sem separadores (7 caracteres alfanuméricos). */
    private static final Pattern PLACA_BR_PATTERN = Pattern.compile("^[A-Z0-9]{7}$");
    /** Aceita URLs externas (http/https) e caminhos locais iniciados com /. */
    private static final Pattern FOTO_URL_PATTERN = Pattern.compile("^(https?://.+|/.+)$");

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

    public static Optional<String> validarNomeEmpregador(String nome) {
        String n = safeTrim(nome);
        if (n.length() < EMPREGADOR_NOME_MIN_LENGTH || n.length() > EMPREGADOR_NOME_MAX_LENGTH) {
            return Optional.of("O nome do empregador deve ter entre 2 e 120 caracteres.");
        }
        return Optional.empty();
    }

    /**
     * @param cnpjBruto texto livre; normaliza para dígitos ou {@code null} se vazio
     */
    public static Optional<String> validarCnpjOpcional(String cnpjBruto) {
        String digitos = normalizarCnpj(cnpjBruto);
        if (digitos == null) {
            return Optional.empty();
        }
        if (digitos.length() != 14) {
            return Optional.of("CNPJ deve ter 14 dígitos ou ficar em branco.");
        }
        return Optional.empty();
    }

    public static String normalizarCnpj(String cnpj) {
        if (cnpj == null) {
            return null;
        }
        String digitos = cnpj.replaceAll(SOMENTE_DIGITOS_REGEX, "");
        return digitos.isEmpty() ? null : digitos;
    }

    public static Optional<String> validarValorRendimento(BigDecimal valor) {
        if (valor == null) {
            return Optional.of("Informe o valor mensal do rendimento.");
        }
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            return Optional.of("O valor deve ser maior que zero.");
        }
        if (valor.compareTo(new BigDecimal("999999999999.99")) > 0) {
            return Optional.of("Valor acima do limite permitido.");
        }
        return Optional.empty();
    }

    /**
     * Remove separadores e padroniza em maiúsculas (ex.: {@code abc-1d23} → {@code ABC1D23}).
     */
    public static String normalizarPlaca(String placaBruta) {
        if (placaBruta == null) {
            return "";
        }
        String limpa = placaBruta.replaceAll("[^A-Za-z0-9]", "").toUpperCase();
        return limpa.length() > PLACA_NORMALIZADA_LENGTH ? limpa.substring(0, PLACA_NORMALIZADA_LENGTH) : limpa;
    }

    public static Optional<String> validarPlaca(String placaNormalizada) {
        String p = safeTrim(placaNormalizada);
        if (p.isEmpty()) {
            return Optional.of("Placa é obrigatória.");
        }
        if (p.length() != PLACA_NORMALIZADA_LENGTH || !PLACA_BR_PATTERN.matcher(p).matches()) {
            return Optional.of("Placa inválida. Informe 7 caracteres alfanuméricos (ex.: ABC1D23 ou ABC1234).");
        }
        return Optional.empty();
    }

    public static Optional<String> validarAnoVeiculo(Integer ano) {
        if (ano == null) {
            return Optional.of("Ano do veículo é obrigatório.");
        }
        int atual = Year.now().getValue();
        if (ano < 1980 || ano > atual + 1) {
            return Optional.of("Ano deve estar entre 1980 e " + (atual + 1) + ".");
        }
        return Optional.empty();
    }

    public static Optional<String> validarMarcaModeloAutomovel(String marca, String modelo) {
        String m = safeTrim(marca);
        String mo = safeTrim(modelo);
        if (m.length() < MARCA_MODELO_MIN_LENGTH || m.length() > MARCA_MODELO_MAX_LENGTH) {
            return Optional.of("A marca deve ter entre 2 e 80 caracteres.");
        }
        if (mo.length() < MARCA_MODELO_MIN_LENGTH || mo.length() > MARCA_MODELO_MAX_LENGTH) {
            return Optional.of("O modelo deve ter entre 2 e 80 caracteres.");
        }
        return Optional.empty();
    }

    public static Optional<String> validarFotoUrlOpcional(String fotoUrl) {
        String url = safeTrim(fotoUrl);
        if (url.isEmpty()) {
            return Optional.empty();
        }
        if (url.length() > FOTO_URL_MAX_LENGTH) {
            return Optional.of("A URL da foto deve ter no máximo " + FOTO_URL_MAX_LENGTH + " caracteres.");
        }
        if (!FOTO_URL_PATTERN.matcher(url).matches()) {
            return Optional.of("A URL deve começar com http://, https:// ou /caminho.");
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
