// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.*;

import static org.lfenergy.compas.cim.mapping.Constants.CIM_MAPPING_SERVICE_NS_URI;

/**
 * Simple Pojo Class to hold a source entry of Cim XML. PowSyBl needs to have a (file)name and of
 * course the RDF XML (as Element).
 */
@XmlType(name = "CimDataType", namespace = CIM_MAPPING_SERVICE_NS_URI)
@XmlAccessorType(XmlAccessType.FIELD)
public class CimData {
    @Schema(example = "CONFIG_BC_EQ_V1.0.0")
    @XmlElement(name = "Name", namespace = CIM_MAPPING_SERVICE_NS_URI, required = true)
    private String name;
    @Schema(example = "RDF XML...", implementation = String.class)
    @XmlAnyElement
    protected Element rdf;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Element getRdf() {
        return rdf;
    }

    public void setRdf(Element rdf) {
        this.rdf = rdf;
    }
}
