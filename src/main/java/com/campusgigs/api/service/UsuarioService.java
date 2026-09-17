package com.campusgigs.api.service;

import com.campusgigs.api.dto.CadastroUsuarioRequest;
import com.campusgigs.api.dto.EnderecoCep;
import com.campusgigs.api.dto.UsuarioResponse;
import com.campusgigs.api.entity.Usuario;
import com.campusgigs.api.entity.enums.Papel;
import com.campusgigs.api.exception.EmailJaCadastradoException;
import com.campusgigs.api.exception.RecursoNaoEncontradoException;
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
    private final CepService cepService;

    @Transactional
    public UsuarioResponse cadastrar(CadastroUsuarioRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new EmailJaCadastradoException(request.email());
        }

        Usuario.UsuarioBuilder usuarioBuilder = Usuario.builder()
                .nome(request.nome())
                .email(request.email())
                .senhaHash(passwordEncoder.encode(request.senha()))
                .papel(Papel.USER);

        if (request.cep() != null && !request.cep().isBlank()) {
            EnderecoCep endereco = cepService.consultar(request.cep());
            usuarioBuilder.cep(endereco.cep())
                    .cidade(endereco.cidade())
                    .uf(endereco.uf());
        }

        return UsuarioResponse.de(usuarioRepository.save(usuarioBuilder.build()));
    }

    public UsuarioResponse buscarPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario nao encontrado"));

        return UsuarioResponse.de(usuario);
    }

    @Transactional
    public UsuarioResponse atualizarCep(String emailAutenticado, String cep) {
        Usuario usuario = usuarioRepository.findByEmail(emailAutenticado)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario nao encontrado"));

        EnderecoCep endereco = cepService.consultar(cep);
        usuario.setCep(endereco.cep());
        usuario.setCidade(endereco.cidade());
        usuario.setUf(endereco.uf());

        return UsuarioResponse.de(usuario);
    }
}
