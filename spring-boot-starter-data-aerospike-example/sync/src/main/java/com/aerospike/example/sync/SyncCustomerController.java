package com.aerospike.example.sync;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RequestMapping("/sync")
@RestController
public class SyncCustomerController {

    @Autowired
    private SyncCustomerRepository repository;

    @PostMapping("/customer")
    public Customer createCustomer(@Valid @RequestBody Customer customer) {
        Customer saved = repository.save(customer);
        log.info("Created {}", saved);
        return saved;
    }

    @GetMapping("/customer/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable(value = "id") String customerId) {
        return repository.findById(customerId)
                .map(body -> {
                    log.info("Retrieved " + body);
                    return ResponseEntity.ok(body);
                })
                .orElse(ResponseEntity.noContent().build());
    }

    @PutMapping("/customer/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable(value = "id") String customerId,
                                                   @Valid @RequestBody Customer customer) {
        return repository.findById(customerId)
                .map(existing -> {
                    existing.setFirstName(customer.getFirstName());
                    existing.setLastName(customer.getLastName());
                    existing.setAge(customer.getAge());
                    Customer result = repository.save(existing);
                    log.info("Updated " + result);
                    return result;
                })
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @DeleteMapping("/customer/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable(value = "id") String customerId) {
        try {
            repository.deleteById(customerId);
            return ResponseEntity.ok().build();
        } catch (DataRetrievalFailureException exception) {
            return ResponseEntity.noContent().build();
        }
    }

    @DeleteMapping("/customers")
    public void deleteCustomers() {
        repository.deleteAll();
        log.info("Deleted all customers");
    }

    @GetMapping("/customers")
    public Iterable<Customer> getAllCustomers() {
        Iterable<Customer> all = repository.findAll();
        log.info("Retrieved all customers");
        return all;
    }

    @GetMapping("/customers/search")
    public List<Customer> getAllCustomersByLastName(@RequestParam(value = "lastName") String lastName) {
        List<Customer> result = repository.findByLastNameOrderByFirstNameAsc(lastName);
        log.info("Retrieved all customers with last name " + lastName);
        return result;
    }

}
