package br.edu.p447.saneatech.internal.ocorrencia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EnderecoRequest(
        @NotBlank(message = "Logradouro é obrigatório.") @Size(max = 180) String logradouro,
        @Size(max = 20) String numero,
        @NotBlank(message = "Bairro é obrigatório.") @Size(max = 100) String bairro,
        @NotBlank(message = "Cidade é obrigatória.") @Size(max = 100) String cidade,
        @NotBlank(message = "UF é obrigatória.")
        @Pattern(regexp = "[A-Za-z]{2}", message = "UF deve conter duas letras.") String uf,
        @Size(max = 200) String referencia) {
}
