package com.johnnyhuang.mixer.dto;

import lombok.*;

import java.util.List;

@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AddressInfoDTO {
    private Float balance;
    private List<TransactionDTO> transactions;

}
