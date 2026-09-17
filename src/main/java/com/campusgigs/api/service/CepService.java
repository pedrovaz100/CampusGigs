package com.campusgigs.api.service;

import com.campusgigs.api.client.ViaCepClient;
import com.campusgigs.api.client.ViaCepResponse;
import com.campusgigs.api.dto.EnderecoCep;
import com.campusgigs.api.exception.CepIndisponivelException;
import com.campusgigs.api.exception.CepInvalidoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class CepService {

    private static final Pattern CEP_VALIDO = Pattern.compile("\\d{8}");

    private final ViaCepClient viaCepClient;

    public EnderecoCep consultar(String cepBruto) {
        String cepNormalizado = normalizar(cepBruto);

        ViaCepResponse resposta;
        try {
            resposta = viaCepClient.buscarCep(cepNormalizado);
        } catch (RestClientException ex) {
            throw new CepIndisponivelException("Falha ao consultar o servico de CEP", ex);
        }

        if (resposta == null || Boolean.TRUE.equals(resposta.erro())
                || resposta.localidade() == null || resposta.uf() == null) {
            throw new CepInvalidoException("CEP nao encontrado: " + cepBruto);
        }

        String cepFormatado = cepNormalizado.substring(0, 5) + "-" + cepNormalizado.substring(5);
        return new EnderecoCep(cepFormatado, resposta.localidade(), resposta.uf());
    }

    private String normalizar(String cepBruto) {
        if (cepBruto == null || cepBruto.isBlank()) {
            throw new CepInvalidoException("CEP e obrigatorio");
        }

        String digitos = cepBruto.replaceAll("\\D", "");

        if (!CEP_VALIDO.matcher(digitos).matches()) {
            throw new CepInvalidoException("CEP invalido. Formato esperado: 00000-000");
        }

        return digitos;
    }
}
