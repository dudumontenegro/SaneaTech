package br.edu.p447.saneatech.internal.ocorrencia.service;

import br.edu.p447.saneatech.internal.ocorrencia.dto.AtualizacaoStatusResponse;
import br.edu.p447.saneatech.internal.ocorrencia.dto.AtualizarStatusRequest;
import br.edu.p447.saneatech.internal.ocorrencia.entity.OcorrenciaEntity;
import br.edu.p447.saneatech.internal.ocorrencia.entity.StatusOcorrencia;
import br.edu.p447.saneatech.internal.ocorrencia.mapper.OcorrenciaMapper;
import br.edu.p447.saneatech.internal.ocorrencia.repository.OcorrenciaRepository;
import br.edu.p447.saneatech.shared.exception.RecursoNaoEncontradoException;
import br.edu.p447.saneatech.shared.exception.RegraNegocioException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Feature: atualização do andamento de uma ocorrência.
 * Valida a transição pedida e delega a mudança ao domínio, que registra o evento no histórico.
 */
@Service
@RequiredArgsConstructor
public class AtualizacaoStatusService {

    private final OcorrenciaRepository ocorrenciaRepository;

    @Transactional
    public AtualizacaoStatusResponse atualizarStatus(Long id, AtualizarStatusRequest request) {
        OcorrenciaEntity ocorrencia = ocorrenciaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Ocorrência", id));

        StatusOcorrencia atual = ocorrencia.getStatus();
        StatusOcorrencia novo = request.status();
        validarTransicao(atual, novo);

        if (novo == StatusOcorrencia.EM_ANDAMENTO) {
            ocorrencia.iniciarAtendimento(request.observacao());
        } else {
            ocorrencia.concluirAtendimento(request.observacao());
        }
        // Entidade gerenciada: o novo HistoricoStatusEntity é persistido por cascade.
        ocorrenciaRepository.flush();
        return OcorrenciaMapper.paraAtualizacaoStatusResposta(ocorrencia, atual);
    }

    /** Somente avanços de uma etapa são permitidos: PENDENTE → EM_ANDAMENTO → CONCLUIDA. */
    public static void validarTransicao(StatusOcorrencia atual, StatusOcorrencia novo) {
        if (novo == null || atual.proximo() != novo) {
            throw new RegraNegocioException("Transição de status inválida: " + atual + " → " + novo
                    + ". Fluxo permitido: PENDENTE → EM_ANDAMENTO → CONCLUIDA.");
        }
    }
}
