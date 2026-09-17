package com.campusgigs.api.exception;

public class CepIndisponivelException extends RuntimeException {

    public CepIndisponivelException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
