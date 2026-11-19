package br.edu.p447.saneatech.aplicacao;

import br.edu.p447.saneatech.dominio.enums.StatusOcorrencia;
import br.edu.p447.saneatech.dominio.enums.TipoOcorrencia;
import br.edu.p447.saneatech.dominio.modelo.EnderecoOcorrencia;
import br.edu.p447.saneatech.dominio.modelo.Ocorrencia;

public record OcorrenciaRegistrada(
    Long id,
    String protocolo,
    TipoOcorrencia tipo,
    String descricao,
    StatusOcorrencia status,
    Long cidadaoId,
    EnderecoResponse endereco
) {
    public static OcorrenciaRegistrada de(Ocorrencia ocorrencia) {
        EnderecoOcorrencia endereco = ocorrencia.getEndereco();
        return new OcorrenciaRegistrada(
            ocorrencia.getId(), ocorrencia.getProtocolo(), ocorrencia.getTipo(),
            ocorrencia.getDescricao(), ocorrencia.getStatus(), ocorrencia.getSolicitante().getId(),
            new EnderecoResponse(endereco.getLogradouro(), endereco.getNumero(), endereco.getBairro(),
                endereco.getCidade(), endereco.getUf(), endereco.getReferencia())
        );
    }

    public record EnderecoResponse(String logradouro, String numero, String bairro,
                                   String cidade, String uf, String referencia) {}
}
