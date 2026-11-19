package br.edu.p447.saneatech.internal.cidadao;

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CidadaoControllerTest {

    @Autowired
    MockMvc mvc;

    private ResultActions postar(String json) throws Exception {
        return mvc.perform(post("/cidadaos").contentType(MediaType.APPLICATION_JSON).content(json));
    }

    @Test
    void cadastraCidadaoERetorna201() throws Exception {
        postar("{\"nome\":\" João Lima \",\"email\":\"JOAO@Teste.com\",\"telefone\":\"83988887777\"}")
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", matchesPattern("/cidadaos/\\d+")))
                .andExpect(jsonPath("$.nome").value("João Lima"))
                .andExpect(jsonPath("$.email").value("joao@teste.com"));
    }

    @Test
    void listaCidadaosIncluindoSeed() throws Exception {
        postar("{\"nome\":\"Ana\",\"email\":\"ana@teste.com\"}").andExpect(status().isCreated());
        mvc.perform(get("/cidadaos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].email").value("maria@saneatech.exemplo"))
                .andExpect(jsonPath("$[1].email").value("ana@teste.com"));
    }

    @Test
    void emailDuplicadoRetorna409() throws Exception {
        postar("{\"nome\":\"Outra Maria\",\"email\":\"MARIA@saneatech.exemplo\"}")
                .andExpect(status().isConflict());
    }

    @Test
    void dadosInvalidosRetornam400() throws Exception {
        postar("{\"nome\":\"\",\"email\":\"nao-e-email\"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.nome").value("Nome é obrigatório."))
                .andExpect(jsonPath("$.erros.email").value("E-mail inválido."));
    }
}
