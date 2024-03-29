// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.rest.v1;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.quarkus.test.security.TestSecurity;
import io.quarkus.test.security.jwt.Claim;
import io.quarkus.test.security.jwt.JwtSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.lfenergy.compas.cim.mapping.model.CimData;
import org.lfenergy.compas.cim.mapping.rest.v1.model.MapRequest;
import org.lfenergy.compas.cim.mapping.service.CompasCimMappingService;
import org.lfenergy.compas.scl2007b4.model.SCL;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.path.xml.config.XmlPathConfig.xmlPathConfig;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.lfenergy.compas.cim.mapping.CimMappingConstants.CIM_MAPPING_SERVICE_V1_NS_URI;
import static org.lfenergy.compas.cim.mapping.CimMappingConstants.SCL_NS_URI;
import static org.mockito.Mockito.*;

@QuarkusTest
@TestHTTPEndpoint(CompasCimMappingResource.class)
@TestSecurity(user = "test-mapper")
@JwtSecurity(claims = {
        // Default the claim "name" is configured for Who, so we will set this claim for the test.
        @Claim(key = "name", value = "Test User")
})
class CompasCimMappingResourceTest {
    @InjectMock
    private CompasCimMappingService compasCimMappingService;

    @Test
    void mapCimToScl_WhenCalled_ThenCorrectMessageIsRetrieved() throws IOException {
        var cimDate = new CimData();
        cimDate.setName("MiniGridTestConfiguration_BC_EQ_v3.0.0.xml");
        cimDate.setRdfData(readFile());
        var request = new MapRequest();
        request.setCimData(List.of(cimDate));

        var scl = new SCL();
        scl.setVersion("2007");
        when(compasCimMappingService.map(any(), eq("Test User"))).thenReturn(scl);

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
        verify(compasCimMappingService, times(1)).map(any(), eq("Test User"));
    }

    private String readFile() throws IOException {
        var resource = requireNonNull(getClass().getResource("/minigrid/MiniGridTestConfiguration_BC_EQ_v3.0.0.xml"));
        var path = Paths.get(resource.getPath());
        return String.join("\n", Files.readAllLines(path)).trim().replaceFirst("^([\\W]+)<", "<");
    }
}
