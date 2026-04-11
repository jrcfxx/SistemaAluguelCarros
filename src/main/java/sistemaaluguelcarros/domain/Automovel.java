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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(
        name = "automovel",
        uniqueConstraints = @UniqueConstraint(columnNames = "placa_normalizada")
)
public class Automovel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Placa sem separadores, em maiúsculas (ex.: ABC1D23).
     */
    @NotBlank
    @Column(name = "placa_normalizada", nullable = false, length = 10)
    private String placaNormalizada;

    @NotBlank
    @Column(nullable = false, length = 80)
    private String marca;

    @NotBlank
    @Column(nullable = false, length = 80)
    private String modelo;

    @NotNull
    @Min(1980)
    @Max(2100)
    @Column(nullable = false)
    private Integer ano;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_proprietario", nullable = false, length = 30)
    private TipoProprietarioVeiculo tipoProprietario = TipoProprietarioVeiculo.LOCADORA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proprietario_cliente_id")
    private Cliente proprietarioCliente;

    public Automovel() {
    }

    public Automovel(String placaNormalizada, String marca, String modelo, Integer ano) {
        this.placaNormalizada = placaNormalizada;
        this.marca = marca;
        this.modelo = modelo;
        this.ano = ano;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlacaNormalizada() {
        return placaNormalizada;
    }

    public void setPlacaNormalizada(String placaNormalizada) {
        this.placaNormalizada = placaNormalizada;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public TipoProprietarioVeiculo getTipoProprietario() {
        return tipoProprietario;
    }

    public void setTipoProprietario(TipoProprietarioVeiculo tipoProprietario) {
        this.tipoProprietario = tipoProprietario;
    }

    public Cliente getProprietarioCliente() {
        return proprietarioCliente;
    }

    public void setProprietarioCliente(Cliente proprietarioCliente) {
        this.proprietarioCliente = proprietarioCliente;
    }
}
