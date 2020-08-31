package org.springframework.boot.autoconfigure.data.aerospike.example.controller;

public class ReactiveIntegrationTest extends BaseIntegrationTest {

    @Override
    protected String urlPath() {
        return "/reactive";
    }
}
