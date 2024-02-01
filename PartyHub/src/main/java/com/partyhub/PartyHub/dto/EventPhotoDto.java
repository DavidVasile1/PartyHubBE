package com.partyhub.PartyHub.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventPhotoDto {
    private UUID id;
    private String city;
    private byte[] mainBanner;
}
