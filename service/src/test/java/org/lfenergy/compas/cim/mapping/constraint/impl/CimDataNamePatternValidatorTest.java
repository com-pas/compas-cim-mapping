// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.constraint.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lfenergy.compas.cim.mapping.constraint.CimDataNamePattern;

import javax.validation.Validation;
import javax.validation.Validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CimDataNamePatternValidatorTest {
    private Validator validator;

    @BeforeEach
    void setupValidator() {
        var factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void isValid_WhenCalledWithCorrectName_ThenNoViolations() {
        isValid_WithNoViolations("MiniGridTestConfiguration_BC_EQ_v3.0.0.xml");
    }

    @Test
    void isValid_WhenCalledWithNullName_ThenNoViolations() {
        isValid_WithNoViolations(null);
    }

    @Test
    void isValid_WhenCalledWithBlankName_ThenNoViolations() {
        isValid_WithNoViolations("");
    }

    private void isValid_WithNoViolations(String name) {
        var simplePojo = new SimplePojo(name);

        var violations = validator.validate(simplePojo);
        assertTrue(violations.isEmpty());
    }

    @Test
    void isValid_WhenCalledWithIncorrectNameWithoutExtension_ThenViolationFound() {
        var simplePojo = new SimplePojo("MiniGridTestConfiguration_BC_EQ_v3.0.0");

        var violations = validator.validate(simplePojo);
        assertEquals(1, violations.size());
    }

    @Test
    void isValid_WhenCalledWithIncorrectNameWithoutEQ_ThenViolationFound() {
        var simplePojo = new SimplePojo("MiniGridTestConfiguration_BCv3.0.0.XML");

        var violations = validator.validate(simplePojo);
        assertEquals(1, violations.size());
    }

    private static final class SimplePojo {
        @CimDataNamePattern
        private String name;

        public SimplePojo(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}