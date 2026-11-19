package br.edu.p447.saneatech.aplicacao;

import br.edu.p447.saneatech.dominio.modelo.EquipeResponsavel;
import br.edu.p447.saneatech.infraestrutura.EquipeResponsavelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso de cadastro de equipe responsável.
 * O construtor de {@link EquipeResponsavel} valida obrigatoriedade e tamanho dos campos;
 * aqui é verificada a unicidade do nome entre equipes já cadastradas.
 */
@Service
public class CadastroEquipeService {
    private final EquipeResponsavelRepository equipes;

    public CadastroEquipeService(EquipeResponsavelRepository equipes) {
        this.equipes = equipes;
    }

    @Transactional
    public EquipeCadastrada cadastrar(String nome, String areaAtuacao) {
        EquipeResponsavel equipe = new EquipeResponsavel(nome, areaAtuacao);
        if (equipes.existsByNomeIgnoreCase(equipe.getNome())) {
            throw new EquipeJaCadastradaException(equipe.getNome());
        }
        return EquipeCadastrada.de(equipes.save(equipe));
    }
}
