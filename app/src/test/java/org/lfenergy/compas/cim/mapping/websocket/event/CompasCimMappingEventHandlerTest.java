// SPDX-FileCopyrightText: 2026 Alliander N.V.
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.websocket.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.compas.cim.mapping.rest.v1.model.MapRequest;
import org.lfenergy.compas.cim.mapping.rest.v1.model.MapResponse;
import org.lfenergy.compas.cim.mapping.service.CompasCimMappingService;
import org.lfenergy.compas.cim.mapping.websocket.v1.event.model.MapEventRequest;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompasCimMappingEventHandlerTest {
    @Mock
    private CompasCimMappingService service;

    @InjectMocks
    private CompasCimMappingEventHandler eventHandler;

    @Test
    void mapWebsocketsEvent_WhenCalled_ThenMapResponseReturned() {
        var who = "Who executed it";
        var mapRequest = new MapRequest();
        var scl = mock(org.lfenergy.compas.scl2007b4.model.SCL.class);

        var session = mockSession();
        var request = new MapEventRequest(session, mapRequest, who);
        when(service.map(mapRequest.getCimData(), who)).thenReturn(scl);

        eventHandler.mapWebsocketsEvent(request);

        var response = verifyResponse(session, MapResponse.class);
        assertEquals(scl, ((MapResponse) response).getScl());
        verify(service).map(mapRequest.getCimData(), who);
    }

    private Session mockSession() {
        var session = mock(Session.class);
        var async = mock(RemoteEndpoint.Async.class);
        when(session.getAsyncRemote()).thenReturn(async);
        return session;
    }

    private <T> T verifyResponse(Session session, Class<T> responseClass) {
        verify(session).getAsyncRemote();
        ArgumentCaptor<T> captor = ArgumentCaptor.forClass(responseClass);
        verify(session.getAsyncRemote()).sendObject(captor.capture());
        return captor.getValue();
    }
}
