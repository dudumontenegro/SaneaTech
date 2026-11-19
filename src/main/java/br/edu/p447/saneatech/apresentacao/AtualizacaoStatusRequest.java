package br.edu.p447.saneatech.apresentacao;

import br.edu.p447.saneatech.dominio.enums.StatusOcorrencia;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Corpo do PATCH /ocorrencias/{id}/status. */
public record AtualizacaoStatusRequest(
        @NotNull(message = "Status obrigatório.") StatusOcorrencia status,
        @NotBlank(message = "Observação obrigatória.")
        @Size(max = 300, message = "Observação deve ter no máximo 300 caracteres.") String observacao) {
}
