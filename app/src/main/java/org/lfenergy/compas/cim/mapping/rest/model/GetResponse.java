// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.cim.mapping.rest.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static org.lfenergy.compas.cim.mapping.rest.Constants.CIM_MAPPING_SERVICE_NS_URI;

@XmlRootElement(name = "GetResponse", namespace = CIM_MAPPING_SERVICE_NS_URI)
@XmlAccessorType(XmlAccessType.FIELD)
public class GetResponse {
    @Schema(example = "Message")
    @XmlElement(name = "Message", namespace = CIM_MAPPING_SERVICE_NS_URI)
    protected String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
