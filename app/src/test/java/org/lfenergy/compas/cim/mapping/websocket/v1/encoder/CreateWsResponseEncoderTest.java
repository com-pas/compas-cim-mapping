// SPDX-FileCopyrightText: 2026 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.websocket.v1.encoder;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.lfenergy.compas.cim.mapping.rest.v1.model.MapResponse;
import org.lfenergy.compas.scl2007b4.model.SCL;

class CreateWsResponseEncoderTest {
    private CreateWsResponseEncoder encoder;

    @BeforeEach
    void init() {
        encoder = new CreateWsResponseEncoder();
        encoder.init(null);
    }

    @Test
    void encode_WhenCalledWithRequest_ThenRequestConvertedToString() {
        var scl = new SCL();
        scl.setVersion("2007");
        var response = new MapResponse();
        response.setScl(scl);

        var result = encoder.encode(response);

        assertNotNull(result);
        assert result.contains("2007");
    }

    @AfterEach
    void destroy() {
        encoder.destroy();
    }
}
