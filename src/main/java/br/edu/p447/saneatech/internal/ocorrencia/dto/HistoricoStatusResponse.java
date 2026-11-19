package br.edu.p447.saneatech.internal.ocorrencia.dto;

import br.edu.p447.saneatech.internal.ocorrencia.entity.StatusOcorrencia;
import java.time.Instant;

public record HistoricoStatusResponse(
        StatusOcorrencia statusAnterior,
        StatusOcorrencia statusNovo,
        String observacao,
        String equipe,
        Instant registradoEm) {
}
