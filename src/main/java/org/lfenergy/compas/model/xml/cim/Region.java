// SPDX-FileCopyrightText: 2020 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.model.xml.cim;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlRootElement;

import org.lfenergy.compas.model.xml.rdf.RdfSection;

/**
 * Substation.Region XML section
 * Containing all information in a Substation.Region section.
 */
@XmlRootElement(name = "Region")
@XmlAccessorType(XmlAccessType.FIELD)
public class Region extends RdfSection implements Serializable {
    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        return new StringBuilder("Substation.Region { ")
                    .append("resource: ").append(getResource())
                    .append(" }")
                    .toString();
    }
    
}
