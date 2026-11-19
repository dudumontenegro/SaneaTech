package br.edu.p447.saneatech.internal.cidadao.mapper;

import br.edu.p447.saneatech.internal.cidadao.dto.CidadaoResumoResponse;
import br.edu.p447.saneatech.internal.cidadao.dto.CriarCidadaoRequest;
import br.edu.p447.saneatech.internal.cidadao.dto.CriarCidadaoResponse;
import br.edu.p447.saneatech.internal.cidadao.entity.CidadaoEntity;

public final class CidadaoMapper {

    private CidadaoMapper() {
    }

    public static CidadaoEntity converteParaEntidade(CriarCidadaoRequest request) {
        return new CidadaoEntity(request.nome(), request.email(), request.telefone());
    }

    public static CriarCidadaoResponse converterParaResposta(CidadaoEntity cidadao) {
        return new CriarCidadaoResponse(cidadao.getId(), cidadao.getNome(), cidadao.getEmail(), cidadao.getTelefone());
    }

    public static CidadaoResumoResponse paraResumoResposta(CidadaoEntity cidadao) {
        return new CidadaoResumoResponse(cidadao.getId(), cidadao.getNome(), cidadao.getEmail(), cidadao.getTelefone());
    }
}
