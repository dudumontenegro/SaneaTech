package br.edu.p447.saneatech.infraestrutura;

import br.edu.p447.saneatech.dominio.modelo.EquipeResponsavel;
import org.springframework.data.jpa.repository.JpaRepository;

/** Acesso a dados das equipes responsáveis. */
public interface EquipeResponsavelRepository extends JpaRepository<EquipeResponsavel, Long> {
    boolean existsByNomeIgnoreCase(String nome);
}
