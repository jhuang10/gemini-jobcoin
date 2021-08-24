package com.johnnyhuang.mixer.client;

import com.johnnyhuang.mixer.dto.MixRequestDTO;
import com.johnnyhuang.mixer.dto.MixResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Non-blocking, reactive client to perform HTTP requests to our own Spring boot application located at localhost.
 * This class is mainly used for the UI created via vaadin
 *
 * @author Johnny Huang
 */

@Component
public class MixerClient {

	private final WebClient client;

	public MixerClient(WebClient.Builder builder, @Value("${mixer.api.uri}") String baseApi) {
		this.client = builder.baseUrl(baseApi).build();
	}

	public Mono<MixResponseDTO> mixJobCoins(MixRequestDTO mixRequestDTO) {
		return client
				.post()
				.uri("/mixer/mix")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.body(Mono.just(mixRequestDTO), MixRequestDTO.class)
				.retrieve()
				.bodyToMono(MixResponseDTO.class);
	}


}
