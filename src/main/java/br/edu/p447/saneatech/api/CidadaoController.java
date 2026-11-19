package br.edu.p447.saneatech.api;

import br.edu.p447.saneatech.aplicacao.CidadaoService;
import br.edu.p447.saneatech.dominio.modelo.Cidadao;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;

public class CidadaoController implements HttpHandler {
    private static final String CAMINHO = "/cidadaos";

    private final CidadaoService servico;
    private final ObjectMapper json = new ObjectMapper();

    public CidadaoController(CidadaoService servico) {
        this.servico = Objects.requireNonNull(servico);
    }

    @Override
    public void handle(HttpExchange requisicao) throws IOException {
        try {
            String caminho = requisicao.getRequestURI().getPath();

            if (!CAMINHO.equals(caminho)) {
                responder(requisicao, 404, new Mensagem("Recurso não encontrado."));
                return;
            }

            String metodo = requisicao.getRequestMethod();

            if ("GET".equalsIgnoreCase(metodo)) {
                List<CidadaoResposta> cidadaos = servico.listar()
                        .stream()
                        .map(CidadaoResposta::de)
                        .toList();

                responder(requisicao, 200, cidadaos);
                return;
            }

            if ("POST".equalsIgnoreCase(metodo)) {
                CidadaoEntrada entrada = json.readValue(
                        requisicao.getRequestBody(),
                        CidadaoEntrada.class
                );

                Cidadao criado = servico.cadastrar(
                        entrada.nome(),
                        entrada.email(),
                        entrada.telefone()
                );

                responder(requisicao, 201, CidadaoResposta.de(criado));
                return;
            }

            requisicao.getResponseHeaders().set("Allow", "GET, POST");
            responder(requisicao, 405, new Mensagem("Método não permitido."));
        } catch (JsonProcessingException | IllegalArgumentException erro) {
            responder(requisicao, 400, new Mensagem(erro.getMessage()));
        } catch (RuntimeException erro) {
            responder(requisicao, 500, new Mensagem("Erro interno ao processar a solicitação."));
        } finally {
            requisicao.close();
        }
    }

    private void responder(HttpExchange requisicao, int status, Object corpo)
            throws IOException {
        byte[] bytes = json.writeValueAsBytes(corpo);
        requisicao.getResponseHeaders().set(
                "Content-Type",
                "application/json; charset=utf-8"
        );
        requisicao.sendResponseHeaders(status, bytes.length);

        try (OutputStream saida = requisicao.getResponseBody()) {
            saida.write(bytes);
        }
    }

    private record CidadaoEntrada(String nome, String email, String telefone) { }

    private record CidadaoResposta(Long id, String nome, String email, String telefone) {
        private static CidadaoResposta de(Cidadao cidadao) {
            return new CidadaoResposta(
                    cidadao.getId(),
                    cidadao.getNome(),
                    cidadao.getEmail(),
                    cidadao.getTelefone()
            );
        }
    }

    private record Mensagem(String mensagem) { }
}