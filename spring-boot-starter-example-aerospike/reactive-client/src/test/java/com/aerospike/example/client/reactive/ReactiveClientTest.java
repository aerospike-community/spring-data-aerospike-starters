package com.aerospike.example.client.reactive;

import com.aerospike.client.reactor.AerospikeReactorClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ReactiveClientTest {

    @Autowired
    AerospikeReactorClient reactorClient;

    @Test
    void contextLoads() {
    }

    @Test
    void reactorClientExists() {
        assertThat(reactorClient.getReadPolicyDefault()).isNotNull();
    }
}
