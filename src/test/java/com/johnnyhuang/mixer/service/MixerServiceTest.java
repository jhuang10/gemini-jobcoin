package com.johnnyhuang.mixer.service;

import com.johnnyhuang.mixer.domain.models.Address;
import com.johnnyhuang.mixer.domain.models.DepositAddress;
import com.johnnyhuang.mixer.domain.models.MixRequest;
import com.johnnyhuang.mixer.domain.models.Transaction;
import com.johnnyhuang.mixer.util.AddressUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import reactor.core.publisher.Flux;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class MixerServiceTest {
    MixerService mixerService;

    @Mock
    private JobCoinService jobCoinService;

    @Mock
    private TransactionScheduler transactionScheduler;


    @BeforeEach
    void setUp() {
        openMocks(this);
        mixerService = new MixerService(jobCoinService, transactionScheduler);
    }

    @Test
    public void shouldReturnTrueIfTransactionRecordedInDepositAddress() {

        MixRequest mixRequest = new MixRequest(Arrays.asList(new Address("d1"), new Address("d2")));
        DepositAddress depositAddress = AddressUtils.generateDepositAddress();

        when(jobCoinService.pollAndTransferToHouse(depositAddress)).thenReturn(Flux.just(10F));

        assertEquals(Boolean.TRUE, mixerService.monitorDeposit(mixRequest, depositAddress).blockLast());

    }

    @Test
    public void shouldReturnFalseIfNoTransactionRecorded() {
        MixRequest mixRequest = new MixRequest(Arrays.asList(new Address("d1"), new Address("d2")));
        DepositAddress depositAddress = AddressUtils.generateDepositAddress();
        when(jobCoinService.pollAndTransferToHouse(depositAddress)).thenReturn(Flux.just(0F));

        assertEquals(Boolean.FALSE, mixerService.monitorDeposit(mixRequest, depositAddress).blockLast());
    }


    @Test
    public void shouldExecuteMixIfAmountGreaterThanZero() {
        MixRequest mixRequest = new MixRequest(Arrays.asList(new Address("d1"), new Address("d2"), new Address("d3")));
        mixerService.executeMix(mixRequest, 100F);
        then(transactionScheduler).should(times(3)).scheduleTransaction(any(Transaction.class));
    }

    @Test
    public void shouldNotExecuteMixIfAmountIsZero() {
        MixRequest mixRequest = new MixRequest(Arrays.asList(new Address("d1"), new Address("d2"), new Address("d3")));
        mixerService.executeMix(mixRequest, 0F);
        then(transactionScheduler).should(times(0)).scheduleTransaction(any(Transaction.class));
    }
}