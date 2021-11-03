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
@Schema(description = "Information and content about a single CIM RDF File.")
@XmlType(name = "CimDataType", namespace = CIM_MAPPING_SERVICE_V1_NS_URI)
@XmlAccessorType(XmlAccessType.FIELD)
public class CimData {
    @Schema(description = "Name of the CIM RDF File.", example = "CONFIG_BC_EQ_V1.0.0.XML")
    @NotBlank
    @CimDataNamePattern
    @XmlElement(name = "Name", namespace = CIM_MAPPING_SERVICE_V1_NS_URI, required = true)
    private String name;

    @Schema(description = "The RDF CIM Content of the file.",
            example = "<![CDATA[<rdf:RDF xmlns:cim=\"http://iec.ch/TC57/2013/CIM-schema-cim16#\" " +
                    "xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">....</rdf:RDF>]]")
    @NotBlank
    @XmlElement(name = "RdfData", namespace = CIM_MAPPING_SERVICE_V1_NS_URI, required = true)
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
