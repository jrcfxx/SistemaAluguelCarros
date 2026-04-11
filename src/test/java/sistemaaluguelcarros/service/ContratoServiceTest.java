package sistemaaluguelcarros.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sistemaaluguelcarros.domain.Automovel;
import sistemaaluguelcarros.domain.Cliente;
import sistemaaluguelcarros.domain.Contrato;
import sistemaaluguelcarros.domain.PedidoAluguel;
import sistemaaluguelcarros.domain.StatusPedido;
import sistemaaluguelcarros.domain.TipoContrato;
import sistemaaluguelcarros.repository.ContratoRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ContratoService")
class ContratoServiceTest {

    @Mock
    private ContratoRepository contratoRepository;

    @Mock
    private PropriedadeVeiculoService propriedadeVeiculoService;

    private ContratoService contratoService;

    @BeforeEach
    void setUp() {
        contratoService = new ContratoService(contratoRepository, propriedadeVeiculoService);
    }

    @Test
    @DisplayName("deve persistir contrato ao aprovar pedido")
    void devePersistirContratoAoAprovarPedido() {
        Cliente cliente = new Cliente("Ana", "12345678900", null, "Rua B", "Dev");
        cliente.setId(1L);
        PedidoAluguel pedido = new PedidoAluguel(cliente, "Descrição longa o suficiente para o pedido de teste.");
        pedido.setId(9L);
        pedido.setStatus(StatusPedido.APROVADO);
        Automovel automovel = new Automovel("ABC1D23", "Marca", "Modelo", 2020);
        automovel.setId(3L);
        pedido.setAutomovel(automovel);

        when(contratoRepository.findByPedidoId(9L)).thenReturn(Optional.empty());
        when(contratoRepository.save(any(Contrato.class))).thenAnswer(invocation -> {
            Contrato c = invocation.getArgument(0);
            c.setId(100L);
            return c;
        });

        Contrato salvo = contratoService.criarContratoParaPedidoAprovado(pedido, TipoContrato.LOCACAO_SIMPLES);

        assertThat(salvo.getId()).isEqualTo(100L);
        assertThat(salvo.getNumeroContrato()).startsWith("CTR-9-");
        assertThat(salvo.getTermos()).contains("Descrição longa o suficiente");
        verify(contratoRepository).save(any(Contrato.class));
        verify(propriedadeVeiculoService).aplicarPropriedadeAposContrato(any(Contrato.class));
    }

    @Test
    @DisplayName("deve impedir segundo contrato para o mesmo pedido")
    void deveImpedirSegundoContrato() {
        Cliente cliente = new Cliente("Ana", "12345678900", null, "Rua B", "Dev");
        PedidoAluguel pedido = new PedidoAluguel(cliente, "Descrição longa o suficiente para o pedido de teste.");
        pedido.setId(9L);
        pedido.setStatus(StatusPedido.APROVADO);

        Contrato existente = new Contrato();
        when(contratoRepository.findByPedidoId(9L)).thenReturn(Optional.of(existente));

        assertThatThrownBy(() -> contratoService.criarContratoParaPedidoAprovado(pedido, TipoContrato.LOCACAO_SIMPLES))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Já existe contrato");
    }
}
