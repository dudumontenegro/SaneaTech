package br.edu.p447.saneatech.internal.ocorrencia.entity;

import br.edu.p447.saneatech.internal.cidadao.entity.CidadaoEntity;
import br.edu.p447.saneatech.internal.equipe.entity.EquipeResponsavelEntity;
import br.edu.p447.saneatech.shared.exception.RegraNegocioException;
import br.edu.p447.saneatech.shared.util.Validacao;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Raiz do agregado de registro e acompanhamento de saneamento.
 * As mudanças de status só acontecem por operações do domínio, que registram o histórico.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ocorrencias")
public class OcorrenciaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false, unique = true, length = 40)
    private String protocolo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoOcorrencia tipo;

    @Column(nullable = false, length = 1000)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusOcorrencia status;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "logradouro", column = @Column(name = "endereco_logradouro", nullable = false, length = 180)),
        @AttributeOverride(name = "numero", column = @Column(name = "endereco_numero", nullable = false, length = 20)),
        @AttributeOverride(name = "bairro", column = @Column(name = "endereco_bairro", nullable = false, length = 100)),
        @AttributeOverride(name = "cidade", column = @Column(name = "endereco_cidade", nullable = false, length = 100)),
        @AttributeOverride(name = "uf", column = @Column(name = "endereco_uf", nullable = false, length = 2)),
        @AttributeOverride(name = "referencia", column = @Column(name = "endereco_referencia", length = 200))
    })
    private EnderecoOcorrencia endereco;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "solicitante_id", nullable = false)
    private CidadaoEntity solicitante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipe_id")
    private EquipeResponsavelEntity equipeResponsavel;

    @Getter(AccessLevel.NONE)
    @OneToMany(mappedBy = "ocorrencia", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("registradoEm ASC, id ASC")
    private List<HistoricoStatusEntity> historico = new ArrayList<>();

    public static OcorrenciaEntity abrir(CidadaoEntity solicitante, TipoOcorrencia tipo,
                                         String descricao, EnderecoOcorrencia endereco) {
        Objects.requireNonNull(solicitante, "Cidadão obrigatório.");
        Objects.requireNonNull(tipo, "Tipo obrigatório.");
        Objects.requireNonNull(endereco, "Endereço obrigatório.");
        String descricaoValidada = Validacao.textoObrigatorio(descricao, "Descrição", 1000);

        OcorrenciaEntity ocorrencia = new OcorrenciaEntity();
        ocorrencia.protocolo = "SAN-" + UUID.randomUUID().toString().toUpperCase();
        ocorrencia.tipo = tipo;
        ocorrencia.descricao = descricaoValidada;
        ocorrencia.endereco = endereco;
        ocorrencia.status = StatusOcorrencia.PENDENTE;
        ocorrencia.solicitante = solicitante;
        ocorrencia.criadoEm = Instant.now();
        ocorrencia.atualizadoEm = ocorrencia.criadoEm;
        solicitante.vincularOcorrencia(ocorrencia);
        ocorrencia.registrarStatus(null, StatusOcorrencia.PENDENTE, "Ocorrência registrada pelo cidadão.");
        return ocorrencia;
    }

    public void atribuirEquipe(EquipeResponsavelEntity novaEquipe) {
        exigirStatus(StatusOcorrencia.PENDENTE, "A equipe só pode ser atribuída enquanto a ocorrência está pendente.");
        Objects.requireNonNull(novaEquipe, "Equipe obrigatória.");
        if (this.equipeResponsavel == novaEquipe) {
            return;
        }
        if (this.equipeResponsavel != null) {
            this.equipeResponsavel.desvincularOcorrencia(this);
        }
        this.equipeResponsavel = novaEquipe;
        novaEquipe.vincularOcorrencia(this);
        tocar();
    }

    public void atualizarDescricao(String novaDescricao) {
        exigirStatus(StatusOcorrencia.PENDENTE, "A descrição só pode ser corrigida enquanto pendente.");
        this.descricao = Validacao.textoObrigatorio(novaDescricao, "Descrição", 1000);
        tocar();
    }

    public void corrigirLocalizacao(EnderecoOcorrencia novoEndereco) {
        exigirStatus(StatusOcorrencia.PENDENTE, "A localização só pode ser corrigida enquanto pendente.");
        this.endereco = Objects.requireNonNull(novoEndereco, "Endereço obrigatório.");
        tocar();
    }

    public void iniciarAtendimento(String observacao) {
        exigirStatus(StatusOcorrencia.PENDENTE, "Somente uma ocorrência pendente pode iniciar atendimento.");
        if (equipeResponsavel == null) {
            throw new RegraNegocioException("Atribua uma equipe antes de iniciar o atendimento.");
        }
        mudarStatus(StatusOcorrencia.EM_ANDAMENTO, observacao);
    }

    public void concluirAtendimento(String observacao) {
        exigirStatus(StatusOcorrencia.EM_ANDAMENTO, "Somente uma ocorrência em andamento pode ser concluída.");
        mudarStatus(StatusOcorrencia.CONCLUIDA, observacao);
    }

    public List<HistoricoStatusEntity> getHistorico() {
        return Collections.unmodifiableList(historico);
    }

    private void mudarStatus(StatusOcorrencia novoStatus, String observacao) {
        String nota = Validacao.textoObrigatorio(observacao, "Observação", 300);
        StatusOcorrencia anterior = this.status;
        this.status = novoStatus;
        tocar();
        registrarStatus(anterior, novoStatus, nota);
    }

    private void registrarStatus(StatusOcorrencia anterior, StatusOcorrencia novo, String nota) {
        historico.add(new HistoricoStatusEntity(this, anterior, novo, nota, equipeResponsavel, atualizadoEm));
    }

    private void exigirStatus(StatusOcorrencia esperado, String mensagem) {
        if (this.status != esperado) {
            throw new RegraNegocioException(mensagem);
        }
    }

    private void tocar() {
        this.atualizadoEm = Instant.now();
    }
}
