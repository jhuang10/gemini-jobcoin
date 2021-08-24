package com.johnnyhuang.mixer.controller;

import com.johnnyhuang.mixer.domain.models.DepositAddress;
import com.johnnyhuang.mixer.domain.models.MixRequest;
import com.johnnyhuang.mixer.dto.MixRequestDTO;
import com.johnnyhuang.mixer.dto.MixResponseDTO;
import com.johnnyhuang.mixer.exception.MixerRequestException;
import com.johnnyhuang.mixer.service.MixerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static com.johnnyhuang.mixer.util.AddressUtils.generateDepositAddress;

/**
 * Responsible for handling requests to mix jobcoins
 */
@RestController
@RequestMapping("/api/mixer")
@Slf4j
public class MixerController {

    private final MixerService mixerService;

    public MixerController(MixerService mixerService) {
        this.mixerService = mixerService;
    }

    @PostMapping(path = "/mix")
    @ResponseBody
    public MixResponseDTO mixJobCoins(@RequestBody MixRequestDTO mixRequestDTO) throws MixerRequestException {
        MixRequest mixRequest = process(mixRequestDTO);

        DepositAddress depositAddress = generateDepositAddress();

        mixerService.monitorDeposit(mixRequest, depositAddress).subscribe(
                success -> log.info("Done {} {} {}", success, mixRequest, depositAddress),
                e -> log.error("Did not execute mix because funds not received in time"));

        return new MixResponseDTO(depositAddress.getAddress(), depositAddress.getExpiryDate());

    }

    protected MixRequest process(MixRequestDTO mixRequestDTO) throws MixerRequestException {
        if (mixRequestDTO == null) {
            throw new MixerRequestException("Empty Request Payload");
        }

        if (mixRequestDTO.getDestinations() == null || mixRequestDTO.getDestinations().isEmpty()) {
            throw new MixerRequestException("At least 1 destination address is required");
        }

        return mixRequestDTO.toMixRequest();
    }


}
