package com.johnnyhuang.mixer.util;

import com.johnnyhuang.mixer.domain.models.DepositAddress;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class AddressUtilsTest {

    @Test
    void shouldGenerateDepositAddress() {
        DepositAddress depositAddress = AddressUtils.generateDepositAddress();
        assertNotNull(depositAddress);
        assertNotNull(depositAddress.getAddress());
    }
}