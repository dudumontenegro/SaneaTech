package br.edu.p447.saneatech.internal.ocorrencia;

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.edu.p447.saneatech.internal.cidadao.repository.CidadaoRepository;
import br.edu.p447.saneatech.internal.ocorrencia.repository.OcorrenciaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/** Registro e consulta de ocorrências. Seed: 4 ocorrências SAN-SEED-0001..0004. */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OcorrenciaControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    OcorrenciaRepository ocorrencias;

    @Autowired
    CidadaoRepository cidadaos;

    private String corpo(Long cidadaoId) {
        return """
                {"cidadaoId": %d, "tipo": "VAZAMENTO_AGUA", "descricao": "Cano estourado",
                 "endereco": {"logradouro": "Rua A", "numero": "10", "bairro": "Centro",
                              "cidade": "João Pessoa", "uf": "pb"}}
                """.formatted(cidadaoId);
    }

    @Test
    void listaOcorrencias() throws Exception {
        mvc.perform(get("/ocorrencias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0].protocolo").value("SAN-SEED-0001"))
                .andExpect(jsonPath("$[0].equipe").value("Equipe de Redes"))
                .andExpect(jsonPath("$[3].equipe").doesNotExist());
    }

    @Test
    void filtraPorStatus() throws Exception {
        mvc.perform(get("/ocorrencias").param("status", "PENDENTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
        mvc.perform(get("/ocorrencias").param("status", "INVALIDO"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void buscaOcorrenciaPorIdComHistorico() throws Exception {
        Long id = ocorrencias.findByProtocolo("SAN-SEED-0003").orElseThrow().getId();
        mvc.perform(get("/ocorrencias/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONCLUIDA"))
                .andExpect(jsonPath("$.solicitante.nome").value("Maria Silva"))
                .andExpect(jsonPath("$.equipeResponsavel.nome").value("Equipe de Redes"))
                .andExpect(jsonPath("$.historico.length()").value(3))
                .andExpect(jsonPath("$.historico[2].statusNovo").value("CONCLUIDA"));
    }

    @Test
    void ocorrenciaInexistenteRetorna404() throws Exception {
        mvc.perform(get("/ocorrencias/{id}", 9999)).andExpect(status().isNotFound());
    }

    @Test
    void registraOcorrenciaPendente() throws Exception {
        Long cidadaoId = cidadaos.findAllByOrderByIdAsc().get(0).getId();
        mvc.perform(post("/ocorrencias").contentType(MediaType.APPLICATION_JSON).content(corpo(cidadaoId)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.status").value("PENDENTE"))
                .andExpect(jsonPath("$.protocolo").value(startsWith("SAN-")))
                .andExpect(jsonPath("$.endereco.uf").value("PB"));
    }

    @Test
    void registrarComCidadaoInexistenteRetorna404() throws Exception {
        mvc.perform(post("/ocorrencias").contentType(MediaType.APPLICATION_JSON).content(corpo(9999L)))
                .andExpect(status().isNotFound());
    }
}
