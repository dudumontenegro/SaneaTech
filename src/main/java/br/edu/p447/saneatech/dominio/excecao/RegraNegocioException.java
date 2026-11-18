package br.edu.p447.saneatech.dominio.excecao;

/** Erro esperado quando uma operação viola as regras do domínio. */
public class RegraNegocioException extends IllegalStateException {
    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
