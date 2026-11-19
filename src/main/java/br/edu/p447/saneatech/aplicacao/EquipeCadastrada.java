package br.edu.p447.saneatech.aplicacao;

import br.edu.p447.saneatech.dominio.modelo.EquipeResponsavel;

/** Dados da equipe após o cadastro. */
public record EquipeCadastrada(Long id, String nome, String areaAtuacao) {
    static EquipeCadastrada de(EquipeResponsavel equipe) {
        return new EquipeCadastrada(equipe.getId(), equipe.getNome(), equipe.getAreaAtuacao());
    }
}
