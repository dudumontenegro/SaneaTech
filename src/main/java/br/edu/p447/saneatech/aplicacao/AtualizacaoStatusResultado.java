package br.edu.p447.saneatech.aplicacao;

import br.edu.p447.saneatech.dominio.enums.StatusOcorrencia;
import br.edu.p447.saneatech.dominio.modelo.HistoricoStatus;
import br.edu.p447.saneatech.dominio.modelo.Ocorrencia;
import java.time.Instant;
import java.util.List;

/** Estado da ocorrência após a atualização, incluindo o histórico completo. */
public record AtualizacaoStatusResultado(
        Long id,
        String protocolo,
        StatusOcorrencia statusAnterior,
        StatusOcorrencia status,
        Instant atualizadoEm,
        List<Evento> historico) {

    public record Evento(StatusOcorrencia statusAnterior, StatusOcorrencia statusNovo,
                         String observacao, String equipe, Instant registradoEm) {
        static Evento de(HistoricoStatus h) {
            String equipe = h.getEquipeResponsavel() == null ? null : h.getEquipeResponsavel().getNome();
            return new Evento(h.getStatusAnterior(), h.getStatusNovo(), h.getObservacao(), equipe, h.getRegistradoEm());
        }
    }

    static AtualizacaoStatusResultado de(Ocorrencia o, StatusOcorrencia statusAnterior) {
        return new AtualizacaoStatusResultado(o.getId(), o.getProtocolo(), statusAnterior, o.getStatus(),
            o.getAtualizadoEm(), o.getHistorico().stream().map(Evento::de).toList());
    }
}
