package org.springframework.boot.autoconfigure.data.aerospike.example.repository;

import org.springframework.boot.autoconfigure.data.aerospike.example.model.Customer;
import org.springframework.data.aerospike.repository.AerospikeRepository;

import java.util.List;

public interface CustomerRepository extends AerospikeRepository<Customer, String> {
    List<Customer> findByLastNameOrderByFirstNameAsc(String lastName);
}
