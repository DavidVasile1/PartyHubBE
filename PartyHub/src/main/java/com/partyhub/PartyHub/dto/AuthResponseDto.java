package com.partyhub.PartyHub.dto;

import lombok.Data;

@Data
public class AuthResponseDto {
    private String accessToken;
    private boolean activated = true;
    private String tokenType = "Bearer ";

    public AuthResponseDto(String accessToken) {
        this.accessToken = accessToken;
    }

}
