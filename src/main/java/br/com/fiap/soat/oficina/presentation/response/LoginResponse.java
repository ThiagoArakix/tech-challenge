package br.com.fiap.soat.oficina.presentation.response;

import java.util.List;

public record LoginResponse(
        String token,
        String tipo,
        long expiresIn,
        String username,
        List<String> roles
) {
    public static LoginResponse de(String token, long expiresIn, String username, List<String> roles) {
        return new LoginResponse(token, "Bearer", expiresIn, username, roles);
    }
}

