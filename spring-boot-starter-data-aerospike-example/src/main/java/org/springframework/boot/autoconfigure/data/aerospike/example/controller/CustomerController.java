package org.springframework.boot.autoconfigure.data.aerospike.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.aerospike.example.model.Customer;
import org.springframework.boot.autoconfigure.data.aerospike.example.repository.CustomerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
public class CustomerController {
    @Autowired
    private CustomerRepository repository;

    @PostMapping("/customer")
    public Mono<Customer> createCustomer(@Valid @RequestBody Customer customer) {
        return repository.save(customer)
                .doOnSuccess(result -> log.info("Created " + result));
    }

    @GetMapping("/customer/{id}")
    public Mono<ResponseEntity<Customer>> getCustomerById(@PathVariable(value = "id") String customerId) {
        return repository.findById(customerId)
                .doOnSuccess(result -> log.info("Retrieved " + result))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/customer/{id}")
    public Mono<ResponseEntity<Customer>> updateCustomer(@PathVariable(value = "id") String customerId,
                                                         @Valid @RequestBody Customer customer) {
        return repository.findById(customerId)
                .flatMap(existing -> {
                    existing.setFirstName(customer.getFirstName());
                    existing.setLastName(customer.getLastName());
                    existing.setAge(customer.getAge());
                    return repository.save(existing)
                            .doOnSuccess(result -> log.info("Updated " + result));
                })
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/customer/{id}")
    public Mono<ResponseEntity<Void>> deleteCustomer(@PathVariable(value = "id") String customerId) {
        return repository.findById(customerId)
                .flatMap(existing ->
                        repository.delete(existing)
                                .then(Mono.just(new ResponseEntity<Void>(OK)))
                                .doOnSuccess(result -> log.info("Deleted " + existing))
                )
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/customers")
    public Flux<Customer> getAllCustomers() {
        return repository.findAll()
                .doOnComplete(() -> log.info("Retrieve all customers"));
    }

    @GetMapping("/customers-by-lastname/{name}")
    public Flux<Customer> getAllCustomersByLastName(@PathVariable(value = "name") String lastName) {
        return repository.findByLastNameOrderByFirstNameAsc(lastName)
                .doOnComplete(() -> log.info("Retrieve all customers with last name " + lastName));
    }

}
