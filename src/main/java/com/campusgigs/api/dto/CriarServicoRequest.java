package com.campusgigs.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CriarServicoRequest(

        @NotBlank(message = "Titulo e obrigatorio")
        @Size(max = 150, message = "Titulo deve ter no maximo 150 caracteres")
        String titulo,

        @NotBlank(message = "Descricao e obrigatoria")
        @Size(max = 2000, message = "Descricao deve ter no maximo 2000 caracteres")
        String descricao,

        @NotBlank(message = "Categoria e obrigatoria")
        @Size(max = 80, message = "Categoria deve ter no maximo 80 caracteres")
        String categoria,

        @NotNull(message = "Preco e obrigatorio")
        @DecimalMin(value = "0.01", message = "Preco deve ser positivo")
        BigDecimal preco
) {
}
