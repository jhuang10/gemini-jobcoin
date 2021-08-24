package com.johnnyhuang.mixer.repository;

import com.johnnyhuang.mixer.client.JobCoinApiClient;
import com.johnnyhuang.mixer.domain.models.Address;
import com.johnnyhuang.mixer.domain.models.Transaction;
import com.johnnyhuang.mixer.dto.AddressInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * The actual component that will submit transactions and check addresses
 * Using reactor the nuances of how a transaction fails are thrown away to return defaults
 */
@Repository
@Slf4j
public class JobCoinRepository {

    private final JobCoinApiClient jobCoinApiClient;

    public JobCoinRepository(JobCoinApiClient jobCoinApiClient) {
        this.jobCoinApiClient = jobCoinApiClient;
    }

    public Mono<Float> getBalance(Address address) {
        return jobCoinApiClient.getAddressInfo(address).map(AddressInfoDTO::getBalance);
    }

    public Mono<Boolean> submitTransaction(Transaction transaction) {
        return jobCoinApiClient.sendJobCoins(transaction)
                .map(statusCode -> true)
                .defaultIfEmpty(false);
    }
}
