package br.edu.p447.saneatech.apresentacao;

import br.edu.p447.saneatech.dominio.modelo.Cidadao;
import br.edu.p447.saneatech.dominio.modelo.Ocorrencia;
import br.edu.p447.saneatech.infraestrutura.CidadaoRepository;
import br.edu.p447.saneatech.infraestrutura.OcorrenciaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OcorrenciaControllerTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired CidadaoRepository cidadaos;
    @Autowired OcorrenciaRepository ocorrencias;

    private Map<String, Object> corpo(Long cidadaoId, String tipo) {
        return Map.of(
            "cidadaoId", cidadaoId,
            "tipo", tipo,
            "descricao", "Problema em via pública",
            "endereco", Map.of("logradouro", "Rua A", "numero", "123", "bairro", "Centro",
                "cidade", "João Pessoa", "uf", "pb", "referencia", "Perto da praça")
        );
    }

    @Test
    void registraOcorrenciaPendenteAssociadaAoCidadao() throws Exception {
        Cidadao cidadao = cidadaos.save(new Cidadao("Maria", "maria@teste.com", null));

        mvc.perform(post("/ocorrencias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(corpo(cidadao.getId(), "VAZAMENTO_AGUA")))
            ).andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.status").value("PENDENTE"))
            .andExpect(jsonPath("$.tipo").value("VAZAMENTO_AGUA"))
            .andExpect(jsonPath("$.cidadaoId").value(cidadao.getId()))
            .andExpect(jsonPath("$.protocolo").value(org.hamcrest.Matchers.startsWith("SAN-")));

        Ocorrencia ocorrencia = ocorrencias.findAll().get(0);
        assertEquals(cidadao.getId(), ocorrencia.getSolicitante().getId());
        assertEquals(1, ocorrencia.getHistorico().size());
        assertNotNull(ocorrencia.getProtocolo());
    }

    @Test
    void aceitaTresTipos() throws Exception {
        Cidadao cidadao = cidadaos.save(new Cidadao("João", "joao@teste.com", null));
        for (String tipo : new String[]{"VAZAMENTO_AGUA", "ESGOTO_CEU_ABERTO", "FALTA_ABASTECIMENTO"}) {
            mvc.perform(post("/ocorrencias")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json.writeValueAsString(corpo(cidadao.getId(), tipo))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipo").value(tipo));
        }
    }

    @Test
    void cidadaoInexistenteRetorna404() throws Exception {
        mvc.perform(post("/ocorrencias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(corpo(9999L, "VAZAMENTO_AGUA"))))
            .andExpect(status().isNotFound());
    }

    @Test
    void dadosInvalidosRetornam400() throws Exception {
        mvc.perform(post("/ocorrencias")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"tipo\":\"VAZAMENTO_AGUA\"}"))
            .andExpect(status().isBadRequest());
    }
}
