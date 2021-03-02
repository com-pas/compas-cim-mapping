// SPDX-FileCopyrightText: 2020 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.model.xml.cim;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * VoltageLevel.Substation XML section
 * Containing all information in a VoltageLevel.Substation section.
 */
@XmlRootElement(name = "VoltageLevel.Substation")
@XmlAccessorType(XmlAccessType.FIELD)
public class VoltageLevelSubstation implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @XmlAttribute(name = "resource", namespace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#")
    private String resource;

    @Override
    public String toString() {
        return new StringBuilder("VoltageLevel.Substation { ")
                    .append("resource: ").append(resource)
                    .append(" }")
                    .toString();
    }
    
}
