package com.johnnyhuang.mixer.service;

import com.johnnyhuang.mixer.domain.models.DepositAddress;
import com.johnnyhuang.mixer.domain.models.Transaction;
import com.johnnyhuang.mixer.exception.MixerTransferException;
import com.johnnyhuang.mixer.repository.JobCoinRepository;
import com.johnnyhuang.mixer.util.AddressUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@Slf4j
public class JobCoinService {

    private final JobCoinRepository jobCoinRepository;

    public JobCoinService(JobCoinRepository jobCoinRepository) {
        this.jobCoinRepository = jobCoinRepository;
        log.info("Started");
    }

    /**
     * Relays the transaction to the jobCoin Repository.
     * Returns True if transaction succeeds and False if it fails.
     */
    public Boolean submitTransaction(Transaction transaction) {
        return jobCoinRepository.submitTransaction(transaction).block();
    }

    /**
     * Deposit Address will be a new UUID with zero balance. We will Poll every second for non-zero balance.
     * After a balance has been detected, we move that balance to the house address.
     * No transfer and Throw error if balance is still zero after expiration Duration has ended.
     * Returns the funds sent to house Address
     */
    public Flux<Float> pollAndTransferToHouse(DepositAddress depositAddress) {
        Duration expirationDuration = Duration.between(LocalDateTime.now(), depositAddress.getExpiryDate());

        return Flux.interval(Duration.ofSeconds(1))
                .take(expirationDuration)
                .flatMap(interval -> jobCoinRepository.getBalance(depositAddress))
                .takeUntil(balance -> balance > 0)
                .flatMap(balance -> {
                    if (balance > 0 ) {
                        return jobCoinRepository.submitTransaction(new Transaction(depositAddress, AddressUtils.getHouseAddress(), balance))
                                .map(success -> success? balance: 0F);
                    }
                    return Mono.empty();
                })
                .switchIfEmpty(s -> s.onError(new MixerTransferException("Balance insufficient")));

    }
}
