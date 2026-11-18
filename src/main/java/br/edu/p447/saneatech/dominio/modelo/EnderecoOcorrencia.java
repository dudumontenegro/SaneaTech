package br.edu.p447.saneatech.dominio.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Locale;

/** Objeto de valor embutido na ocorrência: não possui identidade própria. */
@Embeddable
public class EnderecoOcorrencia {
    @Column(nullable = false, length = 180)
    private String logradouro;

    @Column(nullable = false, length = 20)
    private String numero;

    @Column(nullable = false, length = 100)
    private String bairro;

    @Column(nullable = false, length = 100)
    private String cidade;

    @Column(nullable = false, length = 2)
    private String uf;

    @Column(length = 200)
    private String referencia;

    protected EnderecoOcorrencia() { } // JPA

    public EnderecoOcorrencia(String logradouro, String numero, String bairro,
                              String cidade, String uf, String referencia) {
        this.logradouro = Validacao.textoObrigatorio(logradouro, "Logradouro", 180);
        this.numero = Validacao.textoOpcional(numero, "Número", 20);
        if (this.numero == null) this.numero = "S/N";
        this.bairro = Validacao.textoObrigatorio(bairro, "Bairro", 100);
        this.cidade = Validacao.textoObrigatorio(cidade, "Cidade", 100);
        String estado = Validacao.textoObrigatorio(uf, "UF", 2).toUpperCase(Locale.ROOT);
        if (!estado.matches("[A-Z]{2}")) throw new IllegalArgumentException("UF deve conter duas letras.");
        this.uf = estado;
        this.referencia = Validacao.textoOpcional(referencia, "Referência", 200);
    }

    public String getLogradouro() { return logradouro; }
    public String getNumero() { return numero; }
    public String getBairro() { return bairro; }
    public String getCidade() { return cidade; }
    public String getUf() { return uf; }
    public String getReferencia() { return referencia; }
}
