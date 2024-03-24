package com.partyhub.PartyHub.exceptions;


public class StatisticsNotFoundException extends RuntimeException{
    public StatisticsNotFoundException(String message){
        super(message);
    }
}