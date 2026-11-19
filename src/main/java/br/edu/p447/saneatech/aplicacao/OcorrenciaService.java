package br.edu.p447.saneatech.aplicacao;

import br.edu.p447.saneatech.dominio.enums.TipoOcorrencia;
import br.edu.p447.saneatech.dominio.modelo.Cidadao;
import br.edu.p447.saneatech.dominio.modelo.EnderecoOcorrencia;
import br.edu.p447.saneatech.dominio.modelo.Ocorrencia;
import br.edu.p447.saneatech.infraestrutura.CidadaoRepository;
import br.edu.p447.saneatech.infraestrutura.OcorrenciaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OcorrenciaService {
    private final OcorrenciaRepository ocorrencias;
    private final CidadaoRepository cidadaos;

    public OcorrenciaService(OcorrenciaRepository ocorrencias, CidadaoRepository cidadaos) {
        this.ocorrencias = ocorrencias;
        this.cidadaos = cidadaos;
    }

    @Transactional
    public OcorrenciaRegistrada registrar(Long cidadaoId, TipoOcorrencia tipo,
                                          String descricao, EnderecoOcorrencia endereco) {
        Cidadao cidadao = cidadaos.findById(cidadaoId)
            .orElseThrow(() -> new CidadaoNaoEncontradoException(cidadaoId));
        Ocorrencia ocorrencia = Ocorrencia.abrir(cidadao, tipo, descricao, endereco);
        return OcorrenciaRegistrada.de(ocorrencias.save(ocorrencia));
    }
}
