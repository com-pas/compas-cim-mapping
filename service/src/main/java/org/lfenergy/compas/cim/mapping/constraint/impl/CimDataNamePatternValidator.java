// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.constraint.impl;

import com.powsybl.cgmes.model.CgmesSubset;
import org.lfenergy.compas.cim.mapping.constraint.CimDataNamePattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CimDataNamePatternValidator implements ConstraintValidator<CimDataNamePattern, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value != null && !value.isBlank()) {
            Pattern p = Pattern.compile(REGEX_VALID_NAME);
            return p.matcher(value).matches();
        }
        return true;
    }

    // Copy from PowSyBl, these files are normally accepted by the CGMES Implementation.
    private static final String REGEX_VALID_NAME_IDS = Stream.of(
                    Stream.of("ME"),
                    Arrays.stream(CgmesSubset.values()).map(CgmesSubset::getIdentifier))
            .flatMap(s -> s)
            .collect(Collectors.joining("|", "(", ")"));
    public static final String REGEX_VALID_NAME = ""
            // Ignore case
            + "(?i)"
            // Any number of characters from the start
            + "^.*"
            // Contains one of the valid subset ids
            + REGEX_VALID_NAME_IDS
            // Any number of characters and ending with extension .xml
            + ".*\\.XML$";
}
