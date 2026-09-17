package com.campusgigs.api.dto;

import com.campusgigs.api.entity.Servico;
import com.campusgigs.api.entity.enums.SituacaoServico;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ServicoResponse(
        Long id,
        String titulo,
        String descricao,
        String categoria,
        BigDecimal preco,
        SituacaoServico situacao,
        Long prestadorId,
        String prestadorNome,
        LocalDateTime criadoEm
) {

    public static ServicoResponse de(Servico servico) {
        return new ServicoResponse(
                servico.getId(),
                servico.getTitulo(),
                servico.getDescricao(),
                servico.getCategoria(),
                servico.getPreco(),
                servico.getSituacao(),
                servico.getPrestador().getId(),
                servico.getPrestador().getNome(),
                servico.getCriadoEm()
        );
    }
}
