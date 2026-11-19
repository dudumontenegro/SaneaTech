package br.edu.p447.saneatech.apresentacao;

import br.edu.p447.saneatech.aplicacao.OcorrenciaRegistrada;
import br.edu.p447.saneatech.aplicacao.OcorrenciaService;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ocorrencias")
public class OcorrenciaController {
    private final OcorrenciaService service;

    public OcorrenciaController(OcorrenciaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<OcorrenciaRegistrada> registrar(@Valid @RequestBody CadastroOcorrenciaRequest request) {
        OcorrenciaRegistrada criada = service.registrar(
            request.cidadaoId(), request.tipo(), request.descricao(), request.enderecoDominio());
        return ResponseEntity.created(URI.create("/ocorrencias/" + criada.id())).body(criada);
    }
}
