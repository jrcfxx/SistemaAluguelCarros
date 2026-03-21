package sistemaaluguelcarros.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sistemaaluguelcarros.domain.Cliente;
import sistemaaluguelcarros.repository.ClienteRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClienteService")
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    private ClienteService clienteService;

    @BeforeEach
    void setUp() {
        clienteService = new ClienteService(clienteRepository);
    }

    @Nested
    @DisplayName("salvar")
    class Salvar {

        @Test
        @DisplayName("deve salvar cliente novo com sucesso")
        void deveSalvarClienteNovoComSucesso() {
            Cliente cliente = novoCliente("João Silva", "123.456.789-00", "12.345.678-9",
                    "Rua das Flores, 100", "Engenheiro");
            Cliente clienteSalvo = novoCliente(1L, "João Silva", "123.456.789-00", "12.345.678-9",
                    "Rua das Flores, 100", "Engenheiro");

            when(clienteRepository.findByCpf("123.456.789-00")).thenReturn(Optional.empty());
            when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteSalvo);

            Cliente resultado = clienteService.salvar(cliente);

            assertThat(resultado.getId()).isEqualTo(1L);
            assertThat(resultado.getNome()).isEqualTo("João Silva");
            assertThat(resultado.getCpf()).isEqualTo("123.456.789-00");
            verify(clienteRepository).findByCpf("123.456.789-00");
            verify(clienteRepository).save(cliente);
        }

        @Test
        @DisplayName("deve lançar exceção quando CPF já está cadastrado (cliente novo)")
        void deveLancarExcecaoQuandoCpfDuplicadoParaClienteNovo() {
            Cliente cliente = novoCliente("Maria Santos", "111.222.333-44", null, "Av. Brasil, 50", null);
            Cliente existente = novoCliente(99L, "Outro Nome", "111.222.333-44", null, "Outro Endereço", null);

            when(clienteRepository.findByCpf("111.222.333-44")).thenReturn(Optional.of(existente));

            assertThatThrownBy(() -> clienteService.salvar(cliente))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("CPF já cadastrado")
                    .hasMessageContaining("111.222.333-44");

            verify(clienteRepository).findByCpf("111.222.333-44");
            verify(clienteRepository, org.mockito.Mockito.never()).save(any());
        }

        @Test
        @DisplayName("deve permitir salvar quando edita mesmo cliente (mesmo CPF)")
        void devePermitirSalvarQuandoEditaMesmoCliente() {
            Cliente cliente = novoCliente(1L, "João Atualizado", "123.456.789-00", null, "Nova Rua, 200", "Arquiteto");
            Cliente existente = novoCliente(1L, "João Silva", "123.456.789-00", null, "Rua Antiga", "Engenheiro");

            when(clienteRepository.findByCpf("123.456.789-00")).thenReturn(Optional.of(existente));
            when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

            Cliente resultado = clienteService.salvar(cliente);

            assertThat(resultado.getNome()).isEqualTo("João Atualizado");
            verify(clienteRepository).save(cliente);
        }

        @Test
        @DisplayName("deve lançar exceção quando CPF pertence a outro cliente (edição)")
        void deveLancarExcecaoQuandoCpfPertenceAOutroClienteNaEdicao() {
            Cliente cliente = novoCliente(1L, "Maria", "999.888.777-66", null, "Rua X", null);
            Cliente outroCliente = novoCliente(2L, "Pedro", "999.888.777-66", null, "Rua Y", null);

            when(clienteRepository.findByCpf("999.888.777-66")).thenReturn(Optional.of(outroCliente));

            assertThatThrownBy(() -> clienteService.salvar(cliente))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("CPF já cadastrado");

            verify(clienteRepository, org.mockito.Mockito.never()).save(any());
        }
    }

    @Nested
    @DisplayName("buscarPorId")
    class BuscarPorId {

        @Test
        @DisplayName("deve retornar cliente quando existe")
        void deveRetornarClienteQuandoExiste() {
            Cliente cliente = novoCliente(1L, "Ana", "555.444.333-22", null, "Rua Z", null);
            when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

            Optional<Cliente> resultado = clienteService.buscarPorId(1L);

            assertThat(resultado).isPresent();
            assertThat(resultado.get().getNome()).isEqualTo("Ana");
            verify(clienteRepository).findById(1L);
        }

        @Test
        @DisplayName("deve retornar vazio quando cliente não existe")
        void deveRetornarVazioQuandoNaoExiste() {
            when(clienteRepository.findById(999L)).thenReturn(Optional.empty());

            Optional<Cliente> resultado = clienteService.buscarPorId(999L);

            assertThat(resultado).isEmpty();
        }
    }

    @Nested
    @DisplayName("buscarPorCpf")
    class BuscarPorCpf {

        @Test
        @DisplayName("deve retornar cliente quando CPF existe")
        void deveRetornarClienteQuandoCpfExiste() {
            Cliente cliente = novoCliente(1L, "Carlos", "111.111.111-11", null, "Rua W", null);
            when(clienteRepository.findByCpf("111.111.111-11")).thenReturn(Optional.of(cliente));

            Optional<Cliente> resultado = clienteService.buscarPorCpf("111.111.111-11");

            assertThat(resultado).isPresent();
            assertThat(resultado.get().getCpf()).isEqualTo("111.111.111-11");
            verify(clienteRepository).findByCpf("111.111.111-11");
        }
    }

    @Nested
    @DisplayName("listarTodos")
    class ListarTodos {

        @Test
        @DisplayName("deve delegar ao repositório")
        void deveDelegarAoRepositorio() {
            clienteService.listarTodos();
            verify(clienteRepository).findAll();
        }
    }

    @Nested
    @DisplayName("excluir")
    class Excluir {

        @Test
        @DisplayName("deve excluir por ID")
        void deveExcluirPorId() {
            clienteService.excluir(1L);
            verify(clienteRepository).deleteById(1L);
        }
    }

    private static Cliente novoCliente(String nome, String cpf, String rg, String endereco, String profissao) {
        return new Cliente(nome, cpf, rg, endereco, profissao);
    }

    private static Cliente novoCliente(Long id, String nome, String cpf, String rg, String endereco, String profissao) {
        Cliente c = new Cliente(nome, cpf, rg, endereco, profissao);
        c.setId(id);
        return c;
    }
}
