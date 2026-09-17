package com.campusgigs.api.dto;

public record LoginResponse(
        String token,
        String tipo,
        UsuarioResponse usuario
) {
}
