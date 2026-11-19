package br.edu.p447.saneatech.aplicacao;

/** Lançada quando o id informado não corresponde a nenhuma ocorrência. */
public class OcorrenciaNaoEncontradaException extends RuntimeException {
    public OcorrenciaNaoEncontradaException(Long id) {
        super("Ocorrência " + id + " não encontrada.");
    }
}
