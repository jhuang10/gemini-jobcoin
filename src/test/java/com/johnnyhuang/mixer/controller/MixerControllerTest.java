package com.johnnyhuang.mixer.controller;

import com.johnnyhuang.mixer.domain.models.DepositAddress;
import com.johnnyhuang.mixer.domain.models.MixRequest;
import com.johnnyhuang.mixer.dto.MixRequestDTO;
import com.johnnyhuang.mixer.dto.MixResponseDTO;
import com.johnnyhuang.mixer.exception.MixerRequestException;
import com.johnnyhuang.mixer.service.MixerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import reactor.core.publisher.Flux;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class MixerControllerTest {

    @Mock
    private MixerService mixerService;
    MixerController mixerController;

    @BeforeEach
    void setUp() {
        openMocks(this);
        mixerController = new MixerController(mixerService);
    }

    @Test
    void shouldReturnDepositAddressAndExpirationDateIfValidRequest() throws MixerRequestException {
        MixRequestDTO mixRequestDTO = new MixRequestDTO(asList("d1", "d2", "d3"));
        when(mixerService.monitorDeposit(any(MixRequest.class), any(DepositAddress.class))).thenReturn(Flux.just(Boolean.TRUE));

        MixResponseDTO mixResponseDTO = mixerController.mixJobCoins(mixRequestDTO);
        assertNotNull(mixResponseDTO);
        assertNotNull(mixResponseDTO.getDepositAddress());
        assertNotNull(mixResponseDTO.getExpiryDate());
    }

    @Test
    void shouldThrowExceptionIfEmptyRequest() {
        MixRequestDTO mixRequestDTO = new MixRequestDTO();
        when(mixerService.monitorDeposit(any(MixRequest.class), any(DepositAddress.class))).thenReturn(Flux.just(Boolean.TRUE));

        Assertions.assertThrows(MixerRequestException.class, () -> {
            mixerController.mixJobCoins(mixRequestDTO);
        });
    }

    @Test
    void shouldThrowExceptionIfNoDestination() {
        MixRequestDTO mixRequestDTO = new MixRequestDTO(emptyList());
        when(mixerService.monitorDeposit(any(MixRequest.class), any(DepositAddress.class))).thenReturn(Flux.just(Boolean.TRUE));

        Assertions.assertThrows(MixerRequestException.class, () -> {
            mixerController.mixJobCoins(mixRequestDTO);
        });
    }

    @Test
    void shouldThrowExceptionIfNullDestination() {
        MixRequestDTO mixRequestDTO = new MixRequestDTO(null);
        when(mixerService.monitorDeposit(any(MixRequest.class), any(DepositAddress.class))).thenReturn(Flux.just(Boolean.TRUE));

        Assertions.assertThrows(MixerRequestException.class, () -> {
            mixerController.mixJobCoins(mixRequestDTO);
        });
    }
}