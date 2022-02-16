// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.model;

public class CgmesSwitch extends AbstractCgmesEntity {
    private String type;

    public CgmesSwitch(String id, String name, String type) {
        super(id, name);

        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
