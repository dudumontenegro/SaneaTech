package br.edu.p447.saneatech.internal.equipe.controller;

import br.edu.p447.saneatech.internal.equipe.dto.EquipeResumoResponse;
import br.edu.p447.saneatech.internal.equipe.service.EquipeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Feature: consulta de equipes (GET /equipes e GET /equipes/{id}). */
@RestController
@RequestMapping("equipes")
@RequiredArgsConstructor
public class EquipeController {

    private final EquipeService equipeService;

    @GetMapping
    public ResponseEntity<List<EquipeResumoResponse>> listarTodas() {
        return ResponseEntity.ok(equipeService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipeResumoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(equipeService.buscarEquipePorId(id));
    }
}
