// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.cgmes;

import org.junit.jupiter.api.Test;
import org.lfenergy.compas.cim.mapping.model.CimData;
import org.lfenergy.compas.core.commons.exception.CompasValidationException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CgmesDataValidatorTest {
    private CgmesDataValidator validator = new CgmesDataValidator();

    @Test
    void validateData_WhenCalledWithCorrectName_ThenNoExceptionIsThrown() {
        var cimData = new CimData();
        cimData.setName("MiniGridTestConfiguration_BC_EQ_v3.0.0.xml");

        validator.validateData(List.of(cimData));
    }

    @Test
    void validateData_WhenCalledWithIncorrectName_ThenExceptionIsThrown() {
        var expectedInString1 = "MiniGridTestConfiguration_BC_EQ_v3.0.0"; // NO XML Extension
        var cimData1 = new CimData();
        cimData1.setName(expectedInString1);

        var expectedInString2 = "MiniGridTestConfiguration_BCv3.0.0.xml"; // NO _EQ_ (CgmesSubset) Extension
        var cimData2 = new CimData();
        cimData2.setName(expectedInString2);

        var exception = assertThrows(CompasValidationException.class,
                () -> validator.validateData(List.of(cimData1, cimData2)));
        assertTrue(exception.getMessage().contains(expectedInString1));
        assertTrue(exception.getMessage().contains(expectedInString2));
    }
}