package com.aerospike.example;

import com.aerospike.client.query.IndexCollectionType;
import com.aerospike.client.query.IndexType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.aerospike.annotation.Indexed;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    @Id
    private String id;
    private String firstName;
    @Indexed(type = IndexType.STRING, collectionType = IndexCollectionType.DEFAULT)
    private String lastName;
    private long age;
}

