package br.edu.p447.saneatech.infraestrutura;

import br.edu.p447.saneatech.dominio.modelo.Ocorrencia;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** Acesso a dados das ocorrências já registradas no banco. */
public interface OcorrenciaRepository extends JpaRepository<Ocorrencia, Long> {
    Optional<Ocorrencia> findByProtocolo(String protocolo);
}
