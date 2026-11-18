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

/** Cidadão solicitante de uma ou mais ocorrências. */
@Entity
@Table(name = "cidadaos")
public class Cidadao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, unique = true, length = 160)
    private String email;

    @Column(length = 25)
    private String telefone;

    @OneToMany(mappedBy = "solicitante")
    private List<Ocorrencia> ocorrencias = new ArrayList<>();

    protected Cidadao() { } // JPA

    public Cidadao(String nome, String email, String telefone) {
        this.nome = Validacao.textoObrigatorio(nome, "Nome", 120);
        this.email = emailValidado(email);
        this.telefone = Validacao.textoOpcional(telefone, "Telefone", 25);
    }

    public void corrigirNome(String novoNome) {
        this.nome = Validacao.textoObrigatorio(novoNome, "Nome", 120);
    }

    public void atualizarContato(String novoEmail, String novoTelefone) {
        String emailValidado = emailValidado(novoEmail);
        String telefoneValidado = Validacao.textoOpcional(novoTelefone, "Telefone", 25);
        this.email = emailValidado;
        this.telefone = telefoneValidado;
    }

    private static String emailValidado(String email) {
        String valor = Validacao.textoObrigatorio(email, "E-mail", 160).toLowerCase(java.util.Locale.ROOT);
        if (!valor.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalArgumentException("E-mail inválido.");
        }
        return valor;
    }

    void vincularOcorrencia(Ocorrencia ocorrencia) {
        if (!ocorrencias.contains(ocorrencia)) ocorrencias.add(ocorrencia);
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getTelefone() { return telefone; }
    public List<Ocorrencia> getOcorrencias() { return Collections.unmodifiableList(ocorrencias); }
}
