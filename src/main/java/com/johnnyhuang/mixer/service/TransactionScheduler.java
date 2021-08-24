package com.johnnyhuang.mixer.service;

import com.johnnyhuang.mixer.domain.models.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.TimeUnit;

/**
 * Service that schedules transactions to be executed after some Delay
 */
@Service
@Slf4j
public class TransactionScheduler {

    private final JobCoinService jobCoinService;
    private final long minDelay;
    private final long maxDelay;
    private final Scheduler scheduler;

    public TransactionScheduler(JobCoinService jobCoinService ,
                                @Value("${mixer.transaction.delay.min}") long minDelay, @Value("${mixer.transaction.delay.max}") long maxDelay) {
        this.jobCoinService = jobCoinService;
        this.minDelay = minDelay;
        this.maxDelay = maxDelay;
        this.scheduler = Schedulers.boundedElastic();

    }

    public void scheduleTransaction(Transaction transaction) {
        // Some random delay
        long delay =  RandomUtils.nextLong(minDelay, maxDelay);

        Scheduler.Worker worker = scheduler.createWorker();

        log.info("Scheduling {} in {} second(s)", transaction, delay);
        worker.schedule(() -> {
            if (jobCoinService.submitTransaction(transaction)) {
                log.info("Executed {}", transaction);
            } else {
                log.info("Failed to execute {}", transaction);
            }
        }, delay, TimeUnit.SECONDS);
    }
}
