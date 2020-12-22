package com.aerospike.example.sync;

import org.springframework.data.aerospike.repository.AerospikeRepository;

import java.util.List;

public interface SyncCustomerRepository extends AerospikeRepository<Customer, String> {
    List<Customer> findByLastNameOrderByFirstNameAsc(String lastName);
}
