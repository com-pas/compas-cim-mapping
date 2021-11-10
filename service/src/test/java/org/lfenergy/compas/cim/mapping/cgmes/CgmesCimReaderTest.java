// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.cgmes;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.compas.cim.mapping.exception.CompasCimMappingException;
import org.lfenergy.compas.cim.mapping.model.CimData;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.lfenergy.compas.cim.mapping.exception.CompasCimMappingErrorCode.NO_DATA_ERROR_CODE;

@ExtendWith(MockitoExtension.class)
class CgmesCimReaderTest {
    @InjectMocks
    private CgmesCimReader cgmesCimReader;

    @Test
    void readModel_WhenReadingCimModel_ThenCgmesModelReturnedWithSubstations() throws IOException {
        var cimData = new CimData();
        cimData.setName("MiniGridTestConfiguration_BC_EQ_v3.0.0.xml");
        cimData.setRdfData(readFile());
        var cimDataList = List.of(cimData);

        var result = cgmesCimReader.readModel(cimDataList);

        assertEquals(5, result.substations().size());
    }

    @Test
    void readModel_WhenReadingWithEmptyCimDataList_ThenExceptionThrown() {
        List<CimData> cimDataList = Collections.emptyList();

        var exception = assertThrows(CompasCimMappingException.class,
                () -> cgmesCimReader.readModel(cimDataList));

        assertEquals(NO_DATA_ERROR_CODE, exception.getErrorCode());
    }

    @Test
    void readModel_WhenReadingWithNullList_ThenExceptionThrown() {
        var exception = assertThrows(CompasCimMappingException.class,
                () -> cgmesCimReader.readModel(null));

        assertEquals(NO_DATA_ERROR_CODE, exception.getErrorCode());
    }

    private String readFile() throws IOException {
        var resource = requireNonNull(getClass().getResource("/minigrid/MiniGridTestConfiguration_BC_EQ_v3.0.0.xml"));
        var path = Paths.get(resource.getPath());
        return String.join("", Files.readAllLines(path));
    }
}