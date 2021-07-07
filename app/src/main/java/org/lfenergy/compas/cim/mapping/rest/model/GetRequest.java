// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.cim.mapping.rest.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.lfenergy.compas.cim.mapping.rest.Constants;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "GetRequest", namespace = Constants.CIM_MAPPING_SERVICE_NS_URI)
@XmlAccessorType(XmlAccessType.FIELD)
public class GetRequest {
    @Schema(example = "Jan")
    @XmlElement(name = "Name", namespace = Constants.CIM_MAPPING_SERVICE_NS_URI)
    protected String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
