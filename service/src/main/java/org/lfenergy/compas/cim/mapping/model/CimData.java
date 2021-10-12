// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.lfenergy.compas.cim.mapping.constraint.CimDataNamePattern;

import javax.validation.constraints.NotBlank;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import static org.lfenergy.compas.cim.mapping.CimMappingConstants.CIM_MAPPING_SERVICE_V1_NS_URI;

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

    @NotBlank
    @XmlElement(name = "RdfData", namespace = CIM_MAPPING_SERVICE_V1_NS_URI, required = true)
    @Schema(example = "RDF XML", implementation = String.class)
    private String rdfData;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRdfData() {
        return rdfData;
    }

    public void setRdfData(String rdfData) {
        this.rdfData = rdfData;
    }
}
