package com.aerospike.example.client.reactive;

import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.reactor.AerospikeReactorClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ReactiveClientTest {

    @Autowired
    AerospikeReactorClient reactorClient;

    @Value("${embedded.aerospike.namespace}")
    String namespace;

    @Test
    void contextLoads() {
    }

    @Test
    void reactorClientExists() {
        assertThat(reactorClient.getReadPolicyDefault()).isNotNull();
    }

    @Test
    void writeAndRead() {
        Key key = new Key(namespace, "testSet", "key1");
        Bin bin = new Bin("TestBin1", "Test1");

        reactorClient.put(reactorClient.getWritePolicyDefault(), key, bin).block();
        assertThat(Objects.requireNonNull(reactorClient.get(key).block()).record.bins.get("TestBin1"))
                .isEqualTo("Test1");
    }
}
