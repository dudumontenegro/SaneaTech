package br.edu.p447.saneatech.internal.equipe.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Corpo do POST /equipes. */
public record CriarEquipeRequest(
        @NotBlank(message = "Nome da equipe é obrigatório.")
        @Size(max = 120, message = "Nome da equipe deve ter no máximo 120 caracteres.")
        String nome,

        @NotBlank(message = "Área de atuação é obrigatória.")
        @Size(max = 120, message = "Área de atuação deve ter no máximo 120 caracteres.")
        String areaAtuacao) {
}
