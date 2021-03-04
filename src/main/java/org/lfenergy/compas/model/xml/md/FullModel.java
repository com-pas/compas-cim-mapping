// SPDX-FileCopyrightText: 2020 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.model.xml.md;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.lfenergy.compas.model.xml.CimModel;

/**
 * FullModel XML section
 * Containing all information in a FullModel section.
 */
@XmlRootElement(name = "FullModel")
@XmlAccessorType(XmlAccessType.FIELD)
public class FullModel extends CimModel implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @XmlAttribute(name = "about", namespace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#")
    private String about;

    @XmlElement(name = "Model.created")
    private Date created;

    @XmlElement(name = "Model.scenarioTime")
    private Date scenarioTime;

    @XmlElement(name = "Model.version")
    private Integer version;

    @XmlElement(name = "Model.description")
    private String description;

    @XmlElement(name = "Model.modelingAuthoritySet")
    private String modelingAuthoritySet;

    @XmlElement(name = "Model.profile")
    private ArrayList<String> profile;

    @Override
    public String toString() {
        return new StringBuilder("FullModel { ")
                    .append("about: ").append(about).append(", ")
                    .append("created: ").append(created).append(", ")
                    .append("scenarioTime: ").append(scenarioTime).append(", ")
                    .append("version: ").append(version).append(", ")
                    .append("description: ").append(description).append(", ")
                    .append("modelingAuthoritySet: ").append(modelingAuthoritySet).append(", ")
                    .append("profile: ").append(profile)
                    .append(" }")
                    .toString();
    }
}
