package com.aerospike.example.client.sync;

import com.aerospike.client.AerospikeClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class SyncClientTest {

    @Autowired
    AerospikeClient client;

    @Test
    void contextLoads() {
    }

    @Test
    void syncClientExists() {
        assertThat(client.getReadPolicyDefault()).isNotNull();
    }
}
