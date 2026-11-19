package br.edu.p447.saneatech.internal.cidadao.controller;

import br.edu.p447.saneatech.internal.cidadao.dto.CidadaoResumoResponse;
import br.edu.p447.saneatech.internal.cidadao.dto.CriarCidadaoRequest;
import br.edu.p447.saneatech.internal.cidadao.dto.CriarCidadaoResponse;
import br.edu.p447.saneatech.internal.cidadao.service.CidadaoService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Feature: cadastro de cidadãos (POST e GET /cidadaos). */
@RestController
@RequestMapping("cidadaos")
@RequiredArgsConstructor
public class CidadaoController {

    private final CidadaoService cidadaoService;

    @PostMapping
    public ResponseEntity<CriarCidadaoResponse> cadastrar(@Valid @RequestBody CriarCidadaoRequest request) {
        CriarCidadaoResponse response = cidadaoService.cadastrarCidadao(request);
        return ResponseEntity.created(URI.create("/cidadaos/" + response.id())).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CidadaoResumoResponse>> listarTodos() {
        return ResponseEntity.ok(cidadaoService.listarTodos());
    }
}
