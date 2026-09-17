package com.campusgigs.api.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record ErroResponse(
        LocalDateTime timestamp,
        int status,
        String mensagem,
        Map<String, String> erros
) {

    public static ErroResponse de(int status, String mensagem) {
        return new ErroResponse(LocalDateTime.now(), status, mensagem, null);
    }

    public static ErroResponse de(int status, String mensagem, Map<String, String> erros) {
        return new ErroResponse(LocalDateTime.now(), status, mensagem, erros);
    }
}
