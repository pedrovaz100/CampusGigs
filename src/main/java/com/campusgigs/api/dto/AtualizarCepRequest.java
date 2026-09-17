package com.campusgigs.api.dto;

import jakarta.validation.constraints.NotBlank;

public record AtualizarCepRequest(

        @NotBlank(message = "CEP e obrigatorio")
        String cep
) {
}
