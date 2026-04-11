package sistemaaluguelcarros.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "contrato")
public class Contrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false, unique = true)
    private PedidoAluguel pedido;

    @NotNull
    @Column(name = "data_geracao", nullable = false)
    private LocalDateTime dataGeracao;

    @NotBlank
    @Column(name = "numero_contrato", nullable = false, unique = true, length = 120)
    private String numeroContrato;

    @NotBlank
    @Lob
    @Column(name = "termos", nullable = false)
    private String termos;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_contrato", nullable = false, length = 40)
    private TipoContrato tipoContrato = TipoContrato.LOCACAO_SIMPLES;

    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;

    public Contrato() {
    }

    public Contrato(
            PedidoAluguel pedido,
            String numeroContrato,
            String termos,
            LocalDateTime dataGeracao,
            TipoContrato tipoContrato
    ) {
        this.pedido = pedido;
        this.numeroContrato = numeroContrato;
        this.termos = termos;
        this.dataGeracao = dataGeracao;
        this.tipoContrato = tipoContrato != null ? tipoContrato : TipoContrato.LOCACAO_SIMPLES;
    }

    @PrePersist
    void prePersist() {
        if (dataGeracao == null) {
            dataGeracao = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PedidoAluguel getPedido() {
        return pedido;
    }

    public void setPedido(PedidoAluguel pedido) {
        this.pedido = pedido;
    }

    public LocalDateTime getDataGeracao() {
        return dataGeracao;
    }

    public void setDataGeracao(LocalDateTime dataGeracao) {
        this.dataGeracao = dataGeracao;
    }

    public String getNumeroContrato() {
        return numeroContrato;
    }

    public void setNumeroContrato(String numeroContrato) {
        this.numeroContrato = numeroContrato;
    }

    public String getTermos() {
        return termos;
    }

    public void setTermos(String termos) {
        this.termos = termos;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public TipoContrato getTipoContrato() {
        return tipoContrato;
    }

    public void setTipoContrato(TipoContrato tipoContrato) {
        this.tipoContrato = tipoContrato;
    }
}
