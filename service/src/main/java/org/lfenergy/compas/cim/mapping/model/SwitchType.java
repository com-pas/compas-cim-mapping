// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.model;

import org.lfenergy.compas.cim.mapping.exception.CompasCimMappingException;

import java.util.Arrays;
import java.util.List;

import static org.lfenergy.compas.cim.mapping.exception.CompasCimMappingErrorCode.UNKNOWN_TYPE_ERROR_CODE;

public enum SwitchType {
    BSH("Connector"),
    CAB("ACLineSegment", "DCLineSegment"),
    CBR("ProtectedSwitch", "Breaker", "Recloser"),
    DIS("Switch", "Disconnector", "Fuse", "Jumper", "LoadBreakSwitch", "GroundDisconnector", "Sectionaliser");

    private List<String> cimTypes;

    SwitchType(String... cimTypes) {
        this.cimTypes = List.of(cimTypes);
    }

    public List<String> getCimTypes() {
        return cimTypes;
    }

    public static SwitchType convertSwitchType(String cimType) {
        return Arrays.stream(values())
                .filter(st -> st.getCimTypes().contains(cimType))
                .findFirst()
                .orElseThrow(() -> new CompasCimMappingException(UNKNOWN_TYPE_ERROR_CODE,
                        "The CIM Switch Type '" + cimType + "' is unknown"));
    }
}
