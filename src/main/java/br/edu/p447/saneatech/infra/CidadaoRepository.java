package br.edu.p447.saneatech.infra;

import br.edu.p447.saneatech.dominio.modelo.Cidadao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.Objects;

public class CidadaoRepository {
    private final EntityManagerFactory fabrica;

    public CidadaoRepository(EntityManagerFactory fabrica) {
        this.fabrica = Objects.requireNonNull(fabrica);
    }

    public Cidadao salvar(Cidadao cidadao) {
        EntityManager em = fabrica.createEntityManager();
        EntityTransaction transacao = em.getTransaction();

        try {
            transacao.begin();
            em.persist(cidadao);
            transacao.commit();
            return cidadao;
        } catch (RuntimeException erro) {
            if (transacao.isActive()) {
                transacao.rollback();
            }
            throw erro;
        } finally {
            em.close();
        }
    }

    public List<Cidadao> listar() {
        try (EntityManager em = fabrica.createEntityManager()) {
            return em.createQuery(
                    "SELECT c FROM Cidadao c ORDER BY c.id",
                    Cidadao.class
            ).getResultList();
        }
    }
}