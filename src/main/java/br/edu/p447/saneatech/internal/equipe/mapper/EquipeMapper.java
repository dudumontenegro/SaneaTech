package br.edu.p447.saneatech.internal.equipe.mapper;

import br.edu.p447.saneatech.internal.equipe.dto.CriarEquipeRequest;
import br.edu.p447.saneatech.internal.equipe.dto.CriarEquipeResponse;
import br.edu.p447.saneatech.internal.equipe.dto.EquipeResumoResponse;
import br.edu.p447.saneatech.internal.equipe.entity.EquipeResponsavelEntity;

public final class EquipeMapper {

    private EquipeMapper() {
    }

    public static EquipeResponsavelEntity converteParaEntidade(CriarEquipeRequest request) {
        return new EquipeResponsavelEntity(request.nome(), request.areaAtuacao());
    }

    public static CriarEquipeResponse converterParaResposta(EquipeResponsavelEntity equipe) {
        return new CriarEquipeResponse(equipe.getId(), equipe.getNome(), equipe.getAreaAtuacao());
    }

    public static EquipeResumoResponse paraResumoResposta(EquipeResponsavelEntity equipe) {
        return new EquipeResumoResponse(equipe.getId(), equipe.getNome(), equipe.getAreaAtuacao());
    }
}
