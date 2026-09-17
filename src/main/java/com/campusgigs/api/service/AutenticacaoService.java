package com.campusgigs.api.service;

import com.campusgigs.api.dto.LoginRequest;
import com.campusgigs.api.dto.LoginResponse;
import com.campusgigs.api.dto.UsuarioResponse;
import com.campusgigs.api.entity.Usuario;
import com.campusgigs.api.exception.CredenciaisInvalidasException;
import com.campusgigs.api.repository.UsuarioRepository;
import com.campusgigs.api.security.JwtService;
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
    private final JwtService jwtService;

    public LoginResponse autenticar(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.senha()));
        } catch (AuthenticationException ex) {
            throw new CredenciaisInvalidasException();
        }

        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(CredenciaisInvalidasException::new);

        String token = jwtService.gerarToken(usuario);
        return new LoginResponse(token, "Bearer", UsuarioResponse.de(usuario));
    }
}
