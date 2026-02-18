// SPDX-FileCopyrightText: 2026 Alliander N.V.
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.websocket.v1;

import io.quarkus.security.Authenticated;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.lfenergy.compas.cim.mapping.rest.UserInfoProperties;
import org.lfenergy.compas.cim.mapping.rest.v1.model.MapRequest;
import org.lfenergy.compas.cim.mapping.rest.v1.model.MapResponse;
import org.lfenergy.compas.cim.mapping.service.CompasCimMappingService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@Authenticated
@ApplicationScoped
@ServerEndpoint("/cim-ws/v1/map")
public class CompasCimMappingWebSocket {
    private static final Logger LOGGER = LogManager.getLogger(CompasCimMappingWebSocket.class);

    @Inject
    CompasCimMappingService compasCimMappingService;

    @Inject
    JsonWebToken jsonWebToken;

    @Inject
    UserInfoProperties userInfoProperties;

    @OnOpen
    public void onOpen(Session session) {
        LOGGER.info("WebSocket opened: {}", session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        LOGGER.info("Received WebSocket message: {}", message);
        try {
            MapRequest request = MapRequest.fromXml(message);
            String who = jsonWebToken.getClaim(userInfoProperties.who());
            MapResponse response = new MapResponse();
            response.setScl(compasCimMappingService.map(request.getCimData(), who));
            String xmlResponse = response.toXml();
            session.getAsyncRemote().sendText(xmlResponse);
        } catch (Exception e) {
            LOGGER.error("Error processing WebSocket message", e);
            session.getAsyncRemote().sendText("<error>" + e.getMessage() + "</error>");
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        LOGGER.error("WebSocket error in session {}", session != null ? session.getId() : "unknown", throwable);
        if (session != null && session.isOpen()) {
            String errorMsg = throwable.getMessage() != null ? throwable.getMessage() : "Unknown error";
            session.getAsyncRemote().sendText("<error>" + errorMsg + "</error>");
        }
    }
    
    @OnClose
    public void onClose(Session session) {
        LOGGER.info("WebSocket closed: {}", session.getId());
    }
}
