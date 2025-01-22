package com.aerospike.example;

import org.springframework.data.aerospike.repository.AerospikeRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CustomerRepository extends AerospikeRepository<Customer, String>, CrudRepository<Customer, String> {
    List<Customer> findByLastNameOrderByFirstNameAsc(String lastName);
}
