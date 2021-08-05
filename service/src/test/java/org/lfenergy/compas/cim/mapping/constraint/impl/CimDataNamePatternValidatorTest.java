// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.constraint.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lfenergy.compas.cim.mapping.constraint.CimDataNamePattern;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.xml.parsers.ParserConfigurationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CimDataNamePatternValidatorTest {
    private Validator validator;

    @BeforeEach
    void setupValidator() throws ParserConfigurationException {
        var factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void isValid_WhenCalledWithCorrectName_ThenNoViolations() {
        var simplePojo = new SimplePojo();
        simplePojo.setName("MiniGridTestConfiguration_BC_EQ_v3.0.0.xml");

        var violations = validator.validate(simplePojo);
        assertTrue(violations.isEmpty());
    }

    @Test
    void isValid_WhenCalledWithNoName_ThenNoViolations() {
        var simplePojo = new SimplePojo();
        simplePojo.setName(null);

        var violations = validator.validate(simplePojo);
        assertTrue(violations.isEmpty());
    }

    @Test
    void isValid_WhenCalledWithIncorrectNameWithoutExtension_ThenViolationFound() {
        var simplePojo = new SimplePojo();
        simplePojo.setName("MiniGridTestConfiguration_BC_EQ_v3.0.0");

        var violations = validator.validate(simplePojo);
        assertEquals(1, violations.size());
    }

    @Test
    void isValid_WhenCalledWithIncorrectNameWithoutEQ_ThenViolationFound() {
        var simplePojo = new SimplePojo();
        simplePojo.setName("MiniGridTestConfiguration_BCv3.0.0.XML");

        var violations = validator.validate(simplePojo);
        assertEquals(1, violations.size());
    }

    private static final class SimplePojo {
        @CimDataNamePattern
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}