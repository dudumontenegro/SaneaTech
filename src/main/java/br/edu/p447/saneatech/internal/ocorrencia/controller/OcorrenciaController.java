package br.edu.p447.saneatech.internal.ocorrencia.controller;

import br.edu.p447.saneatech.internal.ocorrencia.dto.CriarOcorrenciaRequest;
import br.edu.p447.saneatech.internal.ocorrencia.dto.CriarOcorrenciaResponse;
import br.edu.p447.saneatech.internal.ocorrencia.dto.OcorrenciaDetalheResponse;
import br.edu.p447.saneatech.internal.ocorrencia.dto.OcorrenciaResumoResponse;
import br.edu.p447.saneatech.internal.ocorrencia.entity.StatusOcorrencia;
import br.edu.p447.saneatech.internal.ocorrencia.service.OcorrenciaService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Registro (POST) e consulta (GET) de ocorrências. */
@RestController
@RequestMapping("ocorrencias")
@RequiredArgsConstructor
public class OcorrenciaController {

    private final OcorrenciaService ocorrenciaService;

    @PostMapping
    public ResponseEntity<CriarOcorrenciaResponse> registrar(@Valid @RequestBody CriarOcorrenciaRequest request) {
        CriarOcorrenciaResponse response = ocorrenciaService.registrarOcorrencia(request);
        return ResponseEntity.created(URI.create("/ocorrencias/" + response.id())).body(response);
    }

    @GetMapping
    public ResponseEntity<List<OcorrenciaResumoResponse>> listarTodas(
            @RequestParam(required = false) StatusOcorrencia status) {
        return ResponseEntity.ok(ocorrenciaService.listarTodas(status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OcorrenciaDetalheResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ocorrenciaService.buscarOcorrenciaPorId(id));
    }
}
