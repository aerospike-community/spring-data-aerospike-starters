package com.aerospike.example.client;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ClientTest {

    @Autowired
    AerospikeClient client;

    @Value("${embedded.aerospike.namespace}")
    String namespace;

    @Test
    void contextLoads() {
    }

    @Test
    void clientExists() {
        assertThat(client.getReadPolicyDefault()).isNotNull();
    }

    @Test
    void saveAndRead() {
        Key key = new Key(namespace, "customers", "key10");
        Bin bin = new Bin("TestBin1", "Test10");

        client.put(client.getWritePolicyDefault(), key, bin);
        assertThat(client.get(null, key).bins.get("TestBin1")).isEqualTo("Test10");
    }
}
