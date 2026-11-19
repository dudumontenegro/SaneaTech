package br.edu.p447.saneatech.internal.cidadao.entity;

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
import java.util.Locale;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Cidadão solicitante de uma ou mais ocorrências. */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "cidadaos")
public class CidadaoEntity {

    private static final String EMAIL_REGEX = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, unique = true, length = 160)
    private String email;

    @Column(length = 25)
    private String telefone;

    @Getter(AccessLevel.NONE)
    @OneToMany(mappedBy = "solicitante")
    private List<OcorrenciaEntity> ocorrencias = new ArrayList<>();

    public CidadaoEntity(String nome, String email, String telefone) {
        this.nome = Validacao.textoObrigatorio(nome, "Nome", 120);
        this.email = emailValidado(email);
        this.telefone = Validacao.textoOpcional(telefone, "Telefone", 25);
    }

    public void corrigirNome(String novoNome) {
        this.nome = Validacao.textoObrigatorio(novoNome, "Nome", 120);
    }

    public void atualizarContato(String novoEmail, String novoTelefone) {
        String emailNovo = emailValidado(novoEmail);
        String telefoneNovo = Validacao.textoOpcional(novoTelefone, "Telefone", 25);
        this.email = emailNovo;
        this.telefone = telefoneNovo;
    }

    /** Mantém o lado inverso do relacionamento; chamado por {@link OcorrenciaEntity#abrir}. */
    public void vincularOcorrencia(OcorrenciaEntity ocorrencia) {
        if (!ocorrencias.contains(ocorrencia)) {
            ocorrencias.add(ocorrencia);
        }
    }

    public List<OcorrenciaEntity> getOcorrencias() {
        return Collections.unmodifiableList(ocorrencias);
    }

    public static String normalizarEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }

    private static String emailValidado(String email) {
        String valor = Validacao.textoObrigatorio(email, "E-mail", 160).toLowerCase(Locale.ROOT);
        if (!valor.matches(EMAIL_REGEX)) {
            throw new IllegalArgumentException("E-mail inválido.");
        }
        return valor;
    }
}
