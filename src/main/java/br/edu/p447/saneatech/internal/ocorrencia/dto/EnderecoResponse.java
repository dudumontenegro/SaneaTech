package br.edu.p447.saneatech.internal.ocorrencia.dto;

public record EnderecoResponse(
        String logradouro,
        String numero,
        String bairro,
        String cidade,
        String uf,
        String referencia) {
}
