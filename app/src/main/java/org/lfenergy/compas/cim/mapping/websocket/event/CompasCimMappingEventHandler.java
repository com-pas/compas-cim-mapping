// SPDX-FileCopyrightText: 2026 Alliander N.V.
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.websocket.event;

import io.quarkus.vertx.ConsumeEvent;
import org.lfenergy.compas.cim.mapping.rest.v1.model.MapResponse;
import org.lfenergy.compas.cim.mapping.service.CompasCimMappingService;
import org.lfenergy.compas.cim.mapping.websocket.v1.event.model.MapEventRequest;
import org.lfenergy.compas.core.websocket.WebsocketHandler;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class CompasCimMappingEventHandler {
    private final CompasCimMappingService compasCimMappingService;

    @Inject
    public CompasCimMappingEventHandler(CompasCimMappingService compasCimMappingService) {
        this.compasCimMappingService = compasCimMappingService;
    }

    @ConsumeEvent(value = "map-ws", blocking = true)
    public void mapWebsocketsEvent(MapEventRequest request) {
        new WebsocketHandler<MapResponse>().execute(request.getSession(), () -> {
            var response = new MapResponse();
            response.setScl(compasCimMappingService.map(request.getRequest().getCimData(), request.getWho()));
            return response;
        });
    }
}
