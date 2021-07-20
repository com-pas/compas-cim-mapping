// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.rest;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
class HealthCheckTest {
    @Test
    void testLivenessEndpoint() {
        given()
                .when().get("/q/health/live")
                .then()
                .statusCode(200);
    }

    @Test
    void testReadinessEndpoint() {
        given()
                .when().get("/q/health/ready")
                .then()
                .statusCode(200);
    }
}