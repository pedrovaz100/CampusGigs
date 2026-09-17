package com.campusgigs.api.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

public interface ViaCepClient {

    @GetExchange("/{cep}/json")
    ViaCepResponse buscarCep(@PathVariable String cep);
}
