package br.edu.p447.saneatech.dominio;

import br.edu.p447.saneatech.dominio.enums.StatusOcorrencia;
import br.edu.p447.saneatech.dominio.enums.TipoOcorrencia;
import br.edu.p447.saneatech.dominio.excecao.RegraNegocioException;
import br.edu.p447.saneatech.dominio.modelo.Cidadao;
import br.edu.p447.saneatech.dominio.modelo.EnderecoOcorrencia;
import br.edu.p447.saneatech.dominio.modelo.EquipeResponsavel;
import br.edu.p447.saneatech.dominio.modelo.Ocorrencia;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OcorrenciaTest {
    private Cidadao cidadao() { return new Cidadao("Ana Souza", "ANA@EXEMPLO.COM", "83999998888"); }
    private EnderecoOcorrencia endereco() {
        return new EnderecoOcorrencia("Rua A", "10", "Centro", "João Pessoa", "pb", "Próximo ao mercado");
    }
    private Ocorrencia nova(Cidadao cidadao) {
        return Ocorrencia.abrir(cidadao, TipoOcorrencia.ESGOTO_CEU_ABERTO, "Esgoto na rua", endereco());
    }

    @Test void abrirDeveCriarPendenteComProtocoloEHistorico() {
        Cidadao solicitante = cidadao();
        Ocorrencia ocorrencia = nova(solicitante);
        assertEquals(StatusOcorrencia.PENDENTE, ocorrencia.getStatus());
        assertTrue(ocorrencia.getProtocolo().startsWith("SAN-"));
        assertEquals(40, ocorrencia.getProtocolo().length());
        assertEquals(1, ocorrencia.getHistorico().size());
        assertNull(ocorrencia.getHistorico().get(0).getStatusAnterior());
        assertEquals(StatusOcorrencia.PENDENTE, ocorrencia.getHistorico().get(0).getStatusNovo());
        assertNull(ocorrencia.getHistorico().get(0).getEquipeResponsavel());
        assertEquals(1, solicitante.getOcorrencias().size());
        assertEquals("ana@exemplo.com", solicitante.getEmail());
        assertEquals("PB", ocorrencia.getEndereco().getUf());
    }

    @Test void fluxoCompletoGeraHistoricoComResponsavel() {
        Ocorrencia ocorrencia = nova(cidadao());
        EquipeResponsavel equipe = new EquipeResponsavel("Equipe Central", "Esgoto");
        ocorrencia.atribuirEquipe(equipe);
        ocorrencia.iniciarAtendimento("Vistoria iniciada");
        ocorrencia.concluirAtendimento("Obstrução removida");
        assertEquals(StatusOcorrencia.CONCLUIDA, ocorrencia.getStatus());
        assertEquals(3, ocorrencia.getHistorico().size());
        assertEquals(StatusOcorrencia.PENDENTE, ocorrencia.getHistorico().get(1).getStatusAnterior());
        assertEquals(StatusOcorrencia.CONCLUIDA, ocorrencia.getHistorico().get(2).getStatusNovo());
        assertSame(equipe, ocorrencia.getHistorico().get(2).getEquipeResponsavel());
        assertEquals(2, equipe.getAlteracoesRegistradas().size());
    }

    @Test void naoPermiteIniciarSemEquipeNemConcluirAntesDeIniciar() {
        Ocorrencia ocorrencia = nova(cidadao());
        assertThrows(RegraNegocioException.class, () -> ocorrencia.iniciarAtendimento("Vistoria"));
        assertThrows(RegraNegocioException.class, () -> ocorrencia.concluirAtendimento("Concluído"));
        assertEquals(StatusOcorrencia.PENDENTE, ocorrencia.getStatus());
    }

    @Test void impedeSaltosERepeticoesDeStatus() {
        Ocorrencia ocorrencia = nova(cidadao());
        ocorrencia.atribuirEquipe(new EquipeResponsavel("Equipe Azul", "Água"));
        ocorrencia.iniciarAtendimento("Iniciado");
        assertThrows(RegraNegocioException.class, () -> ocorrencia.iniciarAtendimento("Repetido"));
        ocorrencia.concluirAtendimento("Concluído");
        assertThrows(RegraNegocioException.class, () -> ocorrencia.concluirAtendimento("Repetido"));
        assertEquals(3, ocorrencia.getHistorico().size());
    }

    @Test void corrigeDadosApenasEnquantoPendente() {
        Ocorrencia ocorrencia = nova(cidadao());
        ocorrencia.atualizarDescricao("Descrição corrigida");
        ocorrencia.corrigirLocalizacao(new EnderecoOcorrencia("Rua B", null, "Altiplano", "João Pessoa", "PB", null));
        assertEquals("Descrição corrigida", ocorrencia.getDescricao());
        assertEquals("S/N", ocorrencia.getEndereco().getNumero());
        ocorrencia.atribuirEquipe(new EquipeResponsavel("Equipe Azul", "Água"));
        ocorrencia.iniciarAtendimento("Visita iniciada");
        assertThrows(RegraNegocioException.class, () -> ocorrencia.atualizarDescricao("Não pode"));
        assertThrows(RegraNegocioException.class, () -> ocorrencia.corrigirLocalizacao(endereco()));
    }

    @Test void reatribuicaoMantemAsDuasPontasDoRelacionamento() {
        Ocorrencia ocorrencia = nova(cidadao());
        EquipeResponsavel antiga = new EquipeResponsavel("A", "Água");
        EquipeResponsavel nova = new EquipeResponsavel("B", "Água");
        ocorrencia.atribuirEquipe(antiga);
        ocorrencia.atribuirEquipe(nova);
        assertTrue(antiga.getOcorrencias().isEmpty());
        assertEquals(1, nova.getOcorrencias().size());
        assertSame(nova, ocorrencia.getEquipeResponsavel());
    }

    @Test void validaEntradasEImpedeAlterarColecoesPorFora() {
        assertThrows(IllegalArgumentException.class, () -> cidadao().atualizarContato("email-sem-arroba", null));
        assertThrows(IllegalArgumentException.class, () -> new EquipeResponsavel("", "Água"));
        assertThrows(IllegalArgumentException.class, () -> new EnderecoOcorrencia("Rua", "1", "B", "C", "P", ""));
        Ocorrencia ocorrencia = nova(cidadao());
        assertThrows(IllegalArgumentException.class, () -> ocorrencia.atualizarDescricao(" "));
        assertThrows(UnsupportedOperationException.class, () -> ocorrencia.getHistorico().clear());
    }

    @Test void observacaoInvalidaNaoMudaEstado() {
        Ocorrencia ocorrencia = nova(cidadao());
        ocorrencia.atribuirEquipe(new EquipeResponsavel("Equipe", "Redes"));
        assertThrows(IllegalArgumentException.class, () -> ocorrencia.iniciarAtendimento(""));
        assertEquals(StatusOcorrencia.PENDENTE, ocorrencia.getStatus());
        assertEquals(1, ocorrencia.getHistorico().size());
    }
}
