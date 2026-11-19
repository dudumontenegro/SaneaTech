package br.edu.p447.saneatech.internal.ocorrencia.mapper;

import br.edu.p447.saneatech.internal.cidadao.entity.CidadaoEntity;
import br.edu.p447.saneatech.internal.equipe.entity.EquipeResponsavelEntity;
import br.edu.p447.saneatech.internal.ocorrencia.dto.AtualizacaoStatusResponse;
import br.edu.p447.saneatech.internal.ocorrencia.dto.CriarOcorrenciaResponse;
import br.edu.p447.saneatech.internal.ocorrencia.dto.EnderecoRequest;
import br.edu.p447.saneatech.internal.ocorrencia.dto.EnderecoResponse;
import br.edu.p447.saneatech.internal.ocorrencia.dto.HistoricoStatusResponse;
import br.edu.p447.saneatech.internal.ocorrencia.dto.OcorrenciaDetalheResponse;
import br.edu.p447.saneatech.internal.ocorrencia.dto.OcorrenciaResumoResponse;
import br.edu.p447.saneatech.internal.ocorrencia.entity.EnderecoOcorrencia;
import br.edu.p447.saneatech.internal.ocorrencia.entity.HistoricoStatusEntity;
import br.edu.p447.saneatech.internal.ocorrencia.entity.OcorrenciaEntity;
import br.edu.p447.saneatech.internal.ocorrencia.entity.StatusOcorrencia;
import java.util.List;

public final class OcorrenciaMapper {

    private OcorrenciaMapper() {
    }

    public static EnderecoOcorrencia converteParaEndereco(EnderecoRequest request) {
        return new EnderecoOcorrencia(request.logradouro(), request.numero(), request.bairro(),
                request.cidade(), request.uf(), request.referencia());
    }

    public static CriarOcorrenciaResponse converterParaResposta(OcorrenciaEntity ocorrencia) {
        return new CriarOcorrenciaResponse(ocorrencia.getId(), ocorrencia.getProtocolo(), ocorrencia.getTipo(),
                ocorrencia.getDescricao(), ocorrencia.getStatus(), ocorrencia.getSolicitante().getId(),
                paraEnderecoResposta(ocorrencia.getEndereco()));
    }

    public static OcorrenciaResumoResponse paraResumoResposta(OcorrenciaEntity ocorrencia) {
        EquipeResponsavelEntity equipe = ocorrencia.getEquipeResponsavel();
        return new OcorrenciaResumoResponse(ocorrencia.getId(), ocorrencia.getProtocolo(), ocorrencia.getTipo(),
                ocorrencia.getStatus(), ocorrencia.getEndereco().getBairro(), ocorrencia.getEndereco().getCidade(),
                ocorrencia.getSolicitante().getNome(), equipe == null ? null : equipe.getNome(),
                ocorrencia.getCriadoEm(), ocorrencia.getAtualizadoEm());
    }

    public static OcorrenciaDetalheResponse paraDetalheResposta(OcorrenciaEntity ocorrencia) {
        CidadaoEntity cidadao = ocorrencia.getSolicitante();
        EquipeResponsavelEntity equipe = ocorrencia.getEquipeResponsavel();
        return new OcorrenciaDetalheResponse(ocorrencia.getId(), ocorrencia.getProtocolo(), ocorrencia.getTipo(),
                ocorrencia.getDescricao(), ocorrencia.getStatus(), ocorrencia.getCriadoEm(),
                ocorrencia.getAtualizadoEm(), paraEnderecoResposta(ocorrencia.getEndereco()),
                new OcorrenciaDetalheResponse.Solicitante(cidadao.getId(), cidadao.getNome(),
                        cidadao.getEmail(), cidadao.getTelefone()),
                equipe == null ? null
                        : new OcorrenciaDetalheResponse.Equipe(equipe.getId(), equipe.getNome(), equipe.getAreaAtuacao()),
                paraHistoricoResposta(ocorrencia.getHistorico()));
    }

    public static AtualizacaoStatusResponse paraAtualizacaoStatusResposta(OcorrenciaEntity ocorrencia,
                                                                          StatusOcorrencia statusAnterior) {
        return new AtualizacaoStatusResponse(ocorrencia.getId(), ocorrencia.getProtocolo(), statusAnterior,
                ocorrencia.getStatus(), ocorrencia.getAtualizadoEm(), paraHistoricoResposta(ocorrencia.getHistorico()));
    }

    private static EnderecoResponse paraEnderecoResposta(EnderecoOcorrencia endereco) {
        return new EnderecoResponse(endereco.getLogradouro(), endereco.getNumero(), endereco.getBairro(),
                endereco.getCidade(), endereco.getUf(), endereco.getReferencia());
    }

    private static List<HistoricoStatusResponse> paraHistoricoResposta(List<HistoricoStatusEntity> historico) {
        return historico.stream()
                .map(h -> new HistoricoStatusResponse(h.getStatusAnterior(), h.getStatusNovo(), h.getObservacao(),
                        h.getEquipeResponsavel() == null ? null : h.getEquipeResponsavel().getNome(),
                        h.getRegistradoEm()))
                .toList();
    }
}
