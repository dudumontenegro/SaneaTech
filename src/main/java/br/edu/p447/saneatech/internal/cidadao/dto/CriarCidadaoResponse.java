package br.edu.p447.saneatech.internal.cidadao.dto;

public record CriarCidadaoResponse(
        Long id,
        String nome,
        String email,
        String telefone) {
}
