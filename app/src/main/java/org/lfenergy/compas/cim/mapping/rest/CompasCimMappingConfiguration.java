// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.rest;

import org.lfenergy.compas.cim.mapping.mapper.CimToSclMapper;

import javax.enterprise.inject.Produces;

/**
 * Create Beans from other dependencies that are used in the application.
 */
public class CompasCimMappingConfiguration {
    @Produces
    public CimToSclMapper createCimToSclMapper() {
        return CimToSclMapper.INSTANCE;
    }
}
