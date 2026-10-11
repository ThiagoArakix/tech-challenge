package br.com.fiap.soat.oficina.presentation.handler;

import br.com.fiap.soat.oficina.domain.exception.DomainException;
import br.com.fiap.soat.oficina.domain.exception.RecursoNaoEncontradoException;
import br.com.fiap.soat.oficina.domain.exception.SaldoEstoqueInsuficienteException;
import br.com.fiap.soat.oficina.domain.exception.TransicaoStatusInvalidaException;
import br.com.fiap.soat.oficina.domain.valueobject.StatusOS;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes de Unidade - GlobalExceptionHandler (ProblemDetail RFC 7807)")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("DomainException deve ser mapeada para HTTP 400 Bad Request")
    void deveMapearDomainException() {
        DomainException ex = new DomainException("Regra de negócio violada");
        ProblemDetail problem = handler.handleDomainException(ex);

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(problem.getDetail()).isEqualTo("Regra de negócio violada");
        assertThat(problem.getTitle()).isEqualTo("Violação de Regra de Negócio");
    }

    @Test
    @DisplayName("RecursoNaoEncontradoException deve ser mapeada para HTTP 404 Not Found")
    void deveMapearRecursoNaoEncontrado() {
        RecursoNaoEncontradoException ex = RecursoNaoEncontradoException.de("Cliente", "123");
        ProblemDetail problem = handler.handleRecursoNaoEncontrado(ex);

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(problem.getTitle()).isEqualTo("Recurso Não Encontrado");
    }

    @Test
    @DisplayName("TransicaoStatusInvalidaException deve ser mapeada para HTTP 422 Unprocessable Entity")
    void deveMapearTransicaoInvalida() {
        TransicaoStatusInvalidaException ex = new TransicaoStatusInvalidaException(StatusOS.RECEBIDA, StatusOS.FINALIZADA);
        ProblemDetail problem = handler.handleTransicaoStatusInvalida(ex);

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        assertThat(problem.getTitle()).isEqualTo("Transição de Estado Inválida");
        assertThat(problem.getProperties()).containsEntry("statusAtual", StatusOS.RECEBIDA);
        assertThat(problem.getProperties()).containsEntry("statusDestino", StatusOS.FINALIZADA);
    }

    @Test
    @DisplayName("SaldoEstoqueInsuficienteException deve ser mapeada para HTTP 409 Conflict")
    void deveMapearSaldoEstoqueInsuficiente() {
        SaldoEstoqueInsuficienteException ex = new SaldoEstoqueInsuficienteException(2, 5);
        ProblemDetail problem = handler.handleSaldoEstoqueInsuficiente(ex);

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(problem.getTitle()).isEqualTo("Saldo de Estoque Insuficiente");
        assertThat(problem.getProperties()).containsEntry("saldoAtual", 2);
        assertThat(problem.getProperties()).containsEntry("quantidadeSolicitada", 5);
    }

    @Test
    @DisplayName("BadCredentialsException deve ser mapeada para HTTP 401")
    void deveMapearBadCredentials() {
        org.springframework.security.authentication.BadCredentialsException ex =
                new org.springframework.security.authentication.BadCredentialsException("Credenciais inválidas");
        ProblemDetail problem = handler.handleBadCredentials(ex);

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(problem.getTitle()).isEqualTo("Falha de Autenticação");
    }

    @Test
    @DisplayName("AuthenticationException deve ser mapeada para HTTP 401")
    void deveMapearAuthenticationException() {
        org.springframework.security.authentication.InsufficientAuthenticationException ex =
                new org.springframework.security.authentication.InsufficientAuthenticationException("Não autenticado");
        ProblemDetail problem = handler.handleAuthenticationException(ex);

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(problem.getTitle()).isEqualTo("Acesso Não Autorizado");
    }

    @Test
    @DisplayName("AccessDeniedException deve ser mapeada para HTTP 403")
    void deveMapearAccessDenied() {
        org.springframework.security.access.AccessDeniedException ex =
                new org.springframework.security.access.AccessDeniedException("Sem permissão");
        ProblemDetail problem = handler.handleAccessDenied(ex);

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(problem.getTitle()).isEqualTo("Acesso Proibido");
    }

    @Test
    @DisplayName("Exception genérica deve ser mapeada para HTTP 500")
    void deveMapearExceptionGenerica() {
        RuntimeException ex = new RuntimeException("Erro inesperado");
        ProblemDetail problem = handler.handleGenericException(ex);

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(problem.getTitle()).isEqualTo("Erro Interno do Servidor");
    }
}

