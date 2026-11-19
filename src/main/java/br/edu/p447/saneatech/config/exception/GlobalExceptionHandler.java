package br.edu.p447.saneatech.config.exception;

import br.edu.p447.saneatech.shared.exception.RecursoDuplicadoException;
import br.edu.p447.saneatech.shared.exception.RecursoNaoEncontradoException;
import br.edu.p447.saneatech.shared.exception.RegraNegocioException;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/** Converte as exceções da aplicação em respostas ProblemDetail (RFC 9457). */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ProblemDetail tratarRecursoNaoEncontrado(RecursoNaoEncontradoException exception,
                                                    HttpServletRequest request) {
        ProblemDetail problema = criar(HttpStatus.NOT_FOUND, "Recurso não encontrado", exception.getMessage(), request);
        problema.setProperty("recurso", exception.getRecurso());
        problema.setProperty("identificador", exception.getIdentificador());
        return problema;
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ProblemDetail tratarRegraNegocio(RegraNegocioException exception, HttpServletRequest request) {
        return criar(HttpStatus.UNPROCESSABLE_CONTENT, "Regra de negócio violada", exception.getMessage(), request);
    }

    @ExceptionHandler(RecursoDuplicadoException.class)
    public ProblemDetail tratarRecursoDuplicado(RecursoDuplicadoException exception, HttpServletRequest request) {
        return criar(HttpStatus.CONFLICT, "Recurso já cadastrado", exception.getMessage(), request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail tratarIntegridade(DataIntegrityViolationException exception, HttpServletRequest request) {
        return criar(HttpStatus.CONFLICT, "Conflito de dados",
                "A operação viola uma restrição do banco de dados (registro duplicado ou referência inválida).", request);
    }

    /** Validações feitas pelas próprias entidades (ex.: e-mail inválido, UF com mais de duas letras). */
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail tratarArgumentoInvalido(IllegalArgumentException exception, HttpServletRequest request) {
        return criar(HttpStatus.BAD_REQUEST, "Dados de requisição inválidos", exception.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail tratarValidacao(MethodArgumentNotValidException exception, HttpServletRequest request) {
        ProblemDetail problema = criar(HttpStatus.BAD_REQUEST, "Dados de requisição inválidos",
                "Erro na validação dos campos informados", request);
        Map<String, String> erros = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors()
                .forEach(erro -> erros.putIfAbsent(erro.getField(), erro.getDefaultMessage()));
        problema.setProperty("erros", erros);
        return problema;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail tratarCorpoInvalido(HttpMessageNotReadableException exception, HttpServletRequest request) {
        return criar(HttpStatus.BAD_REQUEST, "Corpo da requisição inválido",
                "JSON malformado ou com valor não reconhecido (verifique enums como status e tipo).", request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail tratarParametroInvalido(MethodArgumentTypeMismatchException exception,
                                                 HttpServletRequest request) {
        return criar(HttpStatus.BAD_REQUEST, "Parâmetro inválido",
                "Valor inválido para o parâmetro '" + exception.getName() + "'.", request);
    }

    private ProblemDetail criar(HttpStatus status, String titulo, String detalhe, HttpServletRequest request) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(status, detalhe);
        problema.setTitle(titulo);
        problema.setInstance(URI.create(request.getRequestURI()));
        return problema;
    }
}
