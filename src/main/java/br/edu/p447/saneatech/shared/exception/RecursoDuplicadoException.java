package br.edu.p447.saneatech.shared.exception;

/** Já existe um registro com o mesmo valor em um campo que deve ser único. */
public class RecursoDuplicadoException extends RuntimeException {

    public RecursoDuplicadoException(String mensagem) {
        super(mensagem);
    }
}
