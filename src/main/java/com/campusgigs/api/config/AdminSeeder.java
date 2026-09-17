package com.campusgigs.api.config;

import com.campusgigs.api.entity.Usuario;
import com.campusgigs.api.entity.enums.Papel;
import com.campusgigs.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class AdminSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:}")
    private String adminEmail;

    @Value("${app.admin.senha:}")
    private String adminSenha;

    @Override
    public void run(String... args) {
        if (adminEmail.isBlank() || adminSenha.isBlank()) {
            log.warn("ADMIN_EMAIL/ADMIN_PASSWORD nao definidos - nenhum usuario ADMIN de teste foi criado");
            return;
        }

        if (usuarioRepository.existsByEmail(adminEmail)) {
            return;
        }

        Usuario admin = Usuario.builder()
                .nome("Administrador CampusGigs")
                .email(adminEmail)
                .senhaHash(passwordEncoder.encode(adminSenha))
                .papel(Papel.ADMIN)
                .build();

        usuarioRepository.save(admin);
        log.info("Usuario ADMIN de teste criado para o ambiente de desenvolvimento: {}", adminEmail);
    }
}
