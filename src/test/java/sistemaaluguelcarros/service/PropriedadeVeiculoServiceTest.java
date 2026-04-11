package sistemaaluguelcarros.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sistemaaluguelcarros.domain.Automovel;
import sistemaaluguelcarros.domain.Cliente;
import sistemaaluguelcarros.domain.Contrato;
import sistemaaluguelcarros.domain.PedidoAluguel;
import sistemaaluguelcarros.domain.TipoContrato;
import sistemaaluguelcarros.domain.TipoProprietarioVeiculo;
import sistemaaluguelcarros.repository.AutomovelRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PropriedadeVeiculoService")
class PropriedadeVeiculoServiceTest {

    @Mock
    private AutomovelRepository automovelRepository;

    private PropriedadeVeiculoService propriedadeVeiculoService;

    @BeforeEach
    void setUp() {
        propriedadeVeiculoService = new PropriedadeVeiculoService(automovelRepository);
    }

    @Test
    @DisplayName("locação simples mantém titularidade na locadora")
    void locacaoSimplesMantemLocadora() {
        Contrato contrato = contratoCom(TipoContrato.LOCACAO_SIMPLES);

        when(automovelRepository.update(any(Automovel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Automovel atualizado = propriedadeVeiculoService.aplicarPropriedadeAposContrato(contrato);

        assertThat(atualizado.getTipoProprietario()).isEqualTo(TipoProprietarioVeiculo.LOCADORA);
        assertThat(atualizado.getProprietarioCliente()).isNull();
    }

    @Test
    @DisplayName("opção de compra transfere titularidade para o cliente do pedido")
    void opcaoCompraTransfereParaCliente() {
        Contrato contrato = contratoCom(TipoContrato.LOCACAO_COM_OPCAO_COMPRA);
        Automovel auto = contrato.getPedido().getAutomovel();
        Cliente cliente = contrato.getPedido().getCliente();

        when(automovelRepository.update(any(Automovel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<Automovel> captor = ArgumentCaptor.forClass(Automovel.class);
        propriedadeVeiculoService.aplicarPropriedadeAposContrato(contrato);

        verify(automovelRepository).update(captor.capture());
        Automovel salvo = captor.getValue();
        assertThat(salvo.getTipoProprietario()).isEqualTo(TipoProprietarioVeiculo.CLIENTE);
        assertThat(salvo.getProprietarioCliente()).isEqualTo(cliente);
        assertThat(salvo).isEqualTo(auto);
    }

    @Test
    @DisplayName("crédito bancário define titularidade como banco")
    void creditoBancarioDefineBanco() {
        Contrato contrato = contratoCom(TipoContrato.CREDITO_BANCARIO);

        when(automovelRepository.update(any(Automovel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Automovel atualizado = propriedadeVeiculoService.aplicarPropriedadeAposContrato(contrato);

        assertThat(atualizado.getTipoProprietario()).isEqualTo(TipoProprietarioVeiculo.BANCO);
        assertThat(atualizado.getProprietarioCliente()).isNull();
    }

    @Test
    @DisplayName("falha quando pedido não possui automóvel")
    void falhaSemAutomovel() {
        Cliente cliente = new Cliente("Ana", "12345678900", null, "Rua B", "Dev");
        cliente.setId(1L);
        PedidoAluguel pedido = new PedidoAluguel(cliente, "Descrição longa o suficiente para validação do pedido.");
        pedido.setId(9L);
        pedido.setAutomovel(null);
        Contrato contrato = new Contrato();
        contrato.setPedido(pedido);
        contrato.setTipoContrato(TipoContrato.LOCACAO_SIMPLES);

        assertThatThrownBy(() -> propriedadeVeiculoService.aplicarPropriedadeAposContrato(contrato))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("sem automóvel");
    }

    private static Contrato contratoCom(TipoContrato tipo) {
        Cliente cliente = new Cliente("Ana", "12345678900", null, "Rua B", "Dev");
        cliente.setId(1L);
        PedidoAluguel pedido = new PedidoAluguel(cliente, "Descrição longa o suficiente para validação do pedido.");
        pedido.setId(9L);
        Automovel automovel = new Automovel("ABC1D23", "Marca", "Modelo", 2020);
        automovel.setId(3L);
        pedido.setAutomovel(automovel);

        Contrato contrato = new Contrato();
        contrato.setPedido(pedido);
        contrato.setTipoContrato(tipo);
        return contrato;
    }
}
