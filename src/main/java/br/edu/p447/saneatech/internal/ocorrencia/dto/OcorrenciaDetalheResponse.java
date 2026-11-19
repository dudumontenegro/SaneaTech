package br.edu.p447.saneatech.internal.ocorrencia.dto;

import br.edu.p447.saneatech.internal.ocorrencia.entity.StatusOcorrencia;
import br.edu.p447.saneatech.internal.ocorrencia.entity.TipoOcorrencia;
import java.time.Instant;
import java.util.List;

/** Resposta do GET /ocorrencias/{id}, com solicitante, equipe e histórico completo. */
public record OcorrenciaDetalheResponse(
        Long id,
        String protocolo,
        TipoOcorrencia tipo,
        String descricao,
        StatusOcorrencia status,
        Instant criadoEm,
        Instant atualizadoEm,
        EnderecoResponse endereco,
        Solicitante solicitante,
        Equipe equipeResponsavel,
        List<HistoricoStatusResponse> historico) {

    public record Solicitante(Long id, String nome, String email, String telefone) {
    }

    public record Equipe(Long id, String nome, String areaAtuacao) {
    }
}
