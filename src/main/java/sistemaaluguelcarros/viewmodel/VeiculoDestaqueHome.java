package sistemaaluguelcarros.viewmodel;

/**
 * Card de categoria da frota exibido na home pública (conteúdo de vitrine; preços ilustrativos).
 */
public record VeiculoDestaqueHome(
        int ordem,
        String categoria,
        String descricao,
        String imagemPath,
        int passageiros,
        int bagagens,
        String cambio,
        String precoValor,
        String precoPeriodo
) {
}
