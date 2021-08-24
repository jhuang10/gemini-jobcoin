package com.johnnyhuang.mixer.service;

import com.helger.commons.annotation.VisibleForTesting;
import com.johnnyhuang.mixer.domain.models.Address;
import com.johnnyhuang.mixer.domain.models.DepositAddress;
import com.johnnyhuang.mixer.domain.models.MixRequest;
import com.johnnyhuang.mixer.domain.models.Transaction;
import com.johnnyhuang.mixer.exception.MixerTransferException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Random;

import static com.johnnyhuang.mixer.util.AddressUtils.getHouseAddress;

/**
 * The Mixer service is primary service that does the mixing.
 */
@Service
@Slf4j
public class MixerService {
    private final JobCoinService jobCoinService;
    private final TransactionScheduler transactionScheduler;

    public MixerService(JobCoinService jobCoinService, TransactionScheduler transactionScheduler) {
        this.jobCoinService = jobCoinService;
        this.transactionScheduler = transactionScheduler;
    }

    /**
     * Main method of the Mixer service.
     * Polls the JobCoin api repository for a transaction to the deposit address duration expiration period
     * Upon success it will execute the Mix.
     */
    public Flux<Boolean> monitorDeposit(MixRequest mixRequest, DepositAddress depositAddress) {
        return jobCoinService.pollAndTransferToHouse(depositAddress)
                .map(balance -> executeMix(mixRequest, balance))
                .onErrorReturn(MixerTransferException.class, false);
    }

    /**
     * This is the method that does the actual execution. It divides the request into smaller transactions
     * between the house address and the destination addresses. Amounts sent to the destination address
     * will be randomly split.
     */
    @VisibleForTesting
    protected Boolean executeMix(MixRequest mixRequest, Float amountToMix) {
        if (amountToMix == 0) {
            return Boolean.FALSE;
        }

        List<Address> destinations = mixRequest.getDestinations();
        int numberOfDeposits = destinations.size();
        float initialChunk = amountToMix / (numberOfDeposits + 1);
        float[] randomFloats = new float[numberOfDeposits];
        float sumOfRandoms = 0;

        final Random random = new Random();
        for (int i = 0; i < numberOfDeposits; i++) {
            randomFloats[i] = random.nextFloat();
            sumOfRandoms += randomFloats[i];
        }

        for (int i = 0; i < numberOfDeposits; i++) {
            float randomChunk = initialChunk + randomFloats[i] * (initialChunk / sumOfRandoms);
            log.info("Random chunk {} to be sent", randomChunk);
            transactionScheduler.scheduleTransaction(new Transaction(getHouseAddress(), destinations.get(i), randomChunk));
        }

        return Boolean.TRUE;

    }


}
