package com.johnnyhuang.mixer.dto;

import lombok.*;

/**
 * Helper class to get the payload from the JobCoinApi
 */

@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StatusCodeResponseDto {
    private String status;
}
