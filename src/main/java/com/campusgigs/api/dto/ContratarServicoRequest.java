package com.campusgigs.api.dto;

import jakarta.validation.constraints.NotNull;

public record ContratarServicoRequest(

        @NotNull(message = "Servico e obrigatorio")
        Long servicoId
) {
}
