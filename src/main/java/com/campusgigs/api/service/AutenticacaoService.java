package com.campusgigs.api.service;

import com.campusgigs.api.dto.LoginRequest;
import com.campusgigs.api.dto.LoginResponse;
import com.campusgigs.api.dto.UsuarioResponse;
import com.campusgigs.api.entity.Usuario;
import com.campusgigs.api.exception.CredenciaisInvalidasException;
import com.campusgigs.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AutenticacaoService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;

    public LoginResponse autenticar(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.senha()));
        } catch (AuthenticationException ex) {
            throw new CredenciaisInvalidasException();
        }

        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(CredenciaisInvalidasException::new);

        return new LoginResponse("Autenticacao realizada com sucesso", UsuarioResponse.de(usuario));
    }
}
