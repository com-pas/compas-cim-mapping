// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.cgmes;

import com.powsybl.cgmes.model.CgmesSubset;
import org.lfenergy.compas.cim.mapping.model.CimData;
import org.lfenergy.compas.core.commons.exception.CompasValidationException;

import javax.enterprise.context.ApplicationScoped;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.lfenergy.compas.cim.mapping.exception.CompasCimMappingErrorCode.INVALID_NAMES_ERROR_CODE;

/**
 * Class to validate if all the needed data is in the correct format.
 * The PowSyBl Importer ignore entries that don't have the correct (file)name.
 */
@ApplicationScoped
public class CgmesDataValidator {
    /**
     * Validates the name of every name of CimData in the list.
     * <p>
     * If one fails the format check a CompasValidationException will be thrown.
     *
     * @param cimData The List of CimData with the names to check.
     */
    public void validateData(List<CimData> cimData) {
        Pattern p = Pattern.compile(REGEX_VALID_NAME);
        var invalidNames = cimData.stream()
                .map(CimData::getName)
                .filter(name -> !p.matcher(name).matches())
                .collect(Collectors.toSet());

        if (!invalidNames.isEmpty()) {
            var message = String.format("The following names don't match %s, %s",
                    REGEX_VALID_NAME, String.join(",", invalidNames));
            throw new CompasValidationException(INVALID_NAMES_ERROR_CODE, message);
        }
    }

    // Copy from PowSyBl, these files are normally accepted by the CGMES Implementation.
    private static final String REGEX_VALID_NAME_IDS = Stream.of(
            Stream.of("ME"),
            Arrays.stream(CgmesSubset.values()).map(CgmesSubset::getIdentifier))
            .flatMap(s -> s)
            .collect(Collectors.joining("|", "(", ")"));
    private static final String REGEX_VALID_NAME = ""
            // Ignore case
            + "(?i)"
            // Any number of characters from the start
            + "^.*"
            // Contains one of the valid subset ids
            + REGEX_VALID_NAME_IDS
            // Any number of characters and ending with extension .xml
            + ".*\\.XML$";
}
