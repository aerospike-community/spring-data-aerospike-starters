package com.aerospike.example.reactive;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RequestMapping("/reactive")
@RestController
public class ReactiveCustomerController {

    @Autowired
    private ReactiveCustomerRepository repository;

    @PostMapping("/customer")
    public Mono<Customer> createCustomer(@Valid @RequestBody Customer customer) {
        return repository.save(customer)
                .doOnSuccess(result -> log.info("Created {}", result));
    }

    @GetMapping("/customer/{id}")
    public Mono<ResponseEntity<Customer>> getCustomerById(@PathVariable(value = "id") String customerId) {
        return repository.findById(customerId)
                .doOnSuccess(result -> log.info("Retrieved {}", result))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.noContent().build());
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
                            .doOnSuccess(result -> log.info("Updated {}", result));
                })
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.noContent().build());
    }

    @DeleteMapping("/customer/{id}")
    public Mono<ResponseEntity<Void>> deleteCustomer(@PathVariable(value = "id") String customerId) {
        return repository.findById(customerId)
                .flatMap(existing ->
                        repository.delete(existing)
                                .then(Mono.just(new ResponseEntity<Void>(OK)))
                                .doOnSuccess(result -> log.info("Deleted {}", existing))
                )
                .defaultIfEmpty(ResponseEntity.noContent().build());
    }

    @GetMapping("/customers")
    public Flux<Customer> getAllCustomers() {
        return repository.findAll()
                .doOnComplete(() -> log.info("Retrieved all customers"));
    }

    @GetMapping("/customers/search")
    public Flux<Customer> getAllCustomersByLastName(@RequestParam(value = "lastName") String lastName) {
        return repository.findByLastNameOrderByFirstNameAsc(lastName)
                .doOnComplete(() -> log.info("Retrieved all customers with last name {}", lastName));
    }

    @DeleteMapping("/customers")
    @ResponseStatus(HttpStatus.OK)
    public Flux<Void> deleteCustomers() {
        return repository.findAll()
                .flatMap(customer -> repository.deleteById(customer.getId()))
                .doOnComplete(() -> log.info("Deleted all customers"));
    }
}
