package com.aerospike.example.sync;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(
        classes = SyncSpringDataAerospikeExampleApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class SyncIntegrationTest {

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @AfterEach
    void tearDown() {
        RestAssured.given()
                .delete("/sync/customers")
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Order(0)
    @Test
    void returnsEmptyWhenNoCustomers() {
        RestAssured.given()
                .get("/sync/customers")
                .then()
                .assertThat()
                .statusCode(200)
                .body("", hasSize(0));
    }

    @Order(1)
    @Test
    void savesAndGets() {
        RestAssured.given()
                .body(new Customer("andrea", "Andrea", "Bocelli", 61))
                .contentType(ContentType.JSON)
                .post("/sync/customer")
                .then()
                .assertThat()
                .statusCode(200);

        RestAssured.given()
                .get("/sync/customer/andrea")
                .then()
                .assertThat()
                .statusCode(200)
                .body("id", equalTo("andrea"))
                .body("age", equalTo(61))
        ;
    }

    @Order(2)
    @Test
    void savesAndFindsByLastName() {
        RestAssured.given()
                .body(new Customer("andrea", "Andrea", "Bocelli", 61))
                .contentType(ContentType.JSON)
                .post("/sync/customer")
                .then()
                .assertThat()
                .statusCode(200);

        RestAssured.given()
                .get("/sync/customers/search?lastName=Bocelli")
                .then()
                .assertThat()
                .statusCode(200)
                .body("[0].id", equalTo("andrea"))
                .body("[0].age", equalTo(61))
        ;
    }

}