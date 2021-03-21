// SPDX-FileCopyrightText: 2020 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.resource;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import io.quarkus.test.junit.QuarkusTest;

import static io.restassured.RestAssured.given;

import java.io.File;

@QuarkusTest
class MappingResourceTest {

    private final String testXml = "<test>test</test>";

    @Test
    void testCim61850MappingApiEndpoint() {
        given()
            .with()
            .contentType("application/xml")
            .body(testXml)
            .when()
            .post("/mapping/cim2iec61850")
            .then()
                .statusCode(200);
    }

    @AfterAll
    private static void afterAll() {
        // Cleanup of files
        new File("output.ssd").delete();
    }

}