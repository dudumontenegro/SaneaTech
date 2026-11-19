package br.edu.p447.saneatech.aplicacao;

import br.edu.p447.saneatech.dominio.modelo.Cidadao;
import br.edu.p447.saneatech.infra.CidadaoRepository;

import java.util.List;
import java.util.Objects;

public class CidadaoService {
    private final CidadaoRepository repositorio;

    public CidadaoService(CidadaoRepository repositorio) {
        this.repositorio = Objects.requireNonNull(repositorio);
    }

    public Cidadao cadastrar(String nome, String email, String telefone) {
        Cidadao cidadao = new Cidadao(nome, email, telefone);
        return repositorio.salvar(cidadao);
    }

    public List<Cidadao> listar() {
        return repositorio.listar();
    }
}