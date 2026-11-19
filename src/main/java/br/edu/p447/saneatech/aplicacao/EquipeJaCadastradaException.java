package br.edu.p447.saneatech.aplicacao;

/** Já existe uma equipe com o mesmo nome (comparação sem diferenciar maiúsculas). */
public class EquipeJaCadastradaException extends RuntimeException {
    public EquipeJaCadastradaException(String nome) {
        super("Já existe uma equipe cadastrada com o nome \"" + nome + "\".");
    }
}
