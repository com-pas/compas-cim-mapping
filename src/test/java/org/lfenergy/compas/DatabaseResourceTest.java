// SPDX-FileCopyrightText: 2020 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas;

import org.junit.jupiter.api.Test;
import io.quarkus.test.junit.QuarkusTest;

import static io.restassured.RestAssured.given;

@QuarkusTest
class DatabaseResourceTest {

    @Test
    void testDatabaseApiEndpoint() {
        given()
            .when().get("/database")
            .then()
                .statusCode(200);
    }

}