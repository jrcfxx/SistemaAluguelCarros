package sistemaaluguelcarros.repository;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import sistemaaluguelcarros.domain.Cliente;

import java.util.Optional;

@Repository
public interface ClienteRepository extends CrudRepository<Cliente, Long> {

    Optional<Cliente> findByCpf(String cpf);
}
