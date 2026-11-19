package br.edu.p447.saneatech.internal.ocorrencia;

import static br.edu.p447.saneatech.internal.ocorrencia.entity.StatusOcorrencia.CONCLUIDA;
import static br.edu.p447.saneatech.internal.ocorrencia.entity.StatusOcorrencia.EM_ANDAMENTO;
import static br.edu.p447.saneatech.internal.ocorrencia.entity.StatusOcorrencia.PENDENTE;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.edu.p447.saneatech.internal.ocorrencia.entity.StatusOcorrencia;
import br.edu.p447.saneatech.internal.ocorrencia.service.AtualizacaoStatusService;
import br.edu.p447.saneatech.shared.exception.RegraNegocioException;
import org.junit.jupiter.api.Test;

class AtualizacaoStatusServiceTest {

    @Test
    void permiteSomenteAvancosDeUmaEtapa() {
        assertDoesNotThrow(() -> AtualizacaoStatusService.validarTransicao(PENDENTE, EM_ANDAMENTO));
        assertDoesNotThrow(() -> AtualizacaoStatusService.validarTransicao(EM_ANDAMENTO, CONCLUIDA));
    }

    @Test
    void rejeitaPulosRetrocessosEMesmoStatus() {
        StatusOcorrencia[][] invalidas = {
            {PENDENTE, CONCLUIDA}, {PENDENTE, PENDENTE},
            {EM_ANDAMENTO, PENDENTE}, {EM_ANDAMENTO, EM_ANDAMENTO},
            {CONCLUIDA, PENDENTE}, {CONCLUIDA, EM_ANDAMENTO}, {CONCLUIDA, CONCLUIDA}
        };
        for (StatusOcorrencia[] t : invalidas) {
            assertThrows(RegraNegocioException.class,
                    () -> AtualizacaoStatusService.validarTransicao(t[0], t[1]), t[0] + " -> " + t[1]);
        }
    }
}
