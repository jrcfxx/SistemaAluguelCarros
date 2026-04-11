package sistemaaluguelcarros.repository;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.repository.CrudRepository;
import sistemaaluguelcarros.domain.PedidoAluguel;
import sistemaaluguelcarros.domain.StatusPedido;

import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoAluguelRepository extends CrudRepository<PedidoAluguel, Long> {

    @Query(
            "SELECT DISTINCT p FROM PedidoAluguel p JOIN FETCH p.cliente LEFT JOIN FETCH p.contrato ORDER BY p.dataSolicitacao DESC"
    )
    List<PedidoAluguel> listarParaAnalise();

    @Query(
            "SELECT p FROM PedidoAluguel p JOIN FETCH p.cliente LEFT JOIN FETCH p.contrato WHERE p.id = :id"
    )
    Optional<PedidoAluguel> buscarDetalhePorId(Long id);

    @Query(
            "SELECT DISTINCT p FROM PedidoAluguel p JOIN FETCH p.cliente LEFT JOIN FETCH p.contrato WHERE p.cliente.id = :clienteId ORDER BY p.dataSolicitacao DESC"
    )
    List<PedidoAluguel> findByClienteIdOrderByDataSolicitacaoDesc(Long clienteId);

    long countByStatus(StatusPedido status);

    Optional<PedidoAluguel> findByIdAndClienteId(Long id, Long clienteId);
}
