package sistemaaluguelcarros.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sistemaaluguelcarros.domain.Cliente;
import sistemaaluguelcarros.domain.Rendimento;
import sistemaaluguelcarros.repository.RendimentoRepository;
import sistemaaluguelcarros.validation.ValidationRules;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("RendimentoService")
class RendimentoServiceTest {

    @Mock
    private RendimentoRepository rendimentoRepository;

    @Mock
    private ClienteService clienteService;

    @InjectMocks
    private RendimentoService rendimentoService;

    @Test
    @DisplayName("deve impedir quarto rendimento")
    void deveImpedirQuartoRendimento() {
        when(rendimentoRepository.countByCliente_Id(1L)).thenReturn(3L);

        assertThatThrownBy(() -> rendimentoService.adicionar(
                1L,
                "Empresa X",
                null,
                new BigDecimal("1000.00")
        ))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("máximo");
    }

    @Test
    @DisplayName("deve cadastrar quando abaixo do limite")
    void deveCadastrarQuandoAbaixoDoLimite() {
        Cliente cliente = new Cliente("Ana", "52998224725", null, "Rua das Flores 100", null);
        cliente.setId(2L);
        when(rendimentoRepository.countByCliente_Id(2L)).thenReturn(0L);
        when(clienteService.buscarPorId(2L)).thenReturn(Optional.of(cliente));
        when(rendimentoRepository.save(any(Rendimento.class))).thenAnswer(invocation -> {
            Rendimento r = invocation.getArgument(0);
            r.setId(99L);
            return r;
        });

        Rendimento salvo = rendimentoService.adicionar(
                2L,
                "Tech Ltda",
                null,
                new BigDecimal("2500.50")
        );

        assertThat(salvo.getId()).isEqualTo(99L);
        ArgumentCaptor<Rendimento> captor = ArgumentCaptor.forClass(Rendimento.class);
        verify(rendimentoRepository).save(captor.capture());
        assertThat(captor.getValue().getEmpregador().getNome()).isEqualTo("Tech Ltda");
        assertThat(captor.getValue().getValorMensal()).isEqualByComparingTo("2500.50");
    }

    @Test
    @DisplayName("clientePodeAdicionar deve respeitar limite")
    void clientePodeAdicionarDeveRespeitarLimite() {
        when(rendimentoRepository.countByCliente_Id(1L)).thenReturn((long) ValidationRules.MAX_RENDIMENTOS_POR_CLIENTE);
        assertThat(rendimentoService.clientePodeAdicionar(1L)).isFalse();

        when(rendimentoRepository.countByCliente_Id(1L)).thenReturn(2L);
        assertThat(rendimentoService.clientePodeAdicionar(1L)).isTrue();
    }
}
