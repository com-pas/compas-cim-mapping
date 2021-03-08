// SPDX-FileCopyrightText: 2020 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.model.xml.rdf;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * Representing a fully of partly XML potion.
 * *Each XML model should extend this class!*
 */
public abstract class RdfSection {

    /**
     * The about attribute
     */
    @XmlAttribute(name = "about", namespace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#")
    private String about;

    /**
     * The id attribute
     */
    @XmlAttribute(name = "ID", namespace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#")
    private String id;
    
    /**
     * The resource attribute
     */
    @XmlAttribute(name = "resource", namespace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#")
    private String resource;

    public String getAbout() {
        return about;
    }

    public String getId() {
        return id;
    }

    public String getResource() {
        return resource;
    }
    
}
