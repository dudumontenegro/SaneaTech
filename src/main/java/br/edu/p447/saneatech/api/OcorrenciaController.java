package br.edu.p447.saneatech.api;

import br.edu.p447.saneatech.api.dto.OcorrenciaResposta;
import br.edu.p447.saneatech.aplicacao.OcorrenciaService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ocorrencias")
public class OcorrenciaController {
    private final OcorrenciaService ocorrenciaService;

    public OcorrenciaController(OcorrenciaService ocorrenciaService) {
        this.ocorrenciaService = ocorrenciaService;
    }

    @GetMapping
    public List<OcorrenciaResposta> listar() {
        return ocorrenciaService.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OcorrenciaResposta> buscarPorId(@PathVariable Long id) {
        return ocorrenciaService.buscarPorId(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
}