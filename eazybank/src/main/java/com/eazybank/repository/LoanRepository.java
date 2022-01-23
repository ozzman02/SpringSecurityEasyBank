package com.eazybank.repository;

import com.eazybank.model.Loans;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends CrudRepository<Loans, Long> {

    @PreAuthorize("hasRole('ROOT')")
    List<Loans> findByCustomerIdOrderByStartDtDesc(Long customerId);

}

