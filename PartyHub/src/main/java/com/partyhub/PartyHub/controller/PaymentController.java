package com.partyhub.PartyHub.controller;

import com.partyhub.PartyHub.dto.ChargeRequest;
import com.partyhub.PartyHub.dto.PaymentResponse;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.param.ChargeCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
public class PaymentController {

    @Value("${stripe.keys.secret}")
    private String apiKey;

    @PostMapping("/charge")
    public PaymentResponse chargeCard(@RequestBody ChargeRequest chargeRequest) throws StripeException {
        Stripe.apiKey = apiKey;

        ChargeCreateParams params = ChargeCreateParams.builder()
                .setAmount(chargeRequest.getAmount())
                .setCurrency(chargeRequest.getCurrency())
                .setDescription(chargeRequest.getDescription())
                .setSource(chargeRequest.getToken())
                .build();

        Charge charge = Charge.create(params);
        return new PaymentResponse(charge.getId(), charge.getAmount(), charge.getCurrency(), charge.getDescription());
    }

}
