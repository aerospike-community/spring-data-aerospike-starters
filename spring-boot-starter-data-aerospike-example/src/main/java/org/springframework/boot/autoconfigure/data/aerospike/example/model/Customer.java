package org.springframework.boot.autoconfigure.data.aerospike.example.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
public class Customer {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private long age;
}
