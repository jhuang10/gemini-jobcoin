package com.johnnyhuang.mixer.dto;

import com.johnnyhuang.mixer.domain.models.Address;
import com.johnnyhuang.mixer.domain.models.MixRequest;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MixRequestDTO {
    private List<String> destinations;

    public MixRequest toMixRequest() {
            List<Address> destinationAddresses = destinations.stream().map(Address::new).collect(Collectors.toList());
            return new MixRequest(destinationAddresses);
    }

}
