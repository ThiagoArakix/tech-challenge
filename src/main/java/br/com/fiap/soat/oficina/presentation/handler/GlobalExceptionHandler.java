package br.com.fiap.soat.oficina.presentation.handler;

import br.com.fiap.soat.oficina.domain.exception.DomainException;
import br.com.fiap.soat.oficina.domain.exception.RecursoNaoEncontradoException;
import br.com.fiap.soat.oficina.domain.exception.SaldoEstoqueInsuficienteException;
import br.com.fiap.soat.oficina.domain.exception.TransicaoStatusInvalidaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ProblemDetail handleRecursoNaoEncontrado(RecursoNaoEncontradoException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Recurso Não Encontrado");
        problem.setType(URI.create("https://api.oficina.com.br/erros/recurso-nao-encontrado"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(TransicaoStatusInvalidaException.class)
    public ProblemDetail handleTransicaoStatusInvalida(TransicaoStatusInvalidaException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        problem.setTitle("Transição de Estado Inválida");
        problem.setType(URI.create("https://api.oficina.com.br/erros/transicao-invalida"));
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("statusAtual", ex.getStatusAtual());
        problem.setProperty("statusDestino", ex.getStatusDestino());
        return problem;
    }

    @ExceptionHandler(SaldoEstoqueInsuficienteException.class)
    public ProblemDetail handleSaldoEstoqueInsuficiente(SaldoEstoqueInsuficienteException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Saldo de Estoque Insuficiente");
        problem.setType(URI.create("https://api.oficina.com.br/erros/estoque-insuficiente"));
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("saldoAtual", ex.getSaldoAtual());
        problem.setProperty("quantidadeSolicitada", ex.getQuantidadeSolicitada());
        return problem;
    }

    @ExceptionHandler(DomainException.class)
    public ProblemDetail handleDomainException(DomainException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("Violação de Regra de Negócio");
        problem.setType(URI.create("https://api.oficina.com.br/erros/regra-de-negocio"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(MethodArgumentNotValidException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Um ou mais campos informados são inválidos");
        problem.setTitle("Erro de Validação de Dados");
        problem.setType(URI.create("https://api.oficina.com.br/erros/validacao-dados"));
        problem.setProperty("timestamp", Instant.now());

        Map<String, String> erros = new LinkedHashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            erros.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        problem.setProperty("erros", erros);
        return problem;
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentials(BadCredentialsException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED, "Credenciais de acesso inválidas: usuário ou senha incorretos");
        problem.setTitle("Falha de Autenticação");
        problem.setType(URI.create("https://api.oficina.com.br/erros/autenticacao-invalida"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handleAuthenticationException(AuthenticationException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED, "Não autenticado ou token expirado/inválido");
        problem.setTitle("Acesso Não Autorizado");
        problem.setType(URI.create("https://api.oficina.com.br/erros/nao-autorizado"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN, "Você não possui permissão para acessar este recurso");
        problem.setTitle("Acesso Proibido");
        problem.setType(URI.create("https://api.oficina.com.br/erros/acesso-proibido"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro interno inesperado no servidor");
        problem.setTitle("Erro Interno do Servidor");
        problem.setType(URI.create("https://api.oficina.com.br/erros/erro-interno"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
}

