package br.edu.p447.saneatech.internal.ocorrencia.dto;

import br.edu.p447.saneatech.internal.ocorrencia.entity.TipoOcorrencia;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Corpo do POST /ocorrencias. */
public record CriarOcorrenciaRequest(
        @NotNull(message = "Cidadão é obrigatório.") Long cidadaoId,
        @NotNull(message = "Tipo é obrigatório.") TipoOcorrencia tipo,
        @NotBlank(message = "Descrição é obrigatória.")
        @Size(max = 1000, message = "Descrição deve ter no máximo 1000 caracteres.") String descricao,
        @NotNull(message = "Endereço é obrigatório.") @Valid EnderecoRequest endereco) {
}
