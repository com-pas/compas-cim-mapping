// SPDX-FileCopyrightText: 2026 Alliander N.V.
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.websocket.v1.event.model;

import org.lfenergy.compas.cim.mapping.rest.v1.model.MapRequest;

import javax.websocket.Session;
import java.io.Serializable;

public class MapEventRequest implements Serializable {
    private final Session session;
    private final MapRequest request;
    private final String who;

    public MapEventRequest(Session session, MapRequest request, String who) {
        this.session = session;
        this.request = request;
        this.who = who;
    }

    public Session getSession() {
        return session;
    }

    public MapRequest getRequest() {
        return request;
    }

    public String getWho() {
        return who;
    }
}
