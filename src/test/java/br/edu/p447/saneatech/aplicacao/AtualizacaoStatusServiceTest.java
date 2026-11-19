package br.edu.p447.saneatech.aplicacao;

import br.edu.p447.saneatech.dominio.enums.StatusOcorrencia;
import org.junit.jupiter.api.Test;
import static br.edu.p447.saneatech.dominio.enums.StatusOcorrencia.*;
import static org.junit.jupiter.api.Assertions.*;

class AtualizacaoStatusServiceTest {
    @Test void permiteSomenteAvancosDeUmaEtapa() {
        assertDoesNotThrow(() -> AtualizacaoStatusService.validarTransicao(PENDENTE, EM_ANDAMENTO));
        assertDoesNotThrow(() -> AtualizacaoStatusService.validarTransicao(EM_ANDAMENTO, CONCLUIDA));
    }

    @Test void rejeitaPulosRetrocessosEMesmoStatus() {
        StatusOcorrencia[][] invalidas = {
            {PENDENTE, CONCLUIDA}, {PENDENTE, PENDENTE},
            {EM_ANDAMENTO, PENDENTE}, {EM_ANDAMENTO, EM_ANDAMENTO},
            {CONCLUIDA, PENDENTE}, {CONCLUIDA, EM_ANDAMENTO}, {CONCLUIDA, CONCLUIDA}
        };
        for (StatusOcorrencia[] t : invalidas) {
            assertThrows(TransicaoStatusInvalidaException.class,
                () -> AtualizacaoStatusService.validarTransicao(t[0], t[1]), t[0] + " -> " + t[1]);
        }
    }
}
