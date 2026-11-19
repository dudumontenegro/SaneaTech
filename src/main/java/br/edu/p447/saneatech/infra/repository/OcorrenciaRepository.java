package br.edu.p447.saneatech.infra.repository;

import br.edu.p447.saneatech.dominio.modelo.Ocorrencia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OcorrenciaRepository extends JpaRepository<Ocorrencia, Long> { }