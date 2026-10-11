package br.com.fiap.soat.oficina.infrastructure.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final String adminPasswordHash;
    private final String mecanicoPasswordHash;

    public CustomUserDetailsService(PasswordEncoder passwordEncoder) {
        this.adminPasswordHash = passwordEncoder.encode("admin123");
        this.mecanicoPasswordHash = passwordEncoder.encode("mecanico123");
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if ("admin".equals(username)) {
            return User.builder()
                    .username("admin")
                    .password(adminPasswordHash)
                    .roles("ADMIN")
                    .build();
        }
        if ("mecanico".equals(username)) {
            return User.builder()
                    .username("mecanico")
                    .password(mecanicoPasswordHash)
                    .roles("MECANICO")
                    .build();
        }
        throw new UsernameNotFoundException("Usuário não encontrado no sistema: " + username);
    }
}

