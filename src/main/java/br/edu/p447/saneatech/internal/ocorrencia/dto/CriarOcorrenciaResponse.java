package br.edu.p447.saneatech.internal.ocorrencia.dto;

import br.edu.p447.saneatech.internal.ocorrencia.entity.StatusOcorrencia;
import br.edu.p447.saneatech.internal.ocorrencia.entity.TipoOcorrencia;

public record CriarOcorrenciaResponse(
        Long id,
        String protocolo,
        TipoOcorrencia tipo,
        String descricao,
        StatusOcorrencia status,
        Long cidadaoId,
        EnderecoResponse endereco) {
}
