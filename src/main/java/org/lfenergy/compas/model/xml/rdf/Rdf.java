// SPDX-FileCopyrightText: 2020 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.model.xml.rdf;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.lfenergy.compas.model.xml.XmlModel;
import org.lfenergy.compas.model.xml.cim.Substation;
import org.lfenergy.compas.model.xml.cim.VoltageLevel;
import org.lfenergy.compas.model.xml.md.FullModel;

/**
 * Rdf XML section
 * Containing all information in a Rdf section.
 */
@XmlRootElement(name = "RDF")
@XmlAccessorType(XmlAccessType.FIELD)
public class Rdf extends XmlModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @XmlElement(name = "FullModel", namespace = "http://iec.ch/TC57/61970-552/ModelDescription/1#")
    private FullModel fullModel;

    @XmlElement(name = "Substation", namespace = "http://iec.ch/TC57/2013/CIM-schema-cim16#")
    private Substation substation;

    @XmlElement(name = "VoltageLevel", namespace = "http://iec.ch/TC57/2013/CIM-schema-cim16#")
    private VoltageLevel voltageLevel;

    @Override
    public String toString() {
        return new StringBuilder("Rdf { ")
                    .append(fullModel.toString()).append(", ")
                    .append(substation.toString()).append(", ")
                    .append(voltageLevel.toString())
                    .append(" }")
                    .toString();
    }
}
