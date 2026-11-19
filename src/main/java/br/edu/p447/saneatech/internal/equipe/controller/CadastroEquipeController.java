package br.edu.p447.saneatech.internal.equipe.controller;

import br.edu.p447.saneatech.internal.equipe.dto.CriarEquipeRequest;
import br.edu.p447.saneatech.internal.equipe.dto.CriarEquipeResponse;
import br.edu.p447.saneatech.internal.equipe.service.CadastroEquipeService;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Feature: cadastro de equipes (POST /equipes). */
@RestController
@RequestMapping("equipes")
@RequiredArgsConstructor
public class CadastroEquipeController {

    private final CadastroEquipeService cadastroEquipeService;

    @PostMapping
    public ResponseEntity<CriarEquipeResponse> cadastrar(@Valid @RequestBody CriarEquipeRequest request) {
        CriarEquipeResponse response = cadastroEquipeService.cadastrarEquipe(request);
        return ResponseEntity.created(URI.create("/equipes/" + response.id())).body(response);
    }
}
