package br.edu.p447.saneatech.internal.ocorrencia.entity;

/** Estados permitidos: PENDENTE -> EM_ANDAMENTO -> CONCLUIDA. */
public enum StatusOcorrencia {
    PENDENTE,
    EM_ANDAMENTO,
    CONCLUIDA;

    /** Próximo status válido no fluxo, ou {@code null} se o status é final. */
    public StatusOcorrencia proximo() {
        return switch (this) {
            case PENDENTE -> EM_ANDAMENTO;
            case EM_ANDAMENTO -> CONCLUIDA;
            case CONCLUIDA -> null;
        };
    }
}
