package br.edu.p447.saneatech.infra;

import br.edu.p447.saneatech.dominio.enums.StatusOcorrencia;
import br.edu.p447.saneatech.dominio.enums.TipoOcorrencia;
import br.edu.p447.saneatech.dominio.modelo.Cidadao;
import br.edu.p447.saneatech.dominio.modelo.EnderecoOcorrencia;
import br.edu.p447.saneatech.dominio.modelo.EquipeResponsavel;
import br.edu.p447.saneatech.dominio.modelo.Ocorrencia;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class PersistenciaJpaTest {
    @Test void persisteRelacionamentosEHistoricoEmBancoH2() {
        String url = "jdbc:h2:mem:saneatech_test_" + System.nanoTime() + ";DB_CLOSE_DELAY=-1";
        try (EntityManagerFactory fabrica = Persistence.createEntityManagerFactory(
                "saneatech-demo", Map.of("jakarta.persistence.jdbc.url", url))) {
            EntityManager em = fabrica.createEntityManager();
            Long id;
            try {
                em.getTransaction().begin();
                Cidadao cidadao = new Cidadao("Carlos", "carlos@exemplo.com", null);
                EquipeResponsavel equipe = new EquipeResponsavel("Equipe Esgoto", "Manutenção");
                Ocorrencia ocorrencia = Ocorrencia.abrir(cidadao, TipoOcorrencia.ESGOTO_CEU_ABERTO,
                    "Esgoto exposto", new EnderecoOcorrencia("Rua 1", "15", "Centro", "João Pessoa", "PB", null));
                ocorrencia.atribuirEquipe(equipe);
                ocorrencia.iniciarAtendimento("Equipe no local");
                ocorrencia.concluirAtendimento("Serviço finalizado");
                em.persist(cidadao);
                em.persist(equipe);
                em.persist(ocorrencia);
                em.getTransaction().commit();
                id = ocorrencia.getId();
                em.clear();
                Ocorrencia recuperada = em.find(Ocorrencia.class, id);
                assertNotNull(recuperada);
                assertEquals(StatusOcorrencia.CONCLUIDA, recuperada.getStatus());
                assertEquals(3, recuperada.getHistorico().size());
                assertEquals("Carlos", recuperada.getSolicitante().getNome());
                assertEquals("Equipe Esgoto", recuperada.getEquipeResponsavel().getNome());
                assertEquals("João Pessoa", recuperada.getEndereco().getCidade());
                assertEquals(2, recuperada.getEquipeResponsavel().getAlteracoesRegistradas().size());
            } catch (RuntimeException erro) {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                throw erro;
            } finally {
                em.close();
            }
        }
    }
}
