// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.model;

import org.junit.jupiter.api.Test;
import org.lfenergy.compas.cim.mapping.exception.CompasCimMappingException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.lfenergy.compas.cim.mapping.exception.CompasCimMappingErrorCode.UNKNOWN_TYPE_ERROR_CODE;

class SwitchTypeTest {
    @Test
    void convertSwitchType_WhenCalledWithKnownCimType_ThenSCLTypeIsReturned() {
        var expectedType = SwitchType.CAB;

        var result = SwitchType.convertSwitchType(expectedType.getCimTypes().get(0));
        assertEquals(expectedType, result);
    }

    @Test
    void convertSwitchType_WhenCalledWithUnknownCimType_ThenExceptionThrown() {
        var exception = assertThrows(CompasCimMappingException.class,
                () -> SwitchType.convertSwitchType("UNKNOWN TYPE"));
        assertEquals(UNKNOWN_TYPE_ERROR_CODE, exception.getErrorCode());
    }
}