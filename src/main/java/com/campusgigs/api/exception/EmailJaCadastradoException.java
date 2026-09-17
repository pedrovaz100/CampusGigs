package com.campusgigs.api.exception;

public class EmailJaCadastradoException extends RuntimeException {

    public EmailJaCadastradoException(String email) {
        super("O e-mail informado ja esta cadastrado: " + email);
    }
}
