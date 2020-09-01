package org.springframework.boot.autoconfigure.data.aerospike.example.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.data.aerospike.example.ReactiveSpringDataAerospikeExampleApplication;
import org.springframework.boot.autoconfigure.data.aerospike.example.model.Customer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest(
        classes = ReactiveSpringDataAerospikeExampleApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public abstract class BaseIntegrationTest {

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @AfterEach
    void tearDown() {
        RestAssured.given()
                .delete("/customers")
                .then()
                .assertThat()
                .statusCode(200);
    }

    protected abstract String urlPath();

    @Test
    void returnsEmptyWhenNoCustomers() {
        RestAssured.given()
                .get(urlPath() + "/customers")
                .then()
                .assertThat()
                .statusCode(200)
                .body("", hasSize(0));
    }

    @Test
    void savesAndGets() {
        RestAssured.given()
                .body(new Customer("andrea", "Andrea", "Bocelli", 61))
                .contentType(ContentType.JSON)
                .post(urlPath() + "/customer")
                .then()
                .assertThat()
                .statusCode(200);

        RestAssured.given()
                .get(urlPath() + "/customer/andrea")
                .then()
                .assertThat()
                .statusCode(200)
                .body("id", equalTo("andrea"))
                .body("age", equalTo(61))
        ;
    }

    @Test
    void savesAndFindsByLastName() {
        RestAssured.given()
                .body(new Customer("andrea", "Andrea", "Bocelli", 61))
                .contentType(ContentType.JSON)
                .post(urlPath() + "/customer")
                .then()
                .assertThat()
                .statusCode(200);

        RestAssured.given()
                .get(urlPath() + "/customers/search?lastName=Bocelli")
                .then()
                .assertThat()
                .statusCode(200)
                .body("[0].id", equalTo("andrea"))
                .body("[0].age", equalTo(61))
        ;
    }

}