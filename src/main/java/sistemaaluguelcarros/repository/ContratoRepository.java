package sistemaaluguelcarros.repository;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import sistemaaluguelcarros.domain.Contrato;

import java.util.Optional;

@Repository
public interface ContratoRepository extends CrudRepository<Contrato, Long> {

    Optional<Contrato> findByPedidoId(Long pedidoId);

    @Query(
            "SELECT c FROM Contrato c JOIN FETCH c.pedido p JOIN FETCH p.cliente "
                    + "LEFT JOIN FETCH p.automovel WHERE c.id = :id"
    )
    Optional<Contrato> buscarPorIdComPedidoECliente(Long id);

    @Query(
            "SELECT c FROM Contrato c JOIN FETCH c.pedido p JOIN FETCH p.cliente "
                    + "LEFT JOIN FETCH p.automovel WHERE p.id = :pedidoId AND p.cliente.id = :clienteId"
    )
    Optional<Contrato> buscarPorPedidoIdEClienteId(Long pedidoId, Long clienteId);
}
