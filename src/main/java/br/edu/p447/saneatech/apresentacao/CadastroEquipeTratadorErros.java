package br.edu.p447.saneatech.apresentacao;

import br.edu.p447.saneatech.aplicacao.EquipeJaCadastradaException;
import java.util.stream.Collectors;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Erros do cadastro de equipes em formato ProblemDetail (RFC 9457).
 * Restrito ao {@link CadastroEquipeController} para não interferir nos demais endpoints.
 */
@RestControllerAdvice(assignableTypes = CadastroEquipeController.class)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CadastroEquipeTratadorErros {

    @ExceptionHandler(EquipeJaCadastradaException.class)
    ProblemDetail duplicada(EquipeJaCadastradaException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
    }

    /** Validação do domínio (EquipeResponsavel / Validacao). */
    @ExceptionHandler(IllegalArgumentException.class)
    ProblemDetail argumentoInvalido(IllegalArgumentException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail validacao(MethodArgumentNotValidException e) {
        String detalhe = e.getBindingResult().getFieldErrors().stream()
            .map(erro -> erro.getDefaultMessage())
            .sorted()
            .collect(Collectors.joining(" "));
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detalhe);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ProblemDetail corpoInvalido(HttpMessageNotReadableException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
            "Corpo inválido. Use {\"nome\": \"...\", \"areaAtuacao\": \"...\"}.");
    }
}
