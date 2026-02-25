// SPDX-FileCopyrightText: 2026 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.websocket.v1.decoder;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lfenergy.compas.core.commons.exception.CompasException;

import static org.junit.jupiter.api.Assertions.*;
import static org.lfenergy.compas.core.commons.exception.CompasErrorCode.WEBSOCKET_DECODER_ERROR_CODE;
import static org.lfenergy.compas.cim.mapping.CimMappingConstants.CIM_MAPPING_SERVICE_V1_NS_URI;

class CreateWsRequestDecoderTest {
    private CreateWsRequestDecoder decoder;

    @BeforeEach
    void init() {
        decoder = new CreateWsRequestDecoder();
        decoder.init(null);
    }


    @Test
    void willDecode_WhenCalledWithString_ThenTrueReturned() {
        assertTrue(decoder.willDecode(""));
        assertTrue(decoder.willDecode("Some text"));
    }


    @Test
    void willDecode_WhenCalledWithNull_ThenFalseReturned() {
        assertFalse(decoder.willDecode(null));
    }


    @Test
    void decode_WhenCalledWithCorrectRequestXML_ThenStringConvertedToObject() {
        var cimDataName = "TestConfiguration_BC_EQ_v3.0.0.xml";
        var cimRdfData = "<rdf:RDF><rdf:Description>Test RDF Content</rdf:Description></rdf:RDF>";
        var message = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<map:MapRequest xmlns:map=\"" + CIM_MAPPING_SERVICE_V1_NS_URI + "\">" +
                "  <map:CimData>" +
                "    <map:Name>" + cimDataName + "</map:Name>" +
                "    <map:RdfData><![CDATA[" + cimRdfData + "]]></map:RdfData>" +
                "  </map:CimData>" +
                "</map:MapRequest>";

        var result = decoder.decode(message);

        assertNotNull(result);
        assertNotNull(result.getCimData());
        assertEquals(1, result.getCimData().size());
        var cimData = result.getCimData().get(0);
        assertTrue(cimData.getName().contains(cimDataName));
        assertTrue(cimData.getRdfData().contains("Test RDF Content"));
    }


    @Test
    void decode_WhenCalledWithWrongXMLType_ThenExceptionThrown() {
        var message = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<map:InvalidRequest xmlns:map=\"" + CIM_MAPPING_SERVICE_V1_NS_URI + "\">"
                + "</map:InvalidRequest>";

        var exception = assertThrows(CompasException.class, () -> decoder.decode(message));
        assertEquals(WEBSOCKET_DECODER_ERROR_CODE, exception.getErrorCode());
        assertNotNull(exception.getCause());
    }

    @AfterEach
    void destroy() {
        decoder.destroy();
    }
}
