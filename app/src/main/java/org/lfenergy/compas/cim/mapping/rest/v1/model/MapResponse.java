// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.rest.v1.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.lfenergy.compas.scl2007b4.model.SCL;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import static org.lfenergy.compas.cim.mapping.CimMappingConstants.CIM_MAPPING_SERVICE_V1_NS_URI;
import static org.lfenergy.compas.cim.mapping.CimMappingConstants.SCL_NS_URI;

@Schema(description = "Response with the converted CIM RDF Files to IEC 61850 SCL File.")
@XmlRootElement(name = "MapResponse", namespace = CIM_MAPPING_SERVICE_V1_NS_URI)
@XmlAccessorType(XmlAccessType.FIELD)
public class MapResponse {
    @Schema(description = "The SCL File created from the CIM RDF Files.")
    @XmlElement(name = "SCL", namespace = SCL_NS_URI)
    protected SCL scl;

    public SCL getScl() {
        return scl;
    }

    public void setScl(SCL scl) {
        this.scl = scl;
    }
}
