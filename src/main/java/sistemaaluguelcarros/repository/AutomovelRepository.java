package sistemaaluguelcarros.repository;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import sistemaaluguelcarros.domain.Automovel;

import java.util.List;
import java.util.Optional;

@Repository
public interface AutomovelRepository extends CrudRepository<Automovel, Long> {

    Optional<Automovel> findByPlacaNormalizada(String placaNormalizada);

    List<Automovel> findByProprietarioCliente_Id(Long proprietarioClienteId);

    @Query(
            "SELECT DISTINCT a FROM Automovel a LEFT JOIN FETCH a.proprietarioCliente "
                    + "ORDER BY a.marca ASC, a.modelo ASC, a.placaNormalizada ASC"
    )
    List<Automovel> listarOrdenados();
}
