package com.works.repositories;

import com.works.entities.constant.pets.JoinPetCustomer;
import org.springframework.data.jpa.repository.JpaRepository;


public interface JoinPetCustomerRepository extends JpaRepository<JoinPetCustomer, Integer> {
}
