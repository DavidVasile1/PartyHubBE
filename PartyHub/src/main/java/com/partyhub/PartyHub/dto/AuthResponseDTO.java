package com.partyhub.PartyHub.dto;

@Deprecated
public class AuthResponseDTO {
    private String accessToken;
    private String tokenType="Bearer";

    public  AuthResponseDTO(String accessToken){
        this.accessToken=accessToken;
    }
}
