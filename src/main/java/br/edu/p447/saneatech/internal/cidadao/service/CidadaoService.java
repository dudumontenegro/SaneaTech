package br.edu.p447.saneatech.internal.cidadao.service;

import br.edu.p447.saneatech.internal.cidadao.dto.CidadaoResumoResponse;
import br.edu.p447.saneatech.internal.cidadao.dto.CriarCidadaoRequest;
import br.edu.p447.saneatech.internal.cidadao.dto.CriarCidadaoResponse;
import br.edu.p447.saneatech.internal.cidadao.entity.CidadaoEntity;
import br.edu.p447.saneatech.internal.cidadao.mapper.CidadaoMapper;
import br.edu.p447.saneatech.internal.cidadao.repository.CidadaoRepository;
import br.edu.p447.saneatech.shared.exception.RecursoDuplicadoException;
import br.edu.p447.saneatech.shared.exception.RecursoNaoEncontradoException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CidadaoService {

    private final CidadaoRepository cidadaoRepository;

    @Transactional
    public CriarCidadaoResponse cadastrarCidadao(CriarCidadaoRequest request) {
        if (cidadaoRepository.existsByEmail(CidadaoEntity.normalizarEmail(request.email()))) {
            throw new RecursoDuplicadoException("Já existe um cidadão cadastrado com o e-mail \"" + request.email() + "\".");
        }
        CidadaoEntity cidadao = CidadaoMapper.converteParaEntidade(request);
        cidadaoRepository.save(cidadao);
        return CidadaoMapper.converterParaResposta(cidadao);
    }

    @Transactional(readOnly = true)
    public List<CidadaoResumoResponse> listarTodos() {
        return cidadaoRepository.findAllByOrderByIdAsc().stream()
                .map(CidadaoMapper::paraResumoResposta)
                .toList();
    }

    /** Usado por outras features (ex.: registro de ocorrência) que precisam da entidade. */
    @Transactional(readOnly = true)
    public CidadaoEntity buscarEntidadePorId(Long id) {
        return cidadaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cidadão", id));
    }
}
