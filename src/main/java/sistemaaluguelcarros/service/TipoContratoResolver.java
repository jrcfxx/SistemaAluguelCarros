package sistemaaluguelcarros.service;

import sistemaaluguelcarros.domain.TipoContrato;

/**
 * Converte parâmetros de formulário em {@link TipoContrato} com valor padrão seguro.
 */
public final class TipoContratoResolver {

    private TipoContratoResolver() {
    }

    public static TipoContrato resolver(String valorBruto) {
        if (valorBruto == null || valorBruto.isBlank()) {
            return TipoContrato.LOCACAO_SIMPLES;
        }
        try {
            return TipoContrato.valueOf(valorBruto.trim());
        } catch (IllegalArgumentException ex) {
            return TipoContrato.LOCACAO_SIMPLES;
        }
    }
}
