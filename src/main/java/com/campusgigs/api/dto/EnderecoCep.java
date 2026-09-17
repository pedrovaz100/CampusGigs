package com.campusgigs.api.dto;

public record EnderecoCep(
        String cep,
        String cidade,
        String uf
) {
}
