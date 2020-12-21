package com.aerospike.example.sync;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private long age;
}

