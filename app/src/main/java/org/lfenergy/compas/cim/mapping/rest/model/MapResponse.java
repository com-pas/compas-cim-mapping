// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.cim.mapping.rest.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.lfenergy.compas.scl2007b4.model.SCL;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static org.lfenergy.compas.cim.mapping.Constants.CIM_MAPPING_SERVICE_NS_URI;
import static org.lfenergy.compas.cim.mapping.Constants.SCL_NS_URI;

@XmlRootElement(name = "MapResponse", namespace = CIM_MAPPING_SERVICE_NS_URI)
@XmlAccessorType(XmlAccessType.FIELD)
public class MapResponse {
    @Schema(example = "SCL XML")
    @XmlElement(name = "SCL", namespace = SCL_NS_URI)
    protected SCL scl;

    public SCL getScl() {
        return scl;
    }

    public void setScl(SCL scl) {
        this.scl = scl;
    }
}
