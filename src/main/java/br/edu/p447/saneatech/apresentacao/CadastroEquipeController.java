package br.edu.p447.saneatech.apresentacao;

import br.edu.p447.saneatech.aplicacao.CadastroEquipeService;
import br.edu.p447.saneatech.aplicacao.EquipeCadastrada;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Endpoint de cadastro de equipes responsáveis. */
@RestController
@RequestMapping("/equipes")
public class CadastroEquipeController {
    private final CadastroEquipeService service;

    public CadastroEquipeController(CadastroEquipeService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<EquipeCadastrada> cadastrar(@Valid @RequestBody CadastroEquipeRequest request) {
        EquipeCadastrada equipe = service.cadastrar(request.nome(), request.areaAtuacao());
        return ResponseEntity.created(URI.create("/equipes/" + equipe.id())).body(equipe);
    }
}
