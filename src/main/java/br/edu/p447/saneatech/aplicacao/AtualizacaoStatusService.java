package br.edu.p447.saneatech.aplicacao;

import br.edu.p447.saneatech.dominio.enums.StatusOcorrencia;
import br.edu.p447.saneatech.dominio.modelo.Ocorrencia;
import br.edu.p447.saneatech.infraestrutura.OcorrenciaRepository;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso de atualização do andamento de uma ocorrência.
 * Valida a transição pedida e delega a mudança ao domínio, que registra o evento no histórico.
 */
@Service
public class AtualizacaoStatusService {
    private final OcorrenciaRepository ocorrencias;

    public AtualizacaoStatusService(OcorrenciaRepository ocorrencias) {
        this.ocorrencias = ocorrencias;
    }

    @Transactional
    public AtualizacaoStatusResultado atualizar(Long id, StatusOcorrencia novoStatus, String observacao) {
        Objects.requireNonNull(novoStatus, "Status obrigatório.");
        Ocorrencia ocorrencia = ocorrencias.findById(id)
            .orElseThrow(() -> new OcorrenciaNaoEncontradaException(id));

        StatusOcorrencia atual = ocorrencia.getStatus();
        validarTransicao(atual, novoStatus);

        switch (novoStatus) {
            case EM_ANDAMENTO -> ocorrencia.iniciarAtendimento(observacao);
            case CONCLUIDA -> ocorrencia.concluirAtendimento(observacao);
            default -> throw new TransicaoStatusInvalidaException(atual, novoStatus);
        }
        // Entidade gerenciada: o novo HistoricoStatus é persistido por cascade no commit.
        ocorrencias.flush();
        return AtualizacaoStatusResultado.de(ocorrencia, atual);
    }

    /** Somente avanços de uma etapa são permitidos: PENDENTE → EM_ANDAMENTO → CONCLUIDA. */
    static void validarTransicao(StatusOcorrencia atual, StatusOcorrencia novo) {
        boolean permitida = (atual == StatusOcorrencia.PENDENTE && novo == StatusOcorrencia.EM_ANDAMENTO)
            || (atual == StatusOcorrencia.EM_ANDAMENTO && novo == StatusOcorrencia.CONCLUIDA);
        if (!permitida) throw new TransicaoStatusInvalidaException(atual, novo);
    }
}
