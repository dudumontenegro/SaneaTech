package br.edu.p447.saneatech.aplicacao;

import br.edu.p447.saneatech.dominio.enums.StatusOcorrencia;
import br.edu.p447.saneatech.dominio.excecao.RegraNegocioException;

/** Lançada quando a transição pedida não segue PENDENTE → EM_ANDAMENTO → CONCLUIDA. */
public class TransicaoStatusInvalidaException extends RegraNegocioException {
    public TransicaoStatusInvalidaException(StatusOcorrencia atual, StatusOcorrencia solicitado) {
        super("Transição de status inválida: " + atual + " → " + solicitado
            + ". Fluxo permitido: PENDENTE → EM_ANDAMENTO → CONCLUIDA.");
    }
}
