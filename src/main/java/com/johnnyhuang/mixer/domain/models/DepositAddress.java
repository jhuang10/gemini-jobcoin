package com.johnnyhuang.mixer.domain.models;

import lombok.*;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Value
public class DepositAddress extends Address {
    LocalDateTime expiryDate;

    public DepositAddress(String address, LocalDateTime expiryDate) {
        super(address);
        this.expiryDate = expiryDate;
    }

}

