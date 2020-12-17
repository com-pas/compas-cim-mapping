package org.lfenergy.compas;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
class CompasResourceTest {

    @Test
    void testApiEndpoint() {
        given()
          .when().get("/api")
          .then()
             .statusCode(200);
    }

}