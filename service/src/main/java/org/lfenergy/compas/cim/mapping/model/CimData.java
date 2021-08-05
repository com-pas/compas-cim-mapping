// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.lfenergy.compas.cim.mapping.constraint.CimDataNamePattern;
import org.lfenergy.compas.core.commons.constraint.XmlAnyElementValid;
import org.w3c.dom.Element;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

import static org.lfenergy.compas.cim.mapping.CimMappingConstants.CIM_MAPPING_SERVICE_V1_NS_URI;
import static org.lfenergy.compas.cim.mapping.CimMappingConstants.RDF_NS_URI;

/**
 * Simple Pojo Class to hold a source entry of Cim XML. PowSyBl needs to have a (file)name and of
 * course the RDF XML (as Element).
 */
@XmlType(name = "CimDataType", namespace = CIM_MAPPING_SERVICE_V1_NS_URI)
@XmlAccessorType(XmlAccessType.FIELD)
public class CimData {
    @NotBlank
    @CimDataNamePattern
    @Schema(example = "CONFIG_BC_EQ_V1.0.0.XML")
    @XmlElement(name = "Name", namespace = CIM_MAPPING_SERVICE_V1_NS_URI, required = true)
    private String name;

    @Size(min = 1, max = 1, message = "{org.lfenergy.compas.XmlAnyElementValid.moreElements.message}")
    @XmlAnyElementValid(elementName = "RDF", elementNamespace = RDF_NS_URI)
    @Schema(example = "RDF XML...", implementation = String.class)
    @XmlAnyElement
    protected List<Element> rdf = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Element> getRdf() {
        return rdf;
    }

    public void setRdf(List<Element> rdf) {
        this.rdf = rdf;
    }
}
