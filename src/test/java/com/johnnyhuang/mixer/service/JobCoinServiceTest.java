package com.johnnyhuang.mixer.service;

import com.johnnyhuang.mixer.domain.models.Address;
import com.johnnyhuang.mixer.domain.models.DepositAddress;
import com.johnnyhuang.mixer.domain.models.Transaction;
import com.johnnyhuang.mixer.exception.MixerTransferException;
import com.johnnyhuang.mixer.repository.JobCoinRepository;
import com.johnnyhuang.mixer.util.AddressUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class JobCoinServiceTest {

    @Mock
    private JobCoinRepository jobCoinRepository;

    JobCoinService jobCoinService;

    @BeforeEach
    void setUp() {
        openMocks(this);
        jobCoinService = new JobCoinService(jobCoinRepository);
    }

    @Test
    void shouldReturnFundsTransferredToHouseAddressIfDepositAddressReceivedFundsWithinExpirationTime() {
        DepositAddress depositAddress = AddressUtils.generateDepositAddress();
        float expectedBalance = 10F;
        when(jobCoinRepository.getBalance(depositAddress)).thenReturn(Mono.just(expectedBalance));
        when(jobCoinRepository.submitTransaction(new Transaction(depositAddress, AddressUtils.getHouseAddress(), expectedBalance)))
                .thenReturn(Mono.just(Boolean.TRUE));

        assertEquals(expectedBalance, jobCoinService.pollAndTransferToHouse(depositAddress).blockLast());
    }

    @Test
    void shouldThrowMixerTransferExceptionIfNoFundsTransferredToDepositAddressWithinExpirationTime() {
        DepositAddress depositAddress = new DepositAddress("test", LocalDateTime.now());
        float expectedBalance = 10F;
        when(jobCoinRepository.getBalance(depositAddress)).thenReturn(Mono.just(expectedBalance));
        when(jobCoinRepository.submitTransaction(new Transaction(depositAddress, AddressUtils.getHouseAddress(), expectedBalance)))
                .thenReturn(Mono.just(Boolean.TRUE));

        StepVerifier.create(jobCoinService.pollAndTransferToHouse(depositAddress)).expectError(MixerTransferException.class).verify();
    }

    @Test
    void shouldReturnZeroIfTransferToHouseAddressFails() {
        DepositAddress depositAddress = AddressUtils.generateDepositAddress();
        when(jobCoinRepository.getBalance(depositAddress)).thenReturn(Mono.just(10F));
        when(jobCoinRepository.submitTransaction(new Transaction(depositAddress, AddressUtils.getHouseAddress(), 10F))).thenReturn(Mono.just(Boolean.FALSE));

        assertEquals(0, jobCoinService.pollAndTransferToHouse(depositAddress).blockLast());
    }

    @Test
    void submitTransactionShouldPassTransactionToJobCoinRepository() {
        Transaction transaction = new Transaction(new Address("from"), new Address("to"), 10F);
        when(jobCoinRepository.submitTransaction(transaction)).thenReturn(Mono.just(Boolean.TRUE));
        assertTrue(jobCoinService.submitTransaction(transaction));
    }
}