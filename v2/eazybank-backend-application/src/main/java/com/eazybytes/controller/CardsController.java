package com.eazybytes.controller;

import com.eazybytes.model.Cards;
import com.eazybytes.model.Customer;
import com.eazybytes.repository.CardsRepository;
import com.eazybytes.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CardsController {

    @Autowired
    private CardsRepository cardsRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/myCards")
    public List<Cards> getCardDetails(@RequestParam String email) {
        Customer customer = customerRepository.findByEmail(email);
        if (customer != null) {
            return cardsRepository.findByCustomerId(customer.getId());
        }
        return null;
    }

}
