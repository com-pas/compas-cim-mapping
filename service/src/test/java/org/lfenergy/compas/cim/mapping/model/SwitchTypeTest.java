// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.model;

import org.junit.jupiter.api.Test;
import org.lfenergy.compas.cim.mapping.exception.CompasCimMappingException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.lfenergy.compas.cim.mapping.exception.CompasCimMappingErrorCode.UNKNOWN_TYPE_ERROR_CODE;

class SwitchTypeTest {
    @Test
    void convertSwitchType_WhenCalledWithKnownCimType_ThenSCLTypeIsReturned() {
        var expectedType = SwitchType.IFL;

        var result = SwitchType.convertSwitchType(expectedType.getCimTypes().get(0));
        assertEquals(expectedType, result);
    }

    @Test
    void convertSwitchType_WhenCalledWithUnknownCimType_ThenExceptionThrown() {
        var exception = assertThrows(CompasCimMappingException.class,
                () -> SwitchType.convertSwitchType("UNKNOWN TYPE"));
        assertEquals(UNKNOWN_TYPE_ERROR_CODE, exception.getErrorCode());
    }

    @Test
    void checkDoubleEntries_WhenCimTypeAreCoupledToMoreThanOneIECCOde_ThenFail() {
        var listOfCimType = new ArrayList<String>();
        var listOfDoubleType = new ArrayList<String>();

        Arrays.stream(SwitchType.values())
                .flatMap(switchType -> switchType.getCimTypes().stream())
                .forEach(cimType -> {
                    if (listOfCimType.contains(cimType)) {
                        listOfDoubleType.add(cimType);
                    } else {
                        listOfCimType.add(cimType);
                    }
                });

        if (listOfDoubleType.size() > 0) {
            var message = "The following cim type where already mapped: " +
                    listOfDoubleType.stream().collect(Collectors.joining(", "));
            fail(message);
        }
    }
}