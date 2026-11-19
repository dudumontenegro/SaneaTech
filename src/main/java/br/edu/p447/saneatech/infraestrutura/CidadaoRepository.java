package br.edu.p447.saneatech.infraestrutura;

import br.edu.p447.saneatech.dominio.modelo.Cidadao;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CidadaoRepository extends JpaRepository<Cidadao, Long> {
    Optional<Cidadao> findByEmail(String email);
}
