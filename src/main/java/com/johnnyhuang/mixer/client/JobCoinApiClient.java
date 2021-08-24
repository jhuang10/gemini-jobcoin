package com.johnnyhuang.mixer.client;

import com.johnnyhuang.mixer.domain.models.Address;
import com.johnnyhuang.mixer.domain.models.Transaction;
import com.johnnyhuang.mixer.dto.AddressInfoDTO;
import com.johnnyhuang.mixer.dto.StatusCodeResponseDto;
import com.johnnyhuang.mixer.dto.TransactionDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.util.Collections.EMPTY_LIST;

/**
 * Non-blocking, reactive client to perform HTTP requests to gemini's jobcoin api provided
 *
 */

@Component
public class JobCoinApiClient {

	private final WebClient client;

	public JobCoinApiClient(WebClient.Builder builder, @Value("${jobcoin.api.uri}") String baseApi) {
		this.client = builder.baseUrl(baseApi).build();
	}

	public Mono<AddressInfoDTO> getAddressInfo(Address address) {
		return client
				.get()
				.uri("/addresses/{address}", address.getAddress())
				.retrieve()
				.bodyToMono(AddressInfoDTO.class)
				.onErrorReturn(new AddressInfoDTO(0f, EMPTY_LIST));
	}

	public Flux<TransactionDTO> getTransactionHistory() {
		return client
				.get()
				.uri("/transactions")
				.retrieve()
				.bodyToFlux(TransactionDTO.class)
				.onErrorResume(Exception.class, e -> Flux.empty());
	}

	public Mono<StatusCodeResponseDto> sendJobCoins(Transaction transaction) {
		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("fromAddress", transaction.getFromAddress().getAddress());
		body.add("toAddress", transaction.getToAddress().getAddress());
		body.add("amount", String.valueOf(transaction.getAmount()));

		return client
				.post()
				.uri("/transactions")
				.bodyValue(body)
				.retrieve()
				.bodyToMono(StatusCodeResponseDto.class)
				.onErrorResume(Exception.class, e->Mono.empty());
	}
}
