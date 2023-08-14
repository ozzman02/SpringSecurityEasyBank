package com.eazybytes.controller;

import com.eazybytes.model.Accounts;
import com.eazybytes.model.Customer;
import com.eazybytes.repository.AccountsRepository;
import com.eazybytes.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountController {

    @Autowired
    private AccountsRepository accountsRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/myAccount")
    public Accounts getAccountDetails(@RequestParam String email) {
        Customer customer = customerRepository.findByEmail(email);
        if (customer != null) {
            return accountsRepository.findByCustomerId(customer.getId());
        }
        return null;
    }

}
