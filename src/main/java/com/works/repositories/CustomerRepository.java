package com.works.repositories;

import com.works.entities.Customer;
import com.works.entities.Product;
import com.works.entities.projections.CustomerInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    @Query(value = "select cu_id, cu_name, cu_surname, cu_note from Customer", nativeQuery = true)
    List<CustomerInfo> allCustomerInfos();

    @Query(value = "select * from Customer where upper(cu_name) like upper(concat('%', ?1, '%'))", nativeQuery = true)
    List<Customer> searchCustomer(String cu_name);

    @Query(value = "select * from customer order by created_date desc limit 0,30", nativeQuery = true)
    List<Customer> getCustomerSonOtuz();


}