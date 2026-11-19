package br.edu.p447.saneatech.api;

import br.edu.p447.saneatech.SaneaTechApplication;
import br.edu.p447.saneatech.dominio.enums.TipoOcorrencia;
import br.edu.p447.saneatech.dominio.modelo.Cidadao;
import br.edu.p447.saneatech.dominio.modelo.EnderecoOcorrencia;
import br.edu.p447.saneatech.dominio.modelo.Ocorrencia;
import br.edu.p447.saneatech.infra.repository.OcorrenciaRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.support.TransactionTemplate;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = SaneaTechApplication.class)
@AutoConfigureMockMvc
class OcorrenciaControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private OcorrenciaRepository ocorrenciaRepository;

    private Long ocorrenciaId;

    @BeforeEach
    void prepararOcorrencia() {
        ocorrenciaRepository.deleteAll();
        ocorrenciaId = transactionTemplate.execute(transacao -> {
            Cidadao cidadao = new Cidadao("Ana", "ana" + System.nanoTime() + "@exemplo.com", "83999990000");
            entityManager.persist(cidadao);
            Ocorrencia ocorrencia = Ocorrencia.abrir(cidadao, TipoOcorrencia.VAZAMENTO_AGUA,
                "Vazamento na rua", new EnderecoOcorrencia("Rua das Flores", "10", "Centro",
                    "João Pessoa", "PB", null));
            entityManager.persist(ocorrencia);
            return ocorrencia.getId();
        });
    }

    @Test
    void listaOcorrencias() throws Exception {
        mockMvc.perform(get("/ocorrencias").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].descricao").value("Vazamento na rua"));
    }

    @Test
    void consultaOcorrenciaPeloId() throws Exception {
        mockMvc.perform(get("/ocorrencias/{id}", ocorrenciaId).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(ocorrenciaId))
            .andExpect(jsonPath("$.solicitante.nome").value("Ana"))
            .andExpect(jsonPath("$.endereco.cidade").value("João Pessoa"))
            .andExpect(jsonPath("$.historico", hasSize(1)));
    }

    @Test
    void retorna404QuandoOcorrenciaNaoExiste() throws Exception {
        mockMvc.perform(get("/ocorrencias/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }
}