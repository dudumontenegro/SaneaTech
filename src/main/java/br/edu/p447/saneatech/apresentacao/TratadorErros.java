package br.edu.p447.saneatech.apresentacao;

import br.edu.p447.saneatech.aplicacao.OcorrenciaNaoEncontradaException;
import br.edu.p447.saneatech.dominio.excecao.RegraNegocioException;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Converte exceções de aplicação e domínio em respostas HTTP (RFC 9457). */
@RestControllerAdvice
public class TratadorErros {

    @ExceptionHandler(OcorrenciaNaoEncontradaException.class)
    ProblemDetail naoEncontrada(OcorrenciaNaoEncontradaException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    /** Transição inválida ou regra de domínio violada (ex.: iniciar sem equipe). */
    @ExceptionHandler(RegraNegocioException.class)
    ProblemDetail regraNegocio(RegraNegocioException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_CONTENT, e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ProblemDetail argumentoInvalido(IllegalArgumentException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail validacao(MethodArgumentNotValidException e) {
        String detalhe = e.getBindingResult().getFieldErrors().stream()
            .map(erro -> erro.getDefaultMessage())
            .collect(Collectors.joining(" "));
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detalhe);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ProblemDetail corpoInvalido(HttpMessageNotReadableException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
            "Corpo inválido. Use {\"status\": \"EM_ANDAMENTO\" | \"CONCLUIDA\", \"observacao\": \"...\"}.");
    }
}
