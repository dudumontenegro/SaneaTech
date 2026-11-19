package br.edu.p447.saneatech.apresentacao;

import br.edu.p447.saneatech.aplicacao.CidadaoNaoEncontradoException;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;

@RestControllerAdvice
public class TratadorErros {
    @ExceptionHandler(CidadaoNaoEncontradoException.class)
    public ResponseEntity<Map<String, String>> naoEncontrado(CidadaoNaoEncontradoException erro) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", erro.getMessage()));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, HttpMessageNotReadableException.class,
                       IllegalArgumentException.class})
    public ResponseEntity<Map<String, String>> requisicaoInvalida(Exception erro) {
        return ResponseEntity.badRequest().body(Map.of("erro", "Requisição inválida"));
    }
}
