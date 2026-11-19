package br.edu.p447.saneatech.internal.ocorrencia.dto;

import br.edu.p447.saneatech.internal.ocorrencia.entity.StatusOcorrencia;
import br.edu.p447.saneatech.internal.ocorrencia.entity.TipoOcorrencia;
import java.time.Instant;

/** Item da listagem GET /ocorrencias. */
public record OcorrenciaResumoResponse(
        Long id,
        String protocolo,
        TipoOcorrencia tipo,
        StatusOcorrencia status,
        String bairro,
        String cidade,
        String solicitante,
        String equipe,
        Instant criadoEm,
        Instant atualizadoEm) {
}
