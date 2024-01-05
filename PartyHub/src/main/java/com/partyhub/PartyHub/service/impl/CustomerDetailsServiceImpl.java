package com.partyhub.PartyHub.service.impl;

import com.partyhub.PartyHub.entities.CustomerDetails;
import com.partyhub.PartyHub.repository.CustomerDetailsRepository;
import com.partyhub.PartyHub.service.CustomerDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerDetailsServiceImpl implements CustomerDetailsService {
    private final CustomerDetailsRepository customerDetailsRepository;

    @Override
    public void save(CustomerDetails customerDetails) {
        customerDetailsRepository.save(customerDetails);
    }

    @Override
    public CustomerDetails create(int age, String fullName) {
        CustomerDetails customerDetails = new CustomerDetails();
        customerDetails.setAge(age);
        customerDetails.setFullName(fullName);
        customerDetails.setDiscountForNextTicket(0);
        save(customerDetails);
        return customerDetails;
    }
}
