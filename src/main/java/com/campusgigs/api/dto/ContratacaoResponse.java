package com.campusgigs.api.dto;

import com.campusgigs.api.entity.Contratacao;
import com.campusgigs.api.entity.enums.SituacaoContratacao;

import java.time.LocalDateTime;

public record ContratacaoResponse(
        Long id,
        Long servicoId,
        String servicoTitulo,
        Long contratanteId,
        String contratanteNome,
        SituacaoContratacao situacao,
        LocalDateTime criadoEm
) {

    public static ContratacaoResponse de(Contratacao contratacao) {
        return new ContratacaoResponse(
                contratacao.getId(),
                contratacao.getServico().getId(),
                contratacao.getServico().getTitulo(),
                contratacao.getContratante().getId(),
                contratacao.getContratante().getNome(),
                contratacao.getSituacao(),
                contratacao.getCriadoEm()
        );
    }
}
