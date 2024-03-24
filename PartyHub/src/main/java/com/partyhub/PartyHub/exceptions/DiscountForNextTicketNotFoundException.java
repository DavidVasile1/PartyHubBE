package com.partyhub.PartyHub.exceptions;

public class DiscountForNextTicketNotFoundException extends RuntimeException{
    public DiscountForNextTicketNotFoundException(String message){
        super(message);
    }
}