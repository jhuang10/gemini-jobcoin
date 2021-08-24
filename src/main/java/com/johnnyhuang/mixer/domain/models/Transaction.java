package com.johnnyhuang.mixer.domain.models;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class Transaction {
    Address fromAddress;
    Address toAddress;
    Float amount;
}
