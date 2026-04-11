package sistemaaluguelcarros.domain;

/**
 * Quem figura como titular do automóvel no cadastro (laboratório; sem histórico de transferências).
 */
public enum TipoProprietarioVeiculo {

    /** Frota da locadora / sistema. */
    LOCADORA("Locadora"),

    /** Cliente pessoa física cadastrado no sistema. */
    CLIENTE("Cliente"),

    /** Instituição financeira (representação acadêmica, sem entidade separada). */
    BANCO("Banco");

    private final String tituloAmigavel;

    TipoProprietarioVeiculo(String tituloAmigavel) {
        this.tituloAmigavel = tituloAmigavel;
    }

    public String getTituloAmigavel() {
        return tituloAmigavel;
    }
}
