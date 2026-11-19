package br.edu.p447.saneatech.aplicacao;

import br.edu.p447.saneatech.api.dto.OcorrenciaResposta;
import br.edu.p447.saneatech.infra.repository.OcorrenciaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class OcorrenciaService {
    private final OcorrenciaRepository ocorrenciaRepository;

    public OcorrenciaService(OcorrenciaRepository ocorrenciaRepository) {
        this.ocorrenciaRepository = ocorrenciaRepository;
    }

    public List<OcorrenciaResposta> listar() {
        return ocorrenciaRepository.findAll().stream().map(OcorrenciaResposta::de).toList();
    }

    public Optional<OcorrenciaResposta> buscarPorId(Long id) {
        return ocorrenciaRepository.findById(id).map(OcorrenciaResposta::de);
    }
}