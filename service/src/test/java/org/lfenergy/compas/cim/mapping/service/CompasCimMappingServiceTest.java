// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class CompasCimMappingServiceTest {
    private CompasCimMappingService compasCimMappingService = new CompasCimMappingService();

    @Test
    void getMessage_WhenCalledWithName_ThenMessageIncludingNameIsReturned() {
        var result = compasCimMappingService.getMessage("Jan");

        assertEquals("Hello Jan", result);
    }

    @Test
    void getMessage_WhenCalledWithNull_ThenWorldMessageIsReturned() {
        var result = compasCimMappingService.getMessage(null);
        assertEquals("Hello world", result);
    }

    @Test
    void getMessage_WhenCalledWithEmptyString_ThenWorldMessageIsReturned() {
        var result = compasCimMappingService.getMessage("");
        assertEquals("Hello world", result);
    }
}