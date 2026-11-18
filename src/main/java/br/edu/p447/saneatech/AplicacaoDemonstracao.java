package br.edu.p447.saneatech;

import br.edu.p447.saneatech.dominio.enums.TipoOcorrencia;
import br.edu.p447.saneatech.dominio.modelo.Cidadao;
import br.edu.p447.saneatech.dominio.modelo.EnderecoOcorrencia;
import br.edu.p447.saneatech.dominio.modelo.EquipeResponsavel;
import br.edu.p447.saneatech.dominio.modelo.Ocorrencia;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/** Demonstra o modelo JPA em memória; NÃO é Controller, Service nem endpoint. */
public final class AplicacaoDemonstracao {
    private AplicacaoDemonstracao() { }

    public static void main(String[] args) {
        try (EntityManagerFactory fabrica = Persistence.createEntityManagerFactory("saneatech-demo")) {
            EntityManager em = fabrica.createEntityManager();
            try {
                em.getTransaction().begin();
                Cidadao cidadao = new Cidadao("Maria Silva", "maria@saneatech.exemplo", "83999990000");
                EquipeResponsavel equipe = new EquipeResponsavel("Equipe de Redes", "Água e esgoto");
                Ocorrencia ocorrencia = Ocorrencia.abrir(cidadao, TipoOcorrencia.VAZAMENTO_AGUA,
                    "Vazamento de água em via pública",
                    new EnderecoOcorrencia("Rua das Flores", "120", "Centro", "João Pessoa", "PB", "Próximo à praça"));

                ocorrencia.atribuirEquipe(equipe);
                ocorrencia.iniciarAtendimento("Equipe deslocada ao endereço.");
                ocorrencia.concluirAtendimento("Vazamento reparado.");

                em.persist(cidadao);
                em.persist(equipe);
                em.persist(ocorrencia); // historico é salvo por cascade ALL
                em.getTransaction().commit();

                System.out.println("SaneaTech | protocolo: " + ocorrencia.getProtocolo());
                System.out.println("Situação final: " + ocorrencia.getStatus());
                System.out.println("Eventos no histórico: " + ocorrencia.getHistorico().size());
                System.out.println("Ocorrências persistidas: " + em.createQuery(
                    "SELECT COUNT(o) FROM Ocorrencia o", Long.class).getSingleResult());
            } catch (RuntimeException erro) {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                throw erro;
            } finally {
                em.close();
            }
        }
    }
}
