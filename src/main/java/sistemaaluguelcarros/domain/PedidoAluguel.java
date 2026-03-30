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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "pedido_aluguel")
public class PedidoAluguel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Cliente é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @NotBlank(message = "Descrição da solicitação é obrigatória")
    @Column(name = "descricao_solicitacao", nullable = false, length = 1000)
    private String descricaoSolicitacao;

    @NotNull(message = "Status é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusPedido status;

    @Column(name = "data_solicitacao", nullable = false)
    private LocalDateTime dataSolicitacao;

    @Column(name = "ultima_atualizacao", nullable = false)
    private LocalDateTime ultimaAtualizacao;

    public PedidoAluguel() {
    }

    public PedidoAluguel(Cliente cliente, String descricaoSolicitacao) {
        this.cliente = cliente;
        this.descricaoSolicitacao = descricaoSolicitacao;
    }

    @PrePersist
    void prePersist() {
        LocalDateTime agora = LocalDateTime.now();
        if (status == null) {
            status = StatusPedido.PENDENTE;
        }
        if (dataSolicitacao == null) {
            dataSolicitacao = agora;
        }
        if (ultimaAtualizacao == null) {
            ultimaAtualizacao = agora;
        }
    }

    @PreUpdate
    void preUpdate() {
        ultimaAtualizacao = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public String getDescricaoSolicitacao() {
        return descricaoSolicitacao;
    }

    public void setDescricaoSolicitacao(String descricaoSolicitacao) {
        this.descricaoSolicitacao = descricaoSolicitacao;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public void setStatus(StatusPedido status) {
        this.status = status;
    }

    public LocalDateTime getDataSolicitacao() {
        return dataSolicitacao;
    }

    public void setDataSolicitacao(LocalDateTime dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }

    public LocalDateTime getUltimaAtualizacao() {
        return ultimaAtualizacao;
    }

    public void setUltimaAtualizacao(LocalDateTime ultimaAtualizacao) {
        this.ultimaAtualizacao = ultimaAtualizacao;
    }
}
