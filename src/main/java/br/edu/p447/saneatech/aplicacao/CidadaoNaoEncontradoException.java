package br.edu.p447.saneatech.aplicacao;

public class CidadaoNaoEncontradoException extends RuntimeException {
    public CidadaoNaoEncontradoException(Long id) {
        super("Cidadão não encontrado: " + id);
    }
}
