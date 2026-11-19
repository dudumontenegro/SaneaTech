package br.edu.p447.saneatech.apresentacao;

import br.edu.p447.saneatech.dominio.modelo.EquipeResponsavel;
import br.edu.p447.saneatech.infraestrutura.EquipeResponsavelRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Cada teste é revertido ao final, mantendo o banco em memória limpo. */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CadastroEquipeControllerTest {
    @Autowired MockMvc mvc;
    @Autowired EquipeResponsavelRepository equipes;

    private ResultActions postar(String json) throws Exception {
        return mvc.perform(post("/equipes").contentType(MediaType.APPLICATION_JSON).content(json));
    }

    @Test void cadastraEquipeERetorna201() throws Exception {
        postar("{\"nome\":\"  Equipe de Redes  \",\"areaAtuacao\":\"Manutenção de água\"}")
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", matchesPattern("/equipes/\\d+")))
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.nome").value("Equipe de Redes"))
            .andExpect(jsonPath("$.areaAtuacao").value("Manutenção de água"));

        assertEquals(1, equipes.count());
        EquipeResponsavel salva = equipes.findAll().get(0);
        assertEquals("Equipe de Redes", salva.getNome());
    }

    @Test void nomeDuplicadoRetorna409() throws Exception {
        equipes.save(new EquipeResponsavel("Equipe de Redes", "Manutenção"));
        postar("{\"nome\":\"equipe de redes\",\"areaAtuacao\":\"Esgoto\"}")
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.detail").value(containsString("equipe de redes")));
        assertEquals(1, equipes.count());
    }

    @Test void camposObrigatoriosRetornam400() throws Exception {
        postar("{\"nome\":\" \",\"areaAtuacao\":\"Esgoto\"}")
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.detail").value("Nome da equipe é obrigatório."));
        postar("{\"nome\":\"Equipe Esgoto\"}")
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.detail").value("Área de atuação é obrigatória."));
        postar("{}")
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.detail").value(containsString("Nome da equipe é obrigatório.")))
            .andExpect(jsonPath("$.detail").value(containsString("Área de atuação é obrigatória.")));
        assertEquals(0, equipes.count());
    }

    @Test void camposLongosDemaisRetornam400() throws Exception {
        String longo = "a".repeat(121);
        postar("{\"nome\":\"" + longo + "\",\"areaAtuacao\":\"Esgoto\"}")
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.detail").value("Nome da equipe deve ter no máximo 120 caracteres."));
        postar("{\"nome\":\"Equipe\",\"areaAtuacao\":\"" + longo + "\"}")
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.detail").value("Área de atuação deve ter no máximo 120 caracteres."));
        assertEquals(0, equipes.count());
    }

    @Test void corpoMalformadoRetorna400() throws Exception {
        postar("{nome: sem aspas")
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.detail").value(containsString("Corpo inválido")));
    }
}
