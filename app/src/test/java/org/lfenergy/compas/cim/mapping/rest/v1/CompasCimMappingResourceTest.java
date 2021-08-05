// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.rest.v1;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.lfenergy.compas.cim.mapping.model.CimData;
import org.lfenergy.compas.cim.mapping.rest.v1.model.MapRequest;
import org.lfenergy.compas.cim.mapping.service.CompasCimMappingService;
import org.lfenergy.compas.scl2007b4.model.SCL;

import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.path.xml.config.XmlPathConfig.xmlPathConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.lfenergy.compas.cim.mapping.CimMappingConstants.CIM_MAPPING_SERVICE_V1_NS_URI;
import static org.lfenergy.compas.cim.mapping.CimMappingConstants.SCL_NS_URI;
import static org.mockito.Mockito.*;

@QuarkusTest
@TestHTTPEndpoint(CompasCimMappingResource.class)
class CompasCimMappingResourceTest {
    @InjectMock
    private CompasCimMappingService compasCimMappingService;

    @Test
    void mapCimToScl_WhenCalled_ThenCorrectMessageIsRetrieved() {
        var request = new MapRequest();
        request.setCimData(List.of(new CimData()));

        var scl = new SCL();
        scl.setVersion("2007");
        when(compasCimMappingService.map(any())).thenReturn(scl);

        var response = given()
                .contentType(ContentType.XML)
                .body(request)
                .when()
                .post("/map")
                .then()
                .statusCode(200)
                .extract()
                .response();

        var xmlPath = response.xmlPath()
                .using(xmlPathConfig().declaredNamespace("scl", SCL_NS_URI)
                        .declaredNamespace("cms", CIM_MAPPING_SERVICE_V1_NS_URI));
        var sclVersion = xmlPath.getString("cms:MapResponse.scl:SCL.@version");
        assertNotNull(sclVersion);
        assertEquals("2007", sclVersion);
        verify(compasCimMappingService, times(1)).map(any());
    }
}
