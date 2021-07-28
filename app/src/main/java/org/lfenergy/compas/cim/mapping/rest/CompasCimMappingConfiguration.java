// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.rest;

import org.lfenergy.compas.cim.mapping.mapper.CimToSclMapper;
import org.lfenergy.compas.core.commons.ElementConverter;

import javax.enterprise.inject.Produces;

/**
 * Create Beans from other dependencies that are used in the application.
 */
public class CompasCimMappingConfiguration {
    @Produces
    public ElementConverter createElementConverter() {
        return new ElementConverter();
    }

    @Produces
    public CimToSclMapper createSclMapper() {
        return CimToSclMapper.INSTANCE;
    }
}
