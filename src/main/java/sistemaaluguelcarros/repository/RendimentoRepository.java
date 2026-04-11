package sistemaaluguelcarros.repository;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.repository.CrudRepository;
import sistemaaluguelcarros.domain.Rendimento;

import java.util.List;
import java.util.Optional;

@Repository
public interface RendimentoRepository extends CrudRepository<Rendimento, Long> {

    @Query("SELECT r FROM Rendimento r JOIN FETCH r.empregador WHERE r.cliente.id = :clienteId ORDER BY r.id")
    List<Rendimento> listarComEmpregadorPorCliente(Long clienteId);

    long countByCliente_Id(Long clienteId);

    Optional<Rendimento> findByIdAndCliente_Id(Long id, Long clienteId);
}
