// SPDX-FileCopyrightText: 2020 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.model.xml.cim;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * VoltageLevel XML section
 * Containing all information in a VoltageLevel section.
 */
@XmlRootElement(name = "VoltageLevel")
@XmlAccessorType(XmlAccessType.FIELD)
public class VoltageLevel implements Serializable {
    private static final long serialVersionUID = 1L;

    @XmlAttribute(name = "ID", namespace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#")
    private String id;

    @XmlElement(name = "IdentifiedObject.name")
    private String name;

    @XmlElement(name = "VoltageLevel.Substation")
    private VoltageLevelSubstation substation;

    @XmlElement(name = "VoltageLevel.BaseVoltage")
    private VoltageLevelBaseVoltage baseVoltage;

    @Override
    public String toString() {
        return new StringBuilder("VoltageLevel { ")
                    .append("id: ").append(id).append(", ")
                    .append("name: ").append(name).append(", ")
                    .append("substation: ").append(substation).append(", ")
                    .append("baseVoltage: ").append(baseVoltage)
                    .append(" }")
                    .toString();
    }
    
}
