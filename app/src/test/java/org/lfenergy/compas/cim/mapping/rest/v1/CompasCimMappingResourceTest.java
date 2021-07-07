// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.rest.v1;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.lfenergy.compas.cim.mapping.rest.model.GetRequest;
import org.lfenergy.compas.cim.mapping.service.CompasCimMappingService;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@QuarkusTest
@TestHTTPEndpoint(CompasCimMappingResource.class)
class CompasCimMappingResourceTest {
    @InjectMock
    private CompasCimMappingService compasCimMappingService;

    @Test
    void getMessage_WhenCalled_ThenCorrectMessageIsRetrieved() {
        var name = "Jan";
        var request = new GetRequest();
        request.setName(name);

        when(compasCimMappingService.getMessage(name)).thenReturn("Hello " + name);

        var response = given()
                .contentType(ContentType.XML)
                .body(request)
                .when()
                .post("/message")
                .then()
                .statusCode(200)
                .extract()
                .response();

        var xmlPath = response.xmlPath();
        assertEquals("Hello Jan", xmlPath.get("GetResponse.Message"));
        verify(compasCimMappingService, times(1)).getMessage(name);
    }
}
