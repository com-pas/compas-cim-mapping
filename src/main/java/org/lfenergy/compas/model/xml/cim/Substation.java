// SPDX-FileCopyrightText: 2020 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.model.xml.cim;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlRootElement;

import org.lfenergy.compas.model.xml.rdf.RdfSection;

/**
 * Substation XML section
 * Containing all information in a Substation section.
 */
@XmlRootElement(name = "Substation")
@XmlAccessorType(XmlAccessType.FIELD)
public class Substation extends RdfSection implements Serializable {
    private static final long serialVersionUID = 1L;

    @XmlElement(name = "IdentifiedObject.name")
    private String name;

    @XmlElement(name = "Substation.Region")
    private Region region;

    @Override
    public String toString() {
        return new StringBuilder("Substation { ")
                    .append("id: ").append(getId()).append(", ")
                    .append("name: ").append(name).append(", ")
                    .append("region: ").append(region)
                    .append(" }")
                    .toString();
    }
    
}
