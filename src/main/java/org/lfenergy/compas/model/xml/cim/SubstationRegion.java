// SPDX-FileCopyrightText: 2020 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.model.xml.cim;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlRootElement;

import org.lfenergy.compas.model.xml.XmlModel;

/**
 * Substation.Region XML section
 * Containing all information in a Substation.Region section.
 */
@XmlRootElement(name = "Substation.Region")
@XmlAccessorType(XmlAccessType.FIELD)
public class SubstationRegion extends XmlModel implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @XmlAttribute(name = "resource", namespace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#")
    private String resource;

    @Override
    public String toString() {
        return new StringBuilder("Substation.Region { ")
                    .append("resource: ").append(resource)
                    .append(" }")
                    .toString();
    }
    
}
