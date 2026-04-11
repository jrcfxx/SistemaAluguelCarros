package sistemaaluguelcarros.domain;

/**
 * Classificação acadêmica do contrato; define a regra de titularidade do veículo após a geração.
 */
public enum TipoContrato {

    /**
     * Locação tradicional: o veículo permanece sob titularidade da locadora (sistema).
     */
    LOCACAO_SIMPLES("Locação simples"),

    /**
     * Modelo com opção de compra: ao formalizar o contrato, a titularidade passa ao cliente locatário.
     */
    LOCACAO_COM_OPCAO_COMPRA("Locação com opção de compra"),

    /**
     * Contrato com intervenção de crédito: titularidade representada pelo banco (agente financeiro) até quitação.
     */
    CREDITO_BANCARIO("Crédito bancário (leasing/financiamento)");

    private final String tituloAmigavel;

    TipoContrato(String tituloAmigavel) {
        this.tituloAmigavel = tituloAmigavel;
    }

    public String getTituloAmigavel() {
        return tituloAmigavel;
    }
}
