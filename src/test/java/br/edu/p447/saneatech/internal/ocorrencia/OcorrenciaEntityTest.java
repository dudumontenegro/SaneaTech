package br.edu.p447.saneatech.internal.ocorrencia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.p447.saneatech.internal.cidadao.entity.CidadaoEntity;
import br.edu.p447.saneatech.internal.equipe.entity.EquipeResponsavelEntity;
import br.edu.p447.saneatech.internal.ocorrencia.entity.EnderecoOcorrencia;
import br.edu.p447.saneatech.internal.ocorrencia.entity.OcorrenciaEntity;
import br.edu.p447.saneatech.internal.ocorrencia.entity.StatusOcorrencia;
import br.edu.p447.saneatech.internal.ocorrencia.entity.TipoOcorrencia;
import br.edu.p447.saneatech.shared.exception.RegraNegocioException;
import org.junit.jupiter.api.Test;

class OcorrenciaEntityTest {

    private CidadaoEntity cidadao() {
        return new CidadaoEntity("Ana Souza", "ANA@EXEMPLO.COM", "83999998888");
    }

    private OcorrenciaEntity nova(CidadaoEntity cidadao) {
        return OcorrenciaEntity.abrir(cidadao, TipoOcorrencia.ESGOTO_CEU_ABERTO, "Esgoto na rua",
                new EnderecoOcorrencia("Rua A", "10", "Centro", "João Pessoa", "pb", null));
    }

    @Test
    void abrirCriaPendenteComProtocoloEHistorico() {
        CidadaoEntity solicitante = cidadao();
        OcorrenciaEntity ocorrencia = nova(solicitante);
        assertEquals(StatusOcorrencia.PENDENTE, ocorrencia.getStatus());
        assertTrue(ocorrencia.getProtocolo().startsWith("SAN-"));
        assertEquals(40, ocorrencia.getProtocolo().length());
        assertEquals(1, ocorrencia.getHistorico().size());
        assertNull(ocorrencia.getHistorico().get(0).getStatusAnterior());
        assertEquals(1, solicitante.getOcorrencias().size());
        assertEquals("ana@exemplo.com", solicitante.getEmail());
        assertEquals("PB", ocorrencia.getEndereco().getUf());
    }

    @Test
    void fluxoCompletoGeraHistoricoComEquipe() {
        OcorrenciaEntity ocorrencia = nova(cidadao());
        EquipeResponsavelEntity equipe = new EquipeResponsavelEntity("Equipe Central", "Esgoto");
        ocorrencia.atribuirEquipe(equipe);
        ocorrencia.iniciarAtendimento("Vistoria iniciada");
        ocorrencia.concluirAtendimento("Obstrução removida");
        assertEquals(StatusOcorrencia.CONCLUIDA, ocorrencia.getStatus());
        assertEquals(3, ocorrencia.getHistorico().size());
        assertSame(equipe, ocorrencia.getHistorico().get(2).getEquipeResponsavel());
        assertEquals(2, equipe.getAlteracoesRegistradas().size());
    }

    @Test
    void naoIniciaSemEquipeNemConcluiPendente() {
        OcorrenciaEntity ocorrencia = nova(cidadao());
        assertThrows(RegraNegocioException.class, () -> ocorrencia.iniciarAtendimento("Sem equipe"));
        assertThrows(RegraNegocioException.class, () -> ocorrencia.concluirAtendimento("Pular"));
    }

    @Test
    void validaDadosDoCidadaoEEndereco() {
        assertThrows(IllegalArgumentException.class, () -> new CidadaoEntity("Ana", "invalido", null));
        assertThrows(IllegalArgumentException.class,
                () -> new EnderecoOcorrencia("Rua", "1", "Centro", "JP", "P1", null));
    }
}
