package com.johnnyhuang.mixer.dto;

import lombok.*;

import java.util.Date;

@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TransactionDTO {
    private Date timestamp;
    private String fromAddress;
    private String toAddress;
    private Float amount;
}
