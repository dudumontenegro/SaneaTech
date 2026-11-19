package br.edu.p447.saneatech.internal.ocorrencia.repository;

import br.edu.p447.saneatech.internal.ocorrencia.entity.OcorrenciaEntity;
import br.edu.p447.saneatech.internal.ocorrencia.entity.StatusOcorrencia;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OcorrenciaRepository extends JpaRepository<OcorrenciaEntity, Long> {

    Optional<OcorrenciaEntity> findByProtocolo(String protocolo);

    @EntityGraph(attributePaths = {"solicitante", "equipeResponsavel"})
    List<OcorrenciaEntity> findAllByOrderByIdAsc();

    @EntityGraph(attributePaths = {"solicitante", "equipeResponsavel"})
    List<OcorrenciaEntity> findByStatusOrderByIdAsc(StatusOcorrencia status);
}
