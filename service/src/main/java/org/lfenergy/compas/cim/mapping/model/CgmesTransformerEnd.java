// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.model;

public class CgmesTransformerEnd extends AbstractCgmesEntity {
    private String terminalId;
    private String endNumber;

    public CgmesTransformerEnd(String id, String name, String terminalId, String endNumber) {
        super(id, name);
        this.terminalId = terminalId;
        this.endNumber = endNumber;
    }

    public String getUniqueName() {
        return getNameOrId() + "_" + getEndNumber();
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getEndNumber() {
        return endNumber;
    }

    public void setEndNumber(String endNumber) {
        this.endNumber = endNumber;
    }
}
