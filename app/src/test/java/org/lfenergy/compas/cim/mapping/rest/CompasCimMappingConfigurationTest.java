// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.rest;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class CompasCimMappingConfigurationTest {
    private CompasCimMappingConfiguration configuration = new CompasCimMappingConfiguration();

    @Test
    void createElementConverter_WhenCalled_ThenObjectReturned() {
        assertNotNull(configuration.createElementConverter());
    }

    @Test
    void createSubstationMapper_WhenCalled_ThenObjectReturned() {
        assertNotNull(configuration.createSubstationMapper());
    }
}