package br.com.fiap.soat.oficina.infrastructure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes de Unidade - JwtService")
class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService("9a7f8e2d4c6b1a3e5f7a9b0c2d4e6f8a1b3c5d7e9f0a2b4c6d8e0f1a3b5c7d9e", 3600000L);
        userDetails = User.builder()
                .username("admin")
                .password("admin123")
                .roles("ADMIN")
                .build();
    }

    @Test
    @DisplayName("Deve gerar token JWT válido e extrair username corretamente")
    void deveGerarEValidarToken() {
        String token = jwtService.gerarToken(userDetails);

        assertThat(token).isNotBlank();
        assertThat(jwtService.extrairUsername(token)).isEqualTo("admin");
        assertThat(jwtService.isTokenValido(token, userDetails)).isTrue();
        assertThat(jwtService.isTokenValido(token)).isTrue();
    }

    @Test
    @DisplayName("Deve invalidar token expirado")
    void deveInvalidarTokenExpirado() {
        // JwtService com 0ms de expiração
        JwtService jwtExpirado = new JwtService("9a7f8e2d4c6b1a3e5f7a9b0c2d4e6f8a1b3c5d7e9f0a2b4c6d8e0f1a3b5c7d9e", -1000L);
        String token = jwtExpirado.gerarToken(userDetails);

        assertThat(jwtService.isTokenValido(token, userDetails)).isFalse();
        assertThat(jwtService.isTokenValido(token)).isFalse();
    }
}

