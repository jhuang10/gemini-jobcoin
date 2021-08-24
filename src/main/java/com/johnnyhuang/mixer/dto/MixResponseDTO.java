package com.johnnyhuang.mixer.dto;

import lombok.*;

import java.time.LocalDateTime;

@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MixResponseDTO {
    private String depositAddress;
    private LocalDateTime expiryDate;
}
