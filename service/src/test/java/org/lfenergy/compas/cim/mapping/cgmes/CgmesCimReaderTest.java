// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.cgmes;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.compas.cim.mapping.model.CimData;
import org.lfenergy.compas.core.commons.ElementConverter;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CgmesCimReaderTest {
    @Mock
    private ElementConverter converter;

    @InjectMocks
    private CgmesCimReader cgmesCimReader;

    @Test
    void readModel_WhenReadingCimModel_ThenCgmesModelReturnedWithSubstations() throws IOException {
        var cimData = new CimData();
        cimData.setName("MiniGridTestConfiguration_BC_EQ_v3.0.0.xml");
        cimData.getRdf().add(null); // Fake added an Element, will be fixed bij mocking below.
        var cimDataList = List.of(cimData);

        when(converter.convertToString(any(), eq(false))).thenReturn(readFile());

        var result = cgmesCimReader.readModel(cimDataList);

        assertEquals(5, result.substations().size());
        verifyNoMoreInteractions(converter);
    }

    @Test
    void readModel_WhenReadingWithoutCimModel_ThenCgmesModelReturnedWithoutSubstations() {
        List<CimData> cimDataList = Collections.emptyList();

        var result = cgmesCimReader.readModel(cimDataList);

        assertEquals(0, result.substations().size());
        verifyNoMoreInteractions(converter);
    }

    @Test
    void readModel_WhenReadingWithoutRdfElement_ThenCgmesModelReturnedWithoutSubstations() {
        var cimData = new CimData();
        cimData.setName("MiniGridTestConfiguration_BC_EQ_v3.0.0.xml");
        cimData.setRdf(null);
        var cimDataList = List.of(cimData);

        var result = cgmesCimReader.readModel(cimDataList);

        assertEquals(0, result.substations().size());
        verifyNoMoreInteractions(converter);
    }

    @Test
    void readModel_WhenReadingWithTooManyRdfElement_ThenCgmesModelReturnedWithoutSubstations() {
        var cimData = new CimData();
        cimData.setName("MiniGridTestConfiguration_BC_EQ_v3.0.0.xml");
        cimData.getRdf().add(null); // Fake added an Element
        cimData.getRdf().add(null); // Fake added an Element
        var cimDataList = List.of(cimData);

        var result = cgmesCimReader.readModel(cimDataList);

        assertEquals(0, result.substations().size());
        verifyNoMoreInteractions(converter);
    }

    private String readFile() throws IOException {
        var resource = requireNonNull(getClass().getResource("/minigrid/MiniGridTestConfiguration_BC_EQ_v3.0.0.xml"));
        var path = Paths.get(resource.getPath());
        return String.join("", Files.readAllLines(path));
    }
}