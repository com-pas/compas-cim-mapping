// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.cgmes;

import com.powsybl.cgmes.model.CgmesModelException;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CgmesCimReaderTest {
    @Mock
    private CgmesDataValidator cgmesDataValidator;
    @Mock
    private ElementConverter converter;

    @InjectMocks
    private CgmesCimReader cgmesCimReader;

    @Test
    void readModel_WhenReadingCimModel_ThenNetWorkObjectReturnedWithSubstations() throws IOException {
        var cimData = new CimData();
        cimData.setName("MiniGridTestConfiguration_BC_EQ_v3.0.0.xml");
        var cimDataList = List.of(cimData);

        when(converter.convertToString(any(), eq(false))).thenReturn(readFile());

        var result = cgmesCimReader.readModel(cimDataList);

        assertEquals(5, result.getNetwork().getSubstationStream().count());
        verify(cgmesDataValidator, times(1)).validateData(cimDataList);
        verifyNoMoreInteractions(cgmesDataValidator, converter);
    }

    @Test
    void readModel_WhenReadingWithoutCimModel_ThenNetWorkObjectReturnedWithSubstations() throws IOException {
        List<CimData> cimDataList = Collections.emptyList();

        assertThrows(CgmesModelException.class,
                () -> cgmesCimReader.readModel(cimDataList));

        verify(cgmesDataValidator, times(1)).validateData(cimDataList);
        verifyNoMoreInteractions(cgmesDataValidator, converter);
    }

    private String readFile() throws IOException {
        var resource = requireNonNull(getClass().getResource("/minigrid/MiniGridTestConfiguration_BC_EQ_v3.0.0.xml"));
        var path = Paths.get(resource.getPath());
        return String.join("", Files.readAllLines(path));
    }
}