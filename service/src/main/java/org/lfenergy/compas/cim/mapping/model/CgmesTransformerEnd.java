// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.model;

public record CgmesTransformerEnd(String id, String name, String terminalId,
                                  String endNumber) implements CgmesCommonEntity {
    public String getUniqueName() {
        return getNameOrId() + "_" + endNumber();
    }
}
