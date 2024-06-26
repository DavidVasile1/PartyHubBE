package com.partyhub.PartyHub.service.impl;

import com.partyhub.PartyHub.entities.Discount;
import com.partyhub.PartyHub.exceptions.DiscountNotFoundException;
import com.partyhub.PartyHub.repository.DiscountRepository;
import com.partyhub.PartyHub.service.DiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class DiscountServiceImpl implements DiscountService {
    private final DiscountRepository discountRepository;

    @Override
    public Discount saveDiscount(Discount discount) {
        return discountRepository.save(discount);
    }
    @Override
    public Discount findByCode(String code) {
        return discountRepository.findByCode(code)
                .orElseThrow(() -> new DiscountNotFoundException("Discount not found with code: " + code));
    }

    @Override
    public void deleteDiscountByCode(String code) {
        discountRepository.deleteByCode(code);
    }
}
