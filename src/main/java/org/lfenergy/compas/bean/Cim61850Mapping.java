// SPDX-FileCopyrightText: 2020 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.bean;

import javax.enterprise.context.RequestScoped;

import org.jboss.logging.Logger;

@RequestScoped
public class Cim61850Mapping {

    private static final Logger LOGGER = Logger.getLogger(Cim61850Mapping.class);

    /**
     * Get the 61850 mapping from a CIM config file.
     * @param model the CIM config file.
     * @return
     */
    public String get61850Mapping() {
        LOGGER.info("get61850Mapping");
        return "";
    }
    
}
