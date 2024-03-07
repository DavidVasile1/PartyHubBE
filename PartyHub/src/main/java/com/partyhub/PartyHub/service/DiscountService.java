package com.partyhub.PartyHub.service;

import com.partyhub.PartyHub.entities.Discount;

import java.util.Optional;

public interface DiscountService {
    Discount saveDiscount(Discount discount);
    Optional<Discount> findByCode(String code);
    public void deleteDiscountByCode(String code);

}
