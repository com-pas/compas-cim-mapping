// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.model;

public class CgmesTransformerEnd extends AbstractCgmesEntity {
    private String terminalId;

    public CgmesTransformerEnd(String id, String name, String terminalId) {
        super(id, name);
        this.terminalId = terminalId;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }
}
