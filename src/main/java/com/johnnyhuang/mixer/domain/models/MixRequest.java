package com.johnnyhuang.mixer.domain.models;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class MixRequest {
    List<Address> destinations;
}
