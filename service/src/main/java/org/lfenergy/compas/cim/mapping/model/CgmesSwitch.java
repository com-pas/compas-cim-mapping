// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.model;

public class CgmesSwitch extends AbstractCgmesEntity {
    private String type;
    private String terminal1Id;
    private String terminal2Id;

    public CgmesSwitch(String id, String name, String type, String terminal1Id, String terminal2Id) {
        super(id, name);

        this.type = type;
        this.terminal1Id = terminal1Id;
        this.terminal2Id = terminal2Id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTerminal1Id() {
        return terminal1Id;
    }

    public void setTerminal1Id(String terminal1Id) {
        this.terminal1Id = terminal1Id;
    }

    public String getTerminal2Id() {
        return terminal2Id;
    }

    public void setTerminal2Id(String terminal2Id) {
        this.terminal2Id = terminal2Id;
    }
}
