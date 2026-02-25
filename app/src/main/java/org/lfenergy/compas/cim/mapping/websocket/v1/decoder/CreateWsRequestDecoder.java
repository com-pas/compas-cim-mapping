// SPDX-FileCopyrightText: 2026 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.websocket.v1.decoder;

import org.lfenergy.compas.core.websocket.AbstractDecoder;
import org.lfenergy.compas.core.websocket.WebsocketSupport;
import org.lfenergy.compas.cim.mapping.rest.v1.model.MapRequest;

public class CreateWsRequestDecoder extends AbstractDecoder<MapRequest> {
    @Override
    public boolean willDecode(String message) {
        return (message != null);
    }

    @Override
    public MapRequest decode(String message) {
        return WebsocketSupport.decode(message, MapRequest.class);
    }
}