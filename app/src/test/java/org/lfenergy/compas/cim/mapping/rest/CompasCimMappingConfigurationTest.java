// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.rest;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class CompasCimMappingConfigurationTest {
    private CompasCimMappingConfiguration configuration = new CompasCimMappingConfiguration();

    @Test
    void createCimToSclMapper_WhenCalled_ThenObjectReturned() {
        assertNotNull(configuration.createCimToSclMapper());
    }
}