package com.eazybank.controller;

import com.eazybank.model.Customer;
import com.eazybank.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
public class LoginController {

    private final CustomerRepository customerRepository;

    @Autowired
    public LoginController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @RequestMapping("/user")
    public Customer getUserDetailsAfterLogin(Principal user) {
        List<Customer> customers = customerRepository.findByEmail(user.getName());
        if (customers.size() > 0) {
            return customers.get(0);
        } else {
            return null;
        }
    }

}
