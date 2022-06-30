// SPDX-FileCopyrightText: 2022 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.model;

public interface CgmesCommonEntity {
    String id();

    String name();

    default String getNameOrId() {
        return name() != null ? name() : id();
    }
}
