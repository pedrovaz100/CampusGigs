package com.campusgigs.api.service;

import com.campusgigs.api.dto.CadastroUsuarioRequest;
import com.campusgigs.api.dto.UsuarioResponse;
import com.campusgigs.api.entity.Usuario;
import com.campusgigs.api.entity.enums.Papel;
import com.campusgigs.api.exception.EmailJaCadastradoException;
import com.campusgigs.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioResponse cadastrar(CadastroUsuarioRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new EmailJaCadastradoException(request.email());
        }

        Usuario usuario = Usuario.builder()
                .nome(request.nome())
                .email(request.email())
                .senhaHash(passwordEncoder.encode(request.senha()))
                .papel(Papel.USER)
                .build();

        return UsuarioResponse.de(usuarioRepository.save(usuario));
    }
}
