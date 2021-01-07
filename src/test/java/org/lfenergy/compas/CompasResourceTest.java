// SPDX-FileCopyrightText: 2020 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class CompasResourceTest {

    @Test
    public void testCompasEndpoint() {
        given()
          .when().get("/compas")
          .then()
             .statusCode(200);
    }

}