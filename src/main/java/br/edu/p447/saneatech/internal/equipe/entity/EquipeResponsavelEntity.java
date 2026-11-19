package br.edu.p447.saneatech.internal.equipe.entity;

import br.edu.p447.saneatech.internal.ocorrencia.entity.HistoricoStatusEntity;
import br.edu.p447.saneatech.internal.ocorrencia.entity.OcorrenciaEntity;
import br.edu.p447.saneatech.shared.util.Validacao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Unidade ou equipe que trata as ocorrências registradas. */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "equipes_responsaveis")
public class EquipeResponsavelEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 120)
    private String nome;

    @Column(name = "area_atuacao", nullable = false, length = 120)
    private String areaAtuacao;

    @Getter(AccessLevel.NONE)
    @OneToMany(mappedBy = "equipeResponsavel")
    private List<OcorrenciaEntity> ocorrencias = new ArrayList<>();

    @Getter(AccessLevel.NONE)
    @OneToMany(mappedBy = "equipeResponsavel")
    private List<HistoricoStatusEntity> alteracoesRegistradas = new ArrayList<>();

    public EquipeResponsavelEntity(String nome, String areaAtuacao) {
        this.nome = Validacao.textoObrigatorio(nome, "Nome da equipe", 120);
        this.areaAtuacao = Validacao.textoObrigatorio(areaAtuacao, "Área de atuação", 120);
    }

    public void renomear(String novoNome) {
        this.nome = Validacao.textoObrigatorio(novoNome, "Nome da equipe", 120);
    }

    public void alterarAreaAtuacao(String novaArea) {
        this.areaAtuacao = Validacao.textoObrigatorio(novaArea, "Área de atuação", 120);
    }

    public void vincularOcorrencia(OcorrenciaEntity ocorrencia) {
        if (!ocorrencias.contains(ocorrencia)) {
            ocorrencias.add(ocorrencia);
        }
    }

    public void desvincularOcorrencia(OcorrenciaEntity ocorrencia) {
        ocorrencias.remove(ocorrencia);
    }

    public void vincularAlteracao(HistoricoStatusEntity alteracao) {
        if (!alteracoesRegistradas.contains(alteracao)) {
            alteracoesRegistradas.add(alteracao);
        }
    }

    public List<OcorrenciaEntity> getOcorrencias() {
        return Collections.unmodifiableList(ocorrencias);
    }

    public List<HistoricoStatusEntity> getAlteracoesRegistradas() {
        return Collections.unmodifiableList(alteracoesRegistradas);
    }
}
