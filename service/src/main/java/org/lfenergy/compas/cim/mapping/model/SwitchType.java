// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.model;

import org.lfenergy.compas.cim.mapping.exception.CompasCimMappingException;

import java.util.Arrays;
import java.util.List;

import static org.lfenergy.compas.cim.mapping.exception.CompasCimMappingErrorCode.UNKNOWN_TYPE_ERROR_CODE;

/**
 * Mapping between CIM Switch Types and IEC Conducting Equipment Type.
 * <p>
 * Remark: Some CIM Switch Type are coupled to more than 1 IEC Code, that's why some are commented.
 */
public enum SwitchType {
    BSH("Connector"),
    // CAB("ACLineSegment", "DCLineSegment"),
    CAP("ShuntCompensator", "SeriesCompensator"),
    CBR("ProtectedSwitch", "Breaker", "Recloser"),
    CON("FrequencyConverter"),
    CTR("CurrentTransformer"),
    DIS("Switch", "Disconnector", "Fuse", "Jumper", "LoadBreakSwitch", "GroundDisconnector", "Sectionaliser"),
    EFN("PetersenCoil"),
    GEN("GeneratingUnit"),
    // GIL("ACLineSegment", "DCLineSegment"),
    IFL("ACLineSegment", "DCLineSegment", "EquivalentBranch"),
    // LIN("ACLineSegment", "DCLineSegment"),
    LTC("TapChanger", "RatioTapChanger", "PhaseTapChanger"),
    MOT("AsynchronousMachine"),
    PSH("GroundingImpedance"),
    PTR("PowerTransformer"),
    PTW("TransformerEnd", "PowerTransformerEnd", "TransformerTankEnd"),
    // REA("ShuntCompensator", "SeriesCompensator"),
    RES("EarthFaultCompensator"),
    // RRC("SynchronousMachine"),
    SAR("SurgeArrester"),
    SCR("ACDCConverter"),
    SMC("SynchronousMachine"),
    // TCF("FrequencyConverter"),
    TCR("StaticVarCompensator"),
    TNK("TransformerTank"),
    VTR("PotentialTransformer");

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
