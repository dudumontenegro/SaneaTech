package br.edu.p447.saneatech.dominio.modelo;

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

/** Unidade ou equipe que trata as solicitações registradas. */
@Entity
@Table(name = "equipes_responsaveis")
public class EquipeResponsavel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, length = 120)
    private String areaAtuacao;

    @OneToMany(mappedBy = "equipeResponsavel")
    private List<Ocorrencia> ocorrencias = new ArrayList<>();

    @OneToMany(mappedBy = "equipeResponsavel")
    private List<HistoricoStatus> alteracoesRegistradas = new ArrayList<>();

    protected EquipeResponsavel() { } // JPA

    public EquipeResponsavel(String nome, String areaAtuacao) {
        this.nome = Validacao.textoObrigatorio(nome, "Nome da equipe", 120);
        this.areaAtuacao = Validacao.textoObrigatorio(areaAtuacao, "Área de atuação", 120);
    }

    public void renomear(String novoNome) {
        this.nome = Validacao.textoObrigatorio(novoNome, "Nome da equipe", 120);
    }

    public void alterarAreaAtuacao(String novaArea) {
        this.areaAtuacao = Validacao.textoObrigatorio(novaArea, "Área de atuação", 120);
    }

    void vincularOcorrencia(Ocorrencia ocorrencia) {
        if (!ocorrencias.contains(ocorrencia)) ocorrencias.add(ocorrencia);
    }

    void desvincularOcorrencia(Ocorrencia ocorrencia) {
        ocorrencias.remove(ocorrencia);
    }

    void vincularAlteracao(HistoricoStatus alteracao) {
        if (!alteracoesRegistradas.contains(alteracao)) alteracoesRegistradas.add(alteracao);
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getAreaAtuacao() { return areaAtuacao; }
    public List<Ocorrencia> getOcorrencias() { return Collections.unmodifiableList(ocorrencias); }
    public List<HistoricoStatus> getAlteracoesRegistradas() {
        return Collections.unmodifiableList(alteracoesRegistradas);
    }
}
