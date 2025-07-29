// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.cim.mapping.rest.v1.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.lfenergy.compas.cim.mapping.model.CimData;

import jakarta.validation.Valid;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;

import static org.lfenergy.compas.cim.mapping.CimMappingConstants.CIM_MAPPING_SERVICE_V1_NS_URI;

@Schema(description = "Request to convert CIM RDF Files to a single IEC 61850 SCL File.")
@XmlRootElement(name = "MapRequest", namespace = CIM_MAPPING_SERVICE_V1_NS_URI)
@XmlAccessorType(XmlAccessType.FIELD)
public class MapRequest {
    @Schema(description = "List of CIM RDF Files.")
    @Valid
    @XmlElement(name = "CimData", namespace = CIM_MAPPING_SERVICE_V1_NS_URI)
    private List<CimData> cimData;

    public List<CimData> getCimData() {
        return cimData;
    }

    public void setCimData(List<CimData> cimData) {
        this.cimData = cimData;
    }
}
