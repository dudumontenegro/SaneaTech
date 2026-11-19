package br.edu.p447.saneatech;

import br.edu.p447.saneatech.api.CidadaoController;
import br.edu.p447.saneatech.aplicacao.CidadaoService;
import br.edu.p447.saneatech.infra.CidadaoRepository;
import com.sun.net.httpserver.HttpServer;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.io.IOException;
import java.net.InetSocketAddress;

public class AplicacaoApi {
    public static void main(String[] args) throws IOException {
        EntityManagerFactory fabrica =
                Persistence.createEntityManagerFactory("saneatech-demo");

        HttpServer servidor = HttpServer.create(
                new InetSocketAddress("localhost", 8080),
                0
        );

        CidadaoRepository repositorio = new CidadaoRepository(fabrica);
        CidadaoService servico = new CidadaoService(repositorio);
        CidadaoController controller = new CidadaoController(servico);

        servidor.createContext("/cidadaos", controller);
        servidor.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            servidor.stop(0);
            fabrica.close();
        }));

        System.out.println("API disponível em http://localhost:8080/cidadaos");
    }
}