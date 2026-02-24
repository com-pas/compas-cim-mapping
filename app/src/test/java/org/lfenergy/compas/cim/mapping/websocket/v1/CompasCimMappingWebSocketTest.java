// SPDX-FileCopyrightText: 2026 Alliander N.V.
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.websocket.v1;

import io.vertx.mutiny.core.eventbus.EventBus;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lfenergy.compas.cim.mapping.rest.UserInfoProperties;
import org.lfenergy.compas.cim.mapping.rest.v1.model.MapRequest;
import org.lfenergy.compas.cim.mapping.websocket.v1.event.model.MapEventRequest;

import javax.websocket.Session;

import static org.mockito.Mockito.*;

class CompasCimMappingWebSocketTest {
    private EventBus eventBus;
    private JsonWebToken jsonWebToken;
    private UserInfoProperties userInfoProperties;
    private CompasCimMappingWebSocket webSocket;

    @BeforeEach
    void setUp() {
        eventBus = mock(EventBus.class);
        jsonWebToken = mock(JsonWebToken.class);
        userInfoProperties = mock(UserInfoProperties.class);
        webSocket = new CompasCimMappingWebSocket(eventBus, jsonWebToken, userInfoProperties);
    }

    @Test
    void onOpen_ShouldLogInfo() {
        Session session = mock(Session.class);
        when(session.getId()).thenReturn("session-id");
        webSocket.onOpen(session);
    }

    @Test
    void onMapMessage_ShouldSendEvent() {
        Session session = mock(Session.class);
        when(session.getId()).thenReturn("session-id");
        MapRequest request = mock(MapRequest.class);
        when(jsonWebToken.getClaim(anyString())).thenReturn("test-user");
        when(userInfoProperties.who()).thenReturn("who");

        webSocket.onMapMessage(session, request);
        verify(eventBus).send(eq("map-ws"), any(MapEventRequest.class));
    }

    @Test
    void onError_ShouldHandleException() {
        Session session = mock(Session.class);
        Throwable throwable = new RuntimeException("error");
        when(session.getId()).thenReturn("session-id");
        javax.websocket.RemoteEndpoint.Async async = mock(javax.websocket.RemoteEndpoint.Async.class);
        when(session.getAsyncRemote()).thenReturn(async);
        webSocket.onError(session, throwable);
    }

    @Test
    void onClose_ShouldLogDebug() {
        Session session = mock(Session.class);
        when(session.getId()).thenReturn("session-id");
        webSocket.onClose(session);
    }
}
