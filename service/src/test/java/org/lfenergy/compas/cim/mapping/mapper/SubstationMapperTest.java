// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.mapper;

import com.powsybl.iidm.network.Substation;
import com.powsybl.iidm.network.VoltageLevel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubstationMapperTest {
    @Mock
    private Substation substation;

    @Mock
    private VoltageLevel voltageLevel;

    @Test
    void substationToTSubstation_WhenCalledWithSubstation_ThenPropertiesMappedToTSubstation() {
        var expectedId = UUID.randomUUID().toString();
        var expectedDesc = "Some description";

        when(substation.getId()).thenReturn(expectedId);
        when(substation.getOptionalName()).thenReturn(Optional.of(expectedDesc));
        when(substation.getVoltageLevelStream()).thenReturn(null);

        var sclSubstation = SubstationMapper.INSTANCE.substationToTSubstation(substation);

        assertNotNull(sclSubstation);
        assertEquals(expectedId, sclSubstation.getName());
        assertEquals(expectedDesc, sclSubstation.getDesc());
        verify(substation, atLeastOnce()).getId();
        verify(substation, atLeastOnce()).getOptionalName();
        verify(substation, atLeastOnce()).getVoltageLevelStream();
        verifyNoMoreInteractions(substation);
    }

    @Test
    void substationToTSubstation_WhenCalledWithVoltageLevels_ThenListsMappedToTSubstation() {
        when(substation.getVoltageLevelStream()).thenReturn(Stream.of(voltageLevel));

        var sclSubstation = SubstationMapper.INSTANCE.substationToTSubstation(substation);
        assertNotNull(sclSubstation);
        assertEquals(1, sclSubstation.getVoltageLevel().size());
        verify(substation, atLeastOnce()).getVoltageLevelStream();
    }

    @Test
    void voltageLevelToTVoltageLevel_WhenCalledWithVoltageLevel_ThenPropertiesMappedToTVoltageLevel() {
        var expectedName = "TheName";
        var expectedVoltage = BigDecimal.valueOf(100.0);

        when(voltageLevel.getNameOrId()).thenReturn(expectedName);
        when(voltageLevel.getNominalV()).thenReturn(expectedVoltage.doubleValue());

        var sclVoltageLevel = SubstationMapper.INSTANCE.voltageLevelToTVoltageLevel(voltageLevel);

        assertNotNull(sclVoltageLevel);
        assertEquals(expectedName, sclVoltageLevel.getName());
        assertEquals(expectedVoltage, sclVoltageLevel.getVoltage().getValue());
        verify(voltageLevel, atLeastOnce()).getNameOrId();
        verify(voltageLevel, atLeastOnce()).getNominalV();
        verifyNoMoreInteractions(voltageLevel);
    }

    @Test
    void optionalString_WhenCalledWithFilledOptional_ThenStringValueReturned() {
        var expectedValue = "Some string";

        var value = SubstationMapper.INSTANCE.optionalString(Optional.of(expectedValue));

        assertEquals(expectedValue, value);
    }

    @Test
    void optionalString_WhenCalledWithEmptyOptional_ThenBlankStringReturned() {
        var expectedValue = "";

        var value = SubstationMapper.INSTANCE.optionalString(Optional.empty());

        assertEquals(expectedValue, value);
    }
}