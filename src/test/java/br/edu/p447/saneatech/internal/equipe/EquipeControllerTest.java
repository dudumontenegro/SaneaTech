package br.edu.p447.saneatech.internal.equipe;

import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.edu.p447.saneatech.internal.equipe.repository.EquipeResponsavelRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

/** Consulta (GET) e cadastro (POST) de equipes. Seed: 2 equipes. */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EquipeControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    EquipeResponsavelRepository equipes;

    private ResultActions postar(String json) throws Exception {
        return mvc.perform(post("/equipes").contentType(MediaType.APPLICATION_JSON).content(json));
    }

    @Test
    void listaEquipes() throws Exception {
        mvc.perform(get("/equipes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nome").value("Equipe de Redes"));
    }

    @Test
    void buscaEquipePorId() throws Exception {
        Long id = equipes.findAllByOrderByIdAsc().get(0).getId();
        mvc.perform(get("/equipes/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.areaAtuacao").value("Água e esgoto"));
    }

    @Test
    void equipeInexistenteRetorna404() throws Exception {
        mvc.perform(get("/equipes/{id}", 9999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.recurso").value("Equipe"));
    }

    @Test
    void idNaoNumericoRetorna400() throws Exception {
        mvc.perform(get("/equipes/abc")).andExpect(status().isBadRequest());
    }

    @Test
    void cadastraEquipeERetorna201() throws Exception {
        postar("{\"nome\":\"  Equipe de Esgoto  \",\"areaAtuacao\":\"Esgotamento sanitário\"}")
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", matchesPattern("/equipes/\\d+")))
                .andExpect(jsonPath("$.nome").value("Equipe de Esgoto"));
        assertEquals(3, equipes.count());
    }

    @Test
    void nomeDuplicadoRetorna409() throws Exception {
        postar("{\"nome\":\"equipe de redes\",\"areaAtuacao\":\"Esgoto\"}")
                .andExpect(status().isConflict());
        assertEquals(2, equipes.count());
    }

    @Test
    void camposInvalidosRetornam400() throws Exception {
        postar("{}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.nome").value("Nome da equipe é obrigatório."))
                .andExpect(jsonPath("$.erros.areaAtuacao").value("Área de atuação é obrigatória."));
        postar("{\"nome\":\"" + "a".repeat(121) + "\",\"areaAtuacao\":\"Esgoto\"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.nome").value("Nome da equipe deve ter no máximo 120 caracteres."));
        postar("{nome: sem aspas").andExpect(status().isBadRequest());
    }
}
