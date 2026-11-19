package br.edu.p447.saneatech.internal.ocorrencia;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.edu.p447.saneatech.internal.ocorrencia.entity.OcorrenciaEntity;
import br.edu.p447.saneatech.internal.ocorrencia.entity.StatusOcorrencia;
import br.edu.p447.saneatech.internal.ocorrencia.repository.OcorrenciaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

/** Usa as ocorrências inseridas pela migration V5; cada teste é revertido ao final. */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AtualizacaoStatusControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    OcorrenciaRepository ocorrencias;

    private Long id(String protocolo) {
        return ocorrencias.findByProtocolo(protocolo).orElseThrow().getId();
    }

    private ResultActions atualizar(Object id, String json) throws Exception {
        return mvc.perform(patch("/ocorrencias/{id}/status", id)
                .contentType(MediaType.APPLICATION_JSON).content(json));
    }

    private String corpo(String status, String observacao) {
        return "{\"status\":\"" + status + "\",\"observacao\":\"" + observacao + "\"}";
    }

    @Test
    void pendenteParaEmAndamentoRegistraHistorico() throws Exception {
        Long id = id("SAN-SEED-0001");
        atualizar(id, corpo("EM_ANDAMENTO", "Equipe a caminho."))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusAnterior").value("PENDENTE"))
                .andExpect(jsonPath("$.status").value("EM_ANDAMENTO"))
                .andExpect(jsonPath("$.historico.length()").value(2))
                .andExpect(jsonPath("$.historico[1].statusAnterior").value("PENDENTE"))
                .andExpect(jsonPath("$.historico[1].statusNovo").value("EM_ANDAMENTO"))
                .andExpect(jsonPath("$.historico[1].observacao").value("Equipe a caminho."))
                .andExpect(jsonPath("$.historico[1].equipe").value("Equipe de Redes"));

        OcorrenciaEntity salva = ocorrencias.findById(id).orElseThrow();
        assertEquals(StatusOcorrencia.EM_ANDAMENTO, salva.getStatus());
        assertEquals(2, salva.getHistorico().size());
    }

    @Test
    void emAndamentoParaConcluida() throws Exception {
        atualizar(id("SAN-SEED-0002"), corpo("CONCLUIDA", "Rede desobstruída."))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONCLUIDA"))
                .andExpect(jsonPath("$.historico.length()").value(3));
    }

    @Test
    void pularEtapaRetorna422() throws Exception {
        atualizar(id("SAN-SEED-0001"), corpo("CONCLUIDA", "Tentando pular etapa."))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.detail").value(containsString("PENDENTE → CONCLUIDA")));
    }

    @Test
    void mesmoStatusRetorna422() throws Exception {
        atualizar(id("SAN-SEED-0002"), corpo("EM_ANDAMENTO", "De novo."))
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    void concluidaNaoPodeRetroceder() throws Exception {
        atualizar(id("SAN-SEED-0003"), corpo("EM_ANDAMENTO", "Reabrir."))
                .andExpect(status().isUnprocessableContent());
        atualizar(id("SAN-SEED-0002"), corpo("PENDENTE", "Voltar."))
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    void iniciarSemEquipeRetorna422() throws Exception {
        atualizar(id("SAN-SEED-0004"), corpo("EM_ANDAMENTO", "Iniciar."))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.detail").value("Atribua uma equipe antes de iniciar o atendimento."));
    }

    @Test
    void ocorrenciaInexistenteRetorna404() throws Exception {
        atualizar(9999, corpo("EM_ANDAMENTO", "Teste.")).andExpect(status().isNotFound());
    }

    @Test
    void corpoInvalidoRetorna400() throws Exception {
        Long id = id("SAN-SEED-0001");
        atualizar(id, corpo("EM_ANDAMENTO", " "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.observacao").value("Observação obrigatória."));
        atualizar(id, corpo("CANCELADA", "Status inexistente.")).andExpect(status().isBadRequest());
        atualizar(id, "{\"observacao\":\"Sem status.\"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.status").value("Status obrigatório."));
    }
}
