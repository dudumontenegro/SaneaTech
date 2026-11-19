package br.edu.p447.saneatech.internal.ocorrencia.service;

import br.edu.p447.saneatech.internal.cidadao.entity.CidadaoEntity;
import br.edu.p447.saneatech.internal.cidadao.service.CidadaoService;
import br.edu.p447.saneatech.internal.ocorrencia.dto.CriarOcorrenciaRequest;
import br.edu.p447.saneatech.internal.ocorrencia.dto.CriarOcorrenciaResponse;
import br.edu.p447.saneatech.internal.ocorrencia.dto.OcorrenciaDetalheResponse;
import br.edu.p447.saneatech.internal.ocorrencia.dto.OcorrenciaResumoResponse;
import br.edu.p447.saneatech.internal.ocorrencia.entity.OcorrenciaEntity;
import br.edu.p447.saneatech.internal.ocorrencia.entity.StatusOcorrencia;
import br.edu.p447.saneatech.internal.ocorrencia.mapper.OcorrenciaMapper;
import br.edu.p447.saneatech.internal.ocorrencia.repository.OcorrenciaRepository;
import br.edu.p447.saneatech.shared.exception.RecursoNaoEncontradoException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Registro (entrega anterior) e consulta de ocorrências. */
@Service
@RequiredArgsConstructor
public class OcorrenciaService {

    private final OcorrenciaRepository ocorrenciaRepository;
    private final CidadaoService cidadaoService;

    @Transactional
    public CriarOcorrenciaResponse registrarOcorrencia(CriarOcorrenciaRequest request) {
        CidadaoEntity cidadao = cidadaoService.buscarEntidadePorId(request.cidadaoId());
        OcorrenciaEntity ocorrencia = OcorrenciaEntity.abrir(cidadao, request.tipo(), request.descricao(),
                OcorrenciaMapper.converteParaEndereco(request.endereco()));
        ocorrenciaRepository.save(ocorrencia);
        return OcorrenciaMapper.converterParaResposta(ocorrencia);
    }

    @Transactional(readOnly = true)
    public List<OcorrenciaResumoResponse> listarTodas(StatusOcorrencia status) {
        List<OcorrenciaEntity> ocorrencias = status == null
                ? ocorrenciaRepository.findAllByOrderByIdAsc()
                : ocorrenciaRepository.findByStatusOrderByIdAsc(status);
        return ocorrencias.stream().map(OcorrenciaMapper::paraResumoResposta).toList();
    }

    @Transactional(readOnly = true)
    public OcorrenciaDetalheResponse buscarOcorrenciaPorId(Long id) {
        return ocorrenciaRepository.findById(id)
                .map(OcorrenciaMapper::paraDetalheResposta)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Ocorrência", id));
    }
}
