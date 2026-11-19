package br.edu.p447.saneatech.api.dto;

import br.edu.p447.saneatech.dominio.modelo.EnderecoOcorrencia;
import br.edu.p447.saneatech.dominio.modelo.HistoricoStatus;
import br.edu.p447.saneatech.dominio.modelo.Ocorrencia;
import java.time.Instant;
import java.util.List;

public record OcorrenciaResposta(
    Long id,
    String protocolo,
    String tipo,
    String descricao,
    String status,
    Instant criadoEm,
    Instant atualizadoEm,
    EnderecoResposta endereco,
    CidadaoResposta solicitante,
    EquipeResposta equipeResponsavel,
    List<HistoricoResposta> historico
) {
    public static OcorrenciaResposta de(Ocorrencia ocorrencia) {
        return new OcorrenciaResposta(
            ocorrencia.getId(),
            ocorrencia.getProtocolo(),
            ocorrencia.getTipo().name(),
            ocorrencia.getDescricao(),
            ocorrencia.getStatus().name(),
            ocorrencia.getCriadoEm(),
            ocorrencia.getAtualizadoEm(),
            EnderecoResposta.de(ocorrencia.getEndereco()),
            new CidadaoResposta(ocorrencia.getSolicitante().getId(),
                ocorrencia.getSolicitante().getNome(), ocorrencia.getSolicitante().getEmail(),
                ocorrencia.getSolicitante().getTelefone()),
            ocorrencia.getEquipeResponsavel() == null ? null : new EquipeResposta(
                ocorrencia.getEquipeResponsavel().getId(), ocorrencia.getEquipeResponsavel().getNome(),
                    ocorrencia.getEquipeResponsavel().getAreaAtuacao()),
            ocorrencia.getHistorico().stream().map(HistoricoResposta::de).toList()
        );
    }

    public record EnderecoResposta(
        String logradouro, String numero, String bairro, String cidade, String uf, String referencia
    ) {
        private static EnderecoResposta de(EnderecoOcorrencia endereco) {
            return new EnderecoResposta(endereco.getLogradouro(), endereco.getNumero(), endereco.getBairro(),
                endereco.getCidade(), endereco.getUf(), endereco.getReferencia());
        }
    }

    public record CidadaoResposta(Long id, String nome, String email, String telefone) { }

    public record EquipeResposta(Long id, String nome, String areaAtuacao) { }

    public record HistoricoResposta(
        String statusAnterior, String statusNovo, String observacao, Instant registradoEm
    ) {
        private static HistoricoResposta de(HistoricoStatus historico) {
            return new HistoricoResposta(
                historico.getStatusAnterior() == null ? null : historico.getStatusAnterior().name(),
                historico.getStatusNovo().name(), historico.getObservacao(), historico.getRegistradoEm());
        }
    }
}