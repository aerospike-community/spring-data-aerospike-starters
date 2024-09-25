package com.aerospike.example.reactive;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(
        classes = ReactiveSpringDataAerospikeExampleApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class ReactiveIntegrationTest {

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @AfterEach
    void tearDown() {
        RestAssured.given()
                .delete("/reactive/customers")
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Order(0)
    @Test
    void returnsEmptyWhenNoCustomers() {
        RestAssured.given()
                .get("/reactive/customers")
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
                .post("/reactive/customer")
                .then()
                .assertThat()
                .statusCode(200);

        RestAssured.given()
                .get("/reactive/customer/andrea")
                .then()
                .assertThat()
                .statusCode(200)
                .body("id", equalTo("andrea"))
                .body("age", equalTo(61));
    }

    @Order(2)
    @Test
    void savesAndFindsByLastName() {
        RestAssured.given()
                .body(new Customer("andrea", "Andrea", "Bocelli", 61))
                .contentType(ContentType.JSON)
                .post("/reactive/customer")
                .then()
                .assertThat()
                .statusCode(200);

        RestAssured.given()
                .get("/reactive/customers/search?lastName=Bocelli")
                .then()
                .assertThat()
                .statusCode(200)
                .body("[0].id", equalTo("andrea"))
                .body("[0].age", equalTo(61));
    }
}
