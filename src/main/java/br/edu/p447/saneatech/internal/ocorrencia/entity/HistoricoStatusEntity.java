package br.edu.p447.saneatech.internal.ocorrencia.entity;

import br.edu.p447.saneatech.internal.equipe.entity.EquipeResponsavelEntity;
import br.edu.p447.saneatech.shared.util.Validacao;
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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Registro imutável de uma mudança de status, criado somente pela {@link OcorrenciaEntity}. */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "historicos_status")
public class HistoricoStatusEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_anterior", length = 20)
    private StatusOcorrencia statusAnterior;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_novo", nullable = false, length = 20)
    private StatusOcorrencia statusNovo;

    @Column(nullable = false, length = 300)
    private String observacao;

    @Column(name = "registrado_em", nullable = false, updatable = false)
    private Instant registradoEm;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ocorrencia_id", nullable = false)
    private OcorrenciaEntity ocorrencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipe_id")
    private EquipeResponsavelEntity equipeResponsavel;

    HistoricoStatusEntity(OcorrenciaEntity ocorrencia, StatusOcorrencia statusAnterior,
                          StatusOcorrencia statusNovo, String observacao,
                          EquipeResponsavelEntity equipeResponsavel, Instant registradoEm) {
        this.ocorrencia = Objects.requireNonNull(ocorrencia, "Ocorrência obrigatória.");
        this.statusAnterior = statusAnterior;
        this.statusNovo = Objects.requireNonNull(statusNovo, "Status obrigatório.");
        this.observacao = Validacao.textoObrigatorio(observacao, "Observação", 300);
        this.equipeResponsavel = equipeResponsavel;
        this.registradoEm = Objects.requireNonNull(registradoEm, "Data obrigatória.");
        if (equipeResponsavel != null) {
            equipeResponsavel.vincularAlteracao(this);
        }
    }
}
