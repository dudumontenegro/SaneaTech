package br.edu.p447.saneatech.apresentacao;

import br.edu.p447.saneatech.dominio.enums.StatusOcorrencia;
import br.edu.p447.saneatech.dominio.modelo.Ocorrencia;
import br.edu.p447.saneatech.infraestrutura.OcorrenciaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Usa as ocorrências pré-carregadas por data.sql; cada teste é revertido ao final. */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AtualizacaoStatusControllerTest {
    @Autowired MockMvc mvc;
    @Autowired OcorrenciaRepository ocorrencias;

    private Long id(String protocolo) {
        return ocorrencias.findByProtocolo(protocolo).orElseThrow().getId();
    }

    private String corpo(String status, String observacao) {
        return "{\"status\":\"" + status + "\",\"observacao\":\"" + observacao + "\"}";
    }

    @Test void pendenteParaEmAndamentoRegistraHistorico() throws Exception {
        Long id = id("SAN-SEED-0001");
        mvc.perform(patch("/ocorrencias/{id}/status", id).contentType(MediaType.APPLICATION_JSON)
                .content(corpo("EM_ANDAMENTO", "Equipe a caminho.")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.statusAnterior").value("PENDENTE"))
            .andExpect(jsonPath("$.status").value("EM_ANDAMENTO"))
            .andExpect(jsonPath("$.historico.length()").value(2))
            .andExpect(jsonPath("$.historico[1].statusAnterior").value("PENDENTE"))
            .andExpect(jsonPath("$.historico[1].statusNovo").value("EM_ANDAMENTO"))
            .andExpect(jsonPath("$.historico[1].observacao").value("Equipe a caminho."))
            .andExpect(jsonPath("$.historico[1].equipe").value("Equipe de Redes"));

        Ocorrencia salva = ocorrencias.findById(id).orElseThrow();
        assertEquals(StatusOcorrencia.EM_ANDAMENTO, salva.getStatus());
        assertEquals(2, salva.getHistorico().size());
    }

    @Test void emAndamentoParaConcluida() throws Exception {
        mvc.perform(patch("/ocorrencias/{id}/status", id("SAN-SEED-0002")).contentType(MediaType.APPLICATION_JSON)
                .content(corpo("CONCLUIDA", "Rede desobstruída.")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("CONCLUIDA"))
            .andExpect(jsonPath("$.historico.length()").value(3));
    }

    @Test void pularEtapaRetorna422() throws Exception {
        mvc.perform(patch("/ocorrencias/{id}/status", id("SAN-SEED-0001")).contentType(MediaType.APPLICATION_JSON)
                .content(corpo("CONCLUIDA", "Tentando pular etapa.")))
            .andExpect(status().isUnprocessableContent())
            .andExpect(jsonPath("$.detail").value(org.hamcrest.Matchers.containsString("PENDENTE → CONCLUIDA")));
    }

    @Test void concluidaNaoPodeRetroceder() throws Exception {
        mvc.perform(patch("/ocorrencias/{id}/status", id("SAN-SEED-0003")).contentType(MediaType.APPLICATION_JSON)
                .content(corpo("EM_ANDAMENTO", "Reabrir.")))
            .andExpect(status().isUnprocessableContent());
    }

    @Test void iniciarSemEquipeRetorna422() throws Exception {
        mvc.perform(patch("/ocorrencias/{id}/status", id("SAN-SEED-0004")).contentType(MediaType.APPLICATION_JSON)
                .content(corpo("EM_ANDAMENTO", "Iniciar.")))
            .andExpect(status().isUnprocessableContent())
            .andExpect(jsonPath("$.detail").value("Atribua uma equipe antes de iniciar o atendimento."));
    }

    @Test void ocorrenciaInexistenteRetorna404() throws Exception {
        mvc.perform(patch("/ocorrencias/{id}/status", 9999).contentType(MediaType.APPLICATION_JSON)
                .content(corpo("EM_ANDAMENTO", "Teste.")))
            .andExpect(status().isNotFound());
    }

    @Test void corpoInvalidoRetorna400() throws Exception {
        Long id = id("SAN-SEED-0001");
        mvc.perform(patch("/ocorrencias/{id}/status", id).contentType(MediaType.APPLICATION_JSON)
                .content(corpo("EM_ANDAMENTO", " ")))
            .andExpect(status().isBadRequest());
        mvc.perform(patch("/ocorrencias/{id}/status", id).contentType(MediaType.APPLICATION_JSON)
                .content(corpo("CANCELADA", "Status inexistente.")))
            .andExpect(status().isBadRequest());
        mvc.perform(patch("/ocorrencias/{id}/status", id).contentType(MediaType.APPLICATION_JSON)
                .content("{\"observacao\":\"Sem status.\"}"))
            .andExpect(status().isBadRequest());
    }
}
