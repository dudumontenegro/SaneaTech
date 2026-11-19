package br.edu.p447.saneatech.internal.ocorrencia.controller;

import br.edu.p447.saneatech.internal.ocorrencia.dto.AtualizacaoStatusResponse;
import br.edu.p447.saneatech.internal.ocorrencia.dto.AtualizarStatusRequest;
import br.edu.p447.saneatech.internal.ocorrencia.service.AtualizacaoStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Feature: atualização de status (PATCH /ocorrencias/{id}/status). */
@RestController
@RequestMapping("ocorrencias")
@RequiredArgsConstructor
public class AtualizacaoStatusController {

    private final AtualizacaoStatusService atualizacaoStatusService;

    @PatchMapping("/{id}/status")
    public ResponseEntity<AtualizacaoStatusResponse> atualizarStatus(@PathVariable Long id,
                                                                     @Valid @RequestBody AtualizarStatusRequest request) {
        return ResponseEntity.ok(atualizacaoStatusService.atualizarStatus(id, request));
    }
}
