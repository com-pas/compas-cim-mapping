// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.model;

import java.util.Optional;

public class CgmesConnectivityNode {
    private String id;
    private String name;

    public CgmesConnectivityNode(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Optional<String> getOptionalName() {
        return Optional.ofNullable(name);
    }

    public String getNameOrId() {
        return getOptionalName().orElseGet(this::getId);
    }
}
