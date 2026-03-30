package sistemaaluguelcarros.repository;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import sistemaaluguelcarros.domain.PedidoAluguel;

import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoAluguelRepository extends CrudRepository<PedidoAluguel, Long> {

    List<PedidoAluguel> findByClienteIdOrderByDataSolicitacaoDesc(Long clienteId);

    Optional<PedidoAluguel> findByIdAndClienteId(Long id, Long clienteId);
}
