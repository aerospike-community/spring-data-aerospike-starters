package com.aerospike.example.sync;

import org.springframework.data.aerospike.repository.AerospikeRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SyncCustomerRepository extends AerospikeRepository<Customer, String>, CrudRepository<Customer, String> {
    List<Customer> findByLastNameOrderByFirstNameAsc(String lastName);
}
