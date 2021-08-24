package com.johnnyhuang.mixer.dto;

import com.johnnyhuang.mixer.domain.models.Address;
import com.johnnyhuang.mixer.domain.models.MixRequest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MixRequestDTOTest {

    @Test
    void shouldConvertToMixRequestDomainObject() {
        List<String> destinations = Arrays.asList("d1", "d2", "d3");
        MixRequest mixRequest = new MixRequestDTO(destinations).toMixRequest();
        assertEquals(destinations, mixRequest.getDestinations().stream().map(Address::getAddress).collect(Collectors.toList()));
    }
}