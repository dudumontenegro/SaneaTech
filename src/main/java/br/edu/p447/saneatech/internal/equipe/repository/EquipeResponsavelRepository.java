package br.edu.p447.saneatech.internal.equipe.repository;

import br.edu.p447.saneatech.internal.equipe.entity.EquipeResponsavelEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipeResponsavelRepository extends JpaRepository<EquipeResponsavelEntity, Long> {

    boolean existsByNomeIgnoreCase(String nome);

    List<EquipeResponsavelEntity> findAllByOrderByIdAsc();
}
