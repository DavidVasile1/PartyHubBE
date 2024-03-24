package com.partyhub.PartyHub.exceptions;


public class UserDetailsNotFoundException extends RuntimeException{
    public UserDetailsNotFoundException(String message){
        super(message);
    }
}
