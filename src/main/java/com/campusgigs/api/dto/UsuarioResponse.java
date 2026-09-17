package com.campusgigs.api.dto;

import com.campusgigs.api.entity.Usuario;
import com.campusgigs.api.entity.enums.Papel;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        Papel papel,
        String cep,
        String cidade,
        String uf
) {

    public static UsuarioResponse de(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getPapel(),
                usuario.getCep(),
                usuario.getCidade(),
                usuario.getUf()
        );
    }
}
