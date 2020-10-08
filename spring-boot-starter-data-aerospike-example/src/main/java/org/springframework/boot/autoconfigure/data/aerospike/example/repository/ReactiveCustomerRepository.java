package org.springframework.boot.autoconfigure.data.aerospike.example.repository;

import org.springframework.boot.autoconfigure.data.aerospike.example.entity.Customer;
import org.springframework.data.aerospike.repository.ReactiveAerospikeRepository;
import reactor.core.publisher.Flux;

public interface ReactiveCustomerRepository extends ReactiveAerospikeRepository<Customer, String> {
    Flux<Customer> findByLastNameOrderByFirstNameAsc(String lastName);
}
