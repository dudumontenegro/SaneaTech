package br.edu.p447.saneatech.dominio.modelo;

/** Validações compartilhadas sem dependências de infraestrutura. */
final class Validacao {
    private Validacao() { }

    static String textoObrigatorio(String valor, String campo, int maximo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(campo + " é obrigatório.");
        }
        String texto = valor.trim();
        if (texto.length() > maximo) {
            throw new IllegalArgumentException(campo + " deve ter no máximo " + maximo + " caracteres.");
        }
        return texto;
    }

    static String textoOpcional(String valor, String campo, int maximo) {
        if (valor == null || valor.isBlank()) return null;
        return textoObrigatorio(valor, campo, maximo);
    }
}
