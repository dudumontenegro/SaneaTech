package br.edu.p447.saneatech.dominio.modelo;

import br.edu.p447.saneatech.dominio.enums.StatusOcorrencia;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Objects;

/** Registro histórico imutável no domínio, criado somente pela Ocorrencia. */
@Entity
@Table(name = "historicos_status")
public class HistoricoStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private StatusOcorrencia statusAnterior;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusOcorrencia statusNovo;

    @Column(nullable = false, length = 300)
    private String observacao;

    @Column(nullable = false, updatable = false)
    private Instant registradoEm;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ocorrencia_id", nullable = false)
    private Ocorrencia ocorrencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipe_id")
    private EquipeResponsavel equipeResponsavel;

    protected HistoricoStatus() { } // JPA

    HistoricoStatus(Ocorrencia ocorrencia, StatusOcorrencia statusAnterior,
                    StatusOcorrencia statusNovo, String observacao,
                    EquipeResponsavel equipeResponsavel, Instant registradoEm) {
        this.ocorrencia = Objects.requireNonNull(ocorrencia, "Ocorrência obrigatória.");
        this.statusAnterior = statusAnterior;
        this.statusNovo = Objects.requireNonNull(statusNovo, "Status obrigatório.");
        this.observacao = Validacao.textoObrigatorio(observacao, "Observação", 300);
        this.equipeResponsavel = equipeResponsavel;
        this.registradoEm = Objects.requireNonNull(registradoEm, "Data obrigatória.");
        if (equipeResponsavel != null) equipeResponsavel.vincularAlteracao(this);
    }

    public Long getId() { return id; }
    public StatusOcorrencia getStatusAnterior() { return statusAnterior; }
    public StatusOcorrencia getStatusNovo() { return statusNovo; }
    public String getObservacao() { return observacao; }
    public Instant getRegistradoEm() { return registradoEm; }
    public Ocorrencia getOcorrencia() { return ocorrencia; }
    public EquipeResponsavel getEquipeResponsavel() { return equipeResponsavel; }
}
