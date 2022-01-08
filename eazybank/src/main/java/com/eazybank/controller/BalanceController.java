package com.eazybank.controller;

import com.eazybank.model.AccountTransactions;
import com.eazybank.model.Customer;
import com.eazybank.repository.AccountTransactionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BalanceController {

    private final AccountTransactionsRepository accountTransactionsRepository;

    @Autowired
    public BalanceController(AccountTransactionsRepository accountTransactionsRepository) {
        this.accountTransactionsRepository = accountTransactionsRepository;
    }

    @PostMapping("/myBalance")
    public List<AccountTransactions> getBalanceDetails(@RequestBody Customer customer) {
        List<AccountTransactions> accountTransactions =
                accountTransactionsRepository.findByCustomerIdOrderByTransactionDtDesc(customer.getId());
        if (accountTransactions != null ) {
            return accountTransactions;
        }else {
            return null;
        }
    }

}
