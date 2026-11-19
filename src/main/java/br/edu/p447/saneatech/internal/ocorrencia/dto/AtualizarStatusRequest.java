package br.edu.p447.saneatech.internal.ocorrencia.dto;

import br.edu.p447.saneatech.internal.ocorrencia.entity.StatusOcorrencia;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Corpo do PATCH /ocorrencias/{id}/status. */
public record AtualizarStatusRequest(
        @NotNull(message = "Status obrigatório.")
        StatusOcorrencia status,

        @NotBlank(message = "Observação obrigatória.")
        @Size(max = 300, message = "Observação deve ter no máximo 300 caracteres.")
        String observacao) {
}
