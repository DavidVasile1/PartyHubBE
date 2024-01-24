package com.partyhub.PartyHub.exceptions;

public class EmailAlreadyUsedException extends RuntimeException{
    public EmailAlreadyUsedException(String message){
        super(message);
    }
}
