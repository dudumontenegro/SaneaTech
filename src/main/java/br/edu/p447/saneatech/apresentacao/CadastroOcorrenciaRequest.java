package br.edu.p447.saneatech.apresentacao;

import br.edu.p447.saneatech.dominio.enums.TipoOcorrencia;
import br.edu.p447.saneatech.dominio.modelo.EnderecoOcorrencia;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CadastroOcorrenciaRequest(
    @NotNull Long cidadaoId,
    @NotNull TipoOcorrencia tipo,
    @NotBlank String descricao,
    @NotNull @Valid EnderecoRequest endereco
) {
    public EnderecoOcorrencia enderecoDominio() {
        return new EnderecoOcorrencia(endereco.logradouro, endereco.numero, endereco.bairro,
            endereco.cidade, endereco.uf, endereco.referencia);
    }

    public record EnderecoRequest(
        @NotBlank String logradouro,
        String numero,
        @NotBlank String bairro,
        @NotBlank String cidade,
        @NotBlank String uf,
        String referencia
    ) {}
}
