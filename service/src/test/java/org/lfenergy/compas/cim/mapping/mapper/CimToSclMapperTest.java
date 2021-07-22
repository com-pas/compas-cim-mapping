// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.mapper;

import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.Substation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.compas.scl2007b4.model.SCL;
import org.lfenergy.compas.scl2007b4.model.TSubstation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CimToSclMapperTest {
    @Mock
    private Network network;
    @Mock
    private Substation substation;

    @Mock
    private SubstationMapper substationMapper;

    @InjectMocks
    private CimToSclMapper mapper;

    @Test
    void mapCimToScl_WhenCalledWithNetworkIncludingSubstations_ThenSubstationsAreMapped() {
        var scl = new SCL();

        when(network.getSubstationStream()).thenReturn(Stream.of(substation));
        when(substationMapper.substationToTSubstation(substation)).thenReturn(new TSubstation());

        mapper.mapCimToScl(network, scl);

        assertEquals(1, scl.getSubstation().size());
        verify(network, atLeastOnce()).getSubstationStream();
        verify(substationMapper, atLeastOnce()).substationToTSubstation(substation);
        verifyNoMoreInteractions(network, substationMapper);
    }

    @Test
    void mapCimToScl_WhenCalledWithNetworkWithoutSubstations_ThenNoMappingIsDone() {
        var scl = new SCL();

        when(network.getSubstationStream()).thenReturn(Stream.empty());

        mapper.mapCimToScl(network, scl);

        assertEquals(0, scl.getSubstation().size());
        verify(network, atLeastOnce()).getSubstationStream();
        verifyNoMoreInteractions(network, substationMapper);
    }
}