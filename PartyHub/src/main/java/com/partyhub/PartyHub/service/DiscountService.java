package com.partyhub.PartyHub.service;

import com.partyhub.PartyHub.entities.Discount;


public interface DiscountService {
    Discount saveDiscount(Discount discount);
     Discount findByCode(String code);
     void deleteDiscountByCode(String code);

}
