package br.edu.p447.saneatech.internal.cidadao.repository;

import br.edu.p447.saneatech.internal.cidadao.entity.CidadaoEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CidadaoRepository extends JpaRepository<CidadaoEntity, Long> {

    boolean existsByEmail(String email);

    List<CidadaoEntity> findAllByOrderByIdAsc();
}
