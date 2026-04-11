package sistemaaluguelcarros.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sistemaaluguelcarros.domain.Cliente;
import sistemaaluguelcarros.repository.AutomovelRepository;
import sistemaaluguelcarros.repository.ClienteRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClienteService")
class ClienteServiceTest {

    private static final String HASH_EXEMPLO = "$2a$10$abcdefghijklmnopqrstuv";

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private PasswordHashService passwordHashService;

    @Mock
    private AutomovelRepository automovelRepository;

    private ClienteService clienteService;

    @BeforeEach
    void setUp() {
        clienteService = new ClienteService(clienteRepository, passwordHashService, automovelRepository);
    }

    @Nested
    @DisplayName("cadastrarComSenha")
    class CadastrarComSenha {

        @Test
        @DisplayName("deve persistir cliente novo com senha em hash")
        void devePersistirComSenhaEmHash() {
            Cliente cliente = novoCliente("João Silva", "529.982.247-25", "12.345.678-9",
                    "Rua das Flores, 100", "Engenheiro");
            Cliente clienteSalvo = novoCliente(1L, "João Silva", "52998224725", "12.345.678-9",
                    "Rua das Flores, 100", "Engenheiro");
            clienteSalvo.setSenhaHash(HASH_EXEMPLO);

            when(clienteRepository.findByCpf("52998224725")).thenReturn(Optional.empty());
            when(passwordHashService.hash("secret12")).thenReturn(HASH_EXEMPLO);
            when(clienteRepository.save(any(Cliente.class))).thenAnswer(inv -> {
                Cliente c = inv.getArgument(0);
                c.setId(1L);
                return c;
            });

            Cliente resultado = clienteService.cadastrarComSenha(cliente, "secret12", "secret12");

            assertThat(resultado.getId()).isEqualTo(1L);
            assertThat(resultado.getCpf()).isEqualTo("52998224725");
            assertThat(resultado.getSenhaHash()).isEqualTo(HASH_EXEMPLO);
            verify(passwordHashService).hash("secret12");
            verify(clienteRepository).save(any(Cliente.class));
        }

        @Test
        @DisplayName("deve rejeitar CPF duplicado com mensagem amigável")
        void deveRejeitarCpfDuplicado() {
            Cliente cliente = novoCliente("Maria Santos", "111.444.777-35", null, "Av. Brasil, 50", null);
            Cliente existente = novoCliente(99L, "Outro Nome", "11144477735", null, "Outro Endereço", null);

            when(clienteRepository.findByCpf("11144477735")).thenReturn(Optional.of(existente));

            assertThatThrownBy(() -> clienteService.cadastrarComSenha(cliente, "secret12", "secret12"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("CPF");

            verify(clienteRepository).findByCpf("11144477735");
            verify(clienteRepository, never()).save(any());
            verify(passwordHashService, never()).hash(any());
        }

        @Test
        @DisplayName("deve rejeitar CPF inválido")
        void deveRejeitarCpfInvalido() {
            Cliente cliente = novoCliente("Maria Santos", "111.111.111-11", null, "Av. Brasil, 50", null);

            assertThatThrownBy(() -> clienteService.cadastrarComSenha(cliente, "secret12", "secret12"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("CPF inválido");

            verify(clienteRepository, never()).findByCpf(any());
            verify(clienteRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("salvar")
    class Salvar {

        @Test
        @DisplayName("deve lançar exceção se tentar salvar sem id (novo cliente)")
        void deveExigirCadastrarComSenhaParaNovo() {
            Cliente cliente = novoCliente("Novo", "000.000.000-00", null, "Rua", null);

            assertThatThrownBy(() -> clienteService.salvar(cliente))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("cadastrarComSenha");
        }

        @Test
        @DisplayName("deve permitir salvar quando edita mesmo cliente (mesmo CPF)")
        void devePermitirSalvarQuandoEditaMesmoCliente() {
            Cliente cliente = novoCliente(1L, "João Atualizado", "529.982.247-25", null, "Nova Rua, 200", "Arquiteto");
            Cliente existente = novoCliente(1L, "João Silva", "52998224725", null, "Rua Antiga", "Engenheiro");
            existente.setSenhaHash(HASH_EXEMPLO);
            cliente.setSenhaHash(HASH_EXEMPLO);

            when(clienteRepository.findByCpf("52998224725")).thenReturn(Optional.of(existente));
            when(clienteRepository.existsById(1L)).thenReturn(true);
            when(clienteRepository.update(any(Cliente.class))).thenReturn(cliente);

            Cliente resultado = clienteService.salvar(cliente);

            assertThat(resultado.getNome()).isEqualTo("João Atualizado");
            assertThat(resultado.getCpf()).isEqualTo("52998224725");
            verify(clienteRepository).existsById(1L);
            verify(clienteRepository).update(cliente);
        }

        @Test
        @DisplayName("deve lançar exceção quando CPF pertence a outro cliente (edição)")
        void deveLancarExcecaoQuandoCpfPertenceAOutroClienteNaEdicao() {
            Cliente cliente = novoCliente(1L, "Maria", "111.444.777-35", null, "Rua X, 123", null);
            Cliente outroCliente = novoCliente(2L, "Pedro", "11144477735", null, "Rua Y, 456", null);

            when(clienteRepository.findByCpf("11144477735")).thenReturn(Optional.of(outroCliente));

            assertThatThrownBy(() -> clienteService.salvar(cliente))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("CPF já cadastrado");

            verify(clienteRepository, never()).save(any());
            verify(clienteRepository, never()).update(any());
        }

        @Test
        @DisplayName("deve lançar exceção quando tenta atualizar cliente inexistente")
        void deveLancarExcecaoQuandoAtualizarClienteInexistente() {
            Cliente cliente = novoCliente(10L, "Julia", "123.456.789-09", null, "Rua A, 100", "Dev");

            when(clienteRepository.findByCpf("12345678909")).thenReturn(Optional.empty());
            when(clienteRepository.existsById(10L)).thenReturn(false);

            assertThatThrownBy(() -> clienteService.salvar(cliente))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Cliente não encontrado.");

            verify(clienteRepository).existsById(10L);
            verify(clienteRepository, never()).update(any());
        }
    }

    @Nested
    @DisplayName("buscarPorId")
    class BuscarPorId {

        @Test
        @DisplayName("deve retornar cliente quando existe")
        void deveRetornarClienteQuandoExiste() {
            Cliente cliente = novoCliente(1L, "Ana", "529.982.247-25", null, "Rua Z, 10", null);
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
            Cliente cliente = novoCliente(1L, "Carlos", "52998224725", null, "Rua W, 200", null);
            when(clienteRepository.findByCpf("52998224725")).thenReturn(Optional.of(cliente));

            Optional<Cliente> resultado = clienteService.buscarPorCpf("529.982.247-25");

            assertThat(resultado).isPresent();
            assertThat(resultado.get().getCpf()).isEqualTo("52998224725");
            verify(clienteRepository).findByCpf("52998224725");
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
            Cliente cliente = novoCliente(1L, "Carlos", "52998224725", null, "Rua W, 200", null);
            when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
            when(automovelRepository.findByProprietarioCliente_Id(1L)).thenReturn(List.of());

            clienteService.excluir(1L);

            verify(clienteRepository).findById(1L);
            verify(automovelRepository).findByProprietarioCliente_Id(1L);
            verify(clienteRepository).delete(cliente);
        }

        @Test
        @DisplayName("deve lançar exceção quando tenta excluir cliente inexistente")
        void deveLancarExcecaoQuandoExcluirClienteInexistente() {
            when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> clienteService.excluir(99L))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Cliente não encontrado.");

            verify(clienteRepository).findById(99L);
            verify(clienteRepository, never()).delete(any());
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
