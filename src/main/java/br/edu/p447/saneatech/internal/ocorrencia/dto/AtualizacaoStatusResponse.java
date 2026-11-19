package br.edu.p447.saneatech.internal.ocorrencia.dto;

import br.edu.p447.saneatech.internal.ocorrencia.entity.StatusOcorrencia;
import java.time.Instant;
import java.util.List;

/** Estado da ocorrência após a atualização, incluindo o histórico completo. */
public record AtualizacaoStatusResponse(
        Long id,
        String protocolo,
        StatusOcorrencia statusAnterior,
        StatusOcorrencia status,
        Instant atualizadoEm,
        List<HistoricoStatusResponse> historico) {
}
