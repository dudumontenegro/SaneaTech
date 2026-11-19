package br.edu.p447.saneatech.apresentacao;

import br.edu.p447.saneatech.aplicacao.AtualizacaoStatusResultado;
import br.edu.p447.saneatech.aplicacao.AtualizacaoStatusService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Endpoint de atualização do andamento de uma ocorrência existente. */
@RestController
@RequestMapping("/ocorrencias")
public class AtualizacaoStatusController {
    private final AtualizacaoStatusService service;

    public AtualizacaoStatusController(AtualizacaoStatusService service) {
        this.service = service;
    }

    @PatchMapping("/{id}/status")
    public AtualizacaoStatusResultado atualizarStatus(@PathVariable Long id,
                                                      @Valid @RequestBody AtualizacaoStatusRequest request) {
        return service.atualizar(id, request.status(), request.observacao());
    }
}
