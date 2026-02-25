
// SPDX-FileCopyrightText: 2022 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.websocket.v1.encoder;

import org.lfenergy.compas.core.websocket.AbstractEncoder;
import org.lfenergy.compas.core.websocket.WebsocketSupport;
import org.lfenergy.compas.cim.mapping.rest.v1.model.MapResponse;

public class CreateWsResponseEncoder extends AbstractEncoder<MapResponse> {
    @Override
    public String encode(MapResponse jaxbObject) {
        return WebsocketSupport.encode(jaxbObject, MapResponse.class);
    }
}
