// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.service;

import com.powsybl.cgmes.model.CgmesModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.compas.cim.mapping.cgmes.CgmesCimReader;
import org.lfenergy.compas.cim.mapping.mapper.CimToSclMapper;
import org.lfenergy.compas.cim.mapping.mapper.CimToSclMapperContext;
import org.lfenergy.compas.cim.mapping.model.CimData;
import org.lfenergy.compas.scl2007b4.model.SCL;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompasCimMappingServiceTest {
    @Mock
    private CgmesModel cgmesModel;

    @Mock
    private CgmesCimReader cgmesCimReader;
    @Mock
    private CimToSclMapper cimToSclMapper;

    @InjectMocks
    private CompasCimMappingService compasCimMappingService;

    @Test
    void map_WhenCalledWithData_ThenReaderAndMapperAreCalled() {
        when(cgmesCimReader.readModel(any())).thenReturn(cgmesModel);

        var cimDataList = List.of(new CimData());
        var scl = compasCimMappingService.map(cimDataList, "username");

        assertNotNull(scl);
        verify(cgmesCimReader, times(1)).readModel(cimDataList);
        verify(cimToSclMapper, times(1)).mapToScl(any(SCL.class), any(CimToSclMapperContext.class));
        verifyNoMoreInteractions(cgmesCimReader, cimToSclMapper);
    }

    @Test
    void map_WhenCalledWithoutData_ThenReaderAndMapperAreNotCalled() {
        var scl = compasCimMappingService.map(Collections.emptyList(), "username");

        assertNotNull(scl);
        verifyNoInteractions(cgmesCimReader, cimToSclMapper);
    }

    @Test
    void map_WhenCalledWithNullValue_ThenReaderAndMapperAreNotCalled() {
        var scl = compasCimMappingService.map(null, "username");

        assertNotNull(scl);
        verifyNoInteractions(cgmesCimReader, cimToSclMapper);
    }

    @Test
    void createBasicSCL_WhenCalledWithData_ThenNewSCLInstanceReturnedWithPartsFilled() {
        var cimData = new CimData();
        cimData.setName("FILE1.xml");
        var cimDataList = List.of(cimData);

        createBasicSCL_WhenCalled_ThenExpectedName(cimDataList);
    }

    @Test
    void createBasicSCL_WhenCalledWithoutData_ThenNewSCLInstanceReturnedWithPartsFilled() {
        createBasicSCL_WhenCalled_ThenExpectedName(Collections.emptyList());
    }

    @Test
    void createBasicSCL_WhenCalledWithNullValue_ThenNewSCLInstanceReturnedWithPartsFilled() {
        createBasicSCL_WhenCalled_ThenExpectedName(null);
    }

    private void createBasicSCL_WhenCalled_ThenExpectedName(List<CimData> cimDataList) {
        var expectedName = "Mr. Name";

        var scl = compasCimMappingService.createBasicSCL(cimDataList, expectedName);

        assertNotNull(scl);
        assertEquals("2007", scl.getVersion());
        assertEquals("B", scl.getRevision());
        assertEquals((short) 4, scl.getRelease());

        var header = scl.getHeader();
        assertNotNull(header);
        assertNotNull(header.getId());
        assertEquals("0.0.1", header.getVersion());
        assertEquals("", header.getRevision());

        assertNotNull(header.getHistory());
        assertEquals(1, header.getHistory().getHitem().size());

        var item = header.getHistory().getHitem().get(0);
        assertEquals("0.0.1", item.getVersion());
        assertEquals("", item.getRevision());
        assertNotNull(item.getWhen());
        assertEquals(expectedName, item.getWho());

        assertNotNull(item.getWhat());
        if (cimDataList != null && !cimDataList.isEmpty()) {
            cimDataList.forEach(cimData -> assertTrue(item.getWhat().contains(cimData.getName())));
        }
    }
}