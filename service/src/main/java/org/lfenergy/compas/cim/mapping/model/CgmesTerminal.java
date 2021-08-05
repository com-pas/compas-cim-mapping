// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.model;

public class CgmesTerminal extends AbstractCgmesEntity {
    private String connectivityNodeId;

    public CgmesTerminal(String id, String name, String connectivityNodeId) {
        super(id, name);

        this.connectivityNodeId = connectivityNodeId;
    }

    public String getConnectivityNodeId() {
        return connectivityNodeId;
    }

    public void setConnectivityNodeId(String connectivityNodeId) {
        this.connectivityNodeId = connectivityNodeId;
    }
}
