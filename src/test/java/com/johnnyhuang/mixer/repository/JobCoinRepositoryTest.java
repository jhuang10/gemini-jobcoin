package com.johnnyhuang.mixer.repository;

import com.johnnyhuang.mixer.client.JobCoinApiClient;
import com.johnnyhuang.mixer.domain.models.Address;
import com.johnnyhuang.mixer.domain.models.Transaction;
import com.johnnyhuang.mixer.dto.AddressInfoDTO;
import com.johnnyhuang.mixer.dto.StatusCodeResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class JobCoinRepositoryTest {

    @Mock
    private JobCoinApiClient jobCoinApiClient;

    private JobCoinRepository jobCoinRepository;

    @BeforeEach
    void setUp() {
        openMocks(this);
        jobCoinRepository = new JobCoinRepository(jobCoinApiClient);
    }

    @Test
    void shouldRetrieveTheBalanceOfAddress() {

        Address address = new Address("address");
        AddressInfoDTO addressInfoDTO = mock(AddressInfoDTO.class);
        Float expectedBalance = 10F;
        when(jobCoinApiClient.getAddressInfo(address)).thenReturn(Mono.just(addressInfoDTO));
        when(addressInfoDTO.getBalance()).thenReturn(expectedBalance);

        assertEquals(expectedBalance, jobCoinRepository.getBalance(address).block());

    }

    @Test
    void shouldReturnTrueIfJobCoinsWereSentCorrectly() {

        Transaction transaction = new Transaction(new Address("from"), new Address("to"), 10F);
        StatusCodeResponseDto responseDTO = mock(StatusCodeResponseDto.class);
        when(jobCoinApiClient.sendJobCoins(transaction)).thenReturn(Mono.just(responseDTO));
        assertEquals(Boolean.TRUE, jobCoinRepository.submitTransaction(transaction).block());

    }

    @Test
    void shouldReturnFalseIfJobCoinsWereNotSentCorrectly() {

        Transaction transaction = new Transaction(new Address("from"), new Address("to"), 10F);
        when(jobCoinApiClient.sendJobCoins(transaction)).thenReturn(Mono.empty());
        assertEquals(Boolean.FALSE, jobCoinRepository.submitTransaction(transaction).block());

    }
}