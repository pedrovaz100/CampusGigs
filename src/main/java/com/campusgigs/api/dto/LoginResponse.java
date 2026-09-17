package com.campusgigs.api.dto;

public record LoginResponse(
        String mensagem,
        UsuarioResponse usuario
) {
}
