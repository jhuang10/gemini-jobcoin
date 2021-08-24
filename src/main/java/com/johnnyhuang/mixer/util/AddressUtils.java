package com.johnnyhuang.mixer.util;


import com.johnnyhuang.mixer.domain.models.Address;
import com.johnnyhuang.mixer.domain.models.DepositAddress;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Utils class for accessing the House Address and generating random UUID address
 */
public class AddressUtils {

    private static final Address HOUSE_ADDRESS = new Address("HOUSE ADDRESS");
    public static final long SECONDS_TILL_EXPIRATION = 60L;

    public static Address getHouseAddress(){
        return HOUSE_ADDRESS;
    }

    public static DepositAddress generateDepositAddress() {
        return new DepositAddress(UUID.randomUUID().toString(), LocalDateTime.now().plusSeconds(SECONDS_TILL_EXPIRATION));
    }

}
