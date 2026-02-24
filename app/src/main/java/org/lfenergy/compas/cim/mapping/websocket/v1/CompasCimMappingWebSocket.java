// SPDX-FileCopyrightText: 2026 Alliander N.V.
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.websocket.v1;

import io.quarkus.security.Authenticated;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.jwt.JsonWebToken;
import io.vertx.mutiny.core.eventbus.EventBus;
import org.lfenergy.compas.cim.mapping.rest.UserInfoProperties;
import org.lfenergy.compas.cim.mapping.rest.v1.model.MapRequest;
import org.lfenergy.compas.cim.mapping.websocket.v1.event.model.MapEventRequest;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import static org.lfenergy.compas.core.websocket.WebsocketSupport.handleException;

@Authenticated
@ApplicationScoped
@ServerEndpoint(value = "/cim-ws/v1/map",
    decoders = {org.lfenergy.compas.cim.mapping.websocket.v1.decoder.CreateWsRequestDecoder.class},
    encoders = {org.lfenergy.compas.cim.mapping.websocket.v1.encoder.CreateWsResponseEncoder.class})
public class CompasCimMappingWebSocket {
    private static final Logger LOGGER = LogManager.getLogger(CompasCimMappingWebSocket.class);

    private final EventBus eventBus;
    private final JsonWebToken jsonWebToken;
    private final UserInfoProperties userInfoProperties;

    @Inject
    public CompasCimMappingWebSocket(EventBus eventBus,
                                     JsonWebToken jsonWebToken,
                                     UserInfoProperties userInfoProperties) {
        this.eventBus = eventBus;
        this.jsonWebToken = jsonWebToken;
        this.userInfoProperties = userInfoProperties;
    }

    @OnOpen
    public void onOpen(Session session) {
        LOGGER.info("WebSocket opened: {}", session.getId());
    }

    @OnMessage
    public void onMapMessage(Session session, MapRequest request) {
        LOGGER.info("Received WebSocket message (map) from session {}.", session.getId());

        String who = jsonWebToken.getClaim(userInfoProperties.who());
        LOGGER.trace("Username used for Who {}", who);

        eventBus.send("map-ws", new MapEventRequest(session, request, who));
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        LOGGER.warn("WebSocket error in session {}", session.getId(), throwable);
        handleException(session, throwable);
    }
    
    @OnClose
    public void onClose(Session session) {
        LOGGER.debug("WebSocket closed: {}", session.getId());
    }
}