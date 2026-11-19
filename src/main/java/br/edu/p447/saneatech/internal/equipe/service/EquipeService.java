package br.edu.p447.saneatech.internal.equipe.service;

import br.edu.p447.saneatech.internal.equipe.dto.EquipeResumoResponse;
import br.edu.p447.saneatech.internal.equipe.mapper.EquipeMapper;
import br.edu.p447.saneatech.internal.equipe.repository.EquipeResponsavelRepository;
import br.edu.p447.saneatech.shared.exception.RecursoNaoEncontradoException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Feature: consulta de equipes. */
@Service
@RequiredArgsConstructor
public class EquipeService {

    private final EquipeResponsavelRepository equipeRepository;

    @Transactional(readOnly = true)
    public List<EquipeResumoResponse> listarTodas() {
        return equipeRepository.findAllByOrderByIdAsc().stream()
                .map(EquipeMapper::paraResumoResposta)
                .toList();
    }

    @Transactional(readOnly = true)
    public EquipeResumoResponse buscarEquipePorId(Long id) {
        return equipeRepository.findById(id)
                .map(EquipeMapper::paraResumoResposta)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Equipe", id));
    }
}
