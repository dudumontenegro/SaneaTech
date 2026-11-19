package br.edu.p447.saneatech.internal.equipe.service;

import br.edu.p447.saneatech.internal.equipe.dto.CriarEquipeRequest;
import br.edu.p447.saneatech.internal.equipe.dto.CriarEquipeResponse;
import br.edu.p447.saneatech.internal.equipe.entity.EquipeResponsavelEntity;
import br.edu.p447.saneatech.internal.equipe.mapper.EquipeMapper;
import br.edu.p447.saneatech.internal.equipe.repository.EquipeResponsavelRepository;
import br.edu.p447.saneatech.shared.exception.RecursoDuplicadoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Feature: cadastro de equipes.
 * A entidade valida obrigatoriedade e tamanho; aqui é verificada a unicidade do nome
 * (sem diferenciar maiúsculas de minúsculas).
 */
@Service
@RequiredArgsConstructor
public class CadastroEquipeService {

    private final EquipeResponsavelRepository equipeRepository;

    @Transactional
    public CriarEquipeResponse cadastrarEquipe(CriarEquipeRequest request) {
        EquipeResponsavelEntity equipe = EquipeMapper.converteParaEntidade(request);
        if (equipeRepository.existsByNomeIgnoreCase(equipe.getNome())) {
            throw new RecursoDuplicadoException("Já existe uma equipe cadastrada com o nome \"" + equipe.getNome() + "\".");
        }
        equipeRepository.save(equipe);
        return EquipeMapper.converterParaResposta(equipe);
    }
}
