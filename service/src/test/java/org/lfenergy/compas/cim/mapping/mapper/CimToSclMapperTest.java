// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.compas.cim.mapping.cgmes.CgmesCimReader;
import org.lfenergy.compas.cim.mapping.cgmes.CgmesDataValidator;
import org.lfenergy.compas.cim.mapping.model.*;
import org.lfenergy.compas.core.commons.ElementConverter;
import org.lfenergy.compas.scl2007b4.model.SCL;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CimToSclMapperTest {
    @Mock
    private CimToSclMapperContext context;

    @Mock
    private CgmesSubstation cgmesSubstation;
    @Mock
    private CgmesVoltageLevel cgmesVoltageLevel;
    @Mock
    private CgmesBay cgmesBay;
    @Mock
    private CgmesConnectivityNode cgmesConnectivityNode;
    @Mock
    private CgmesSwitch cgmesSwitch;

    private CimToSclMapper mapper;

    @BeforeEach
    void setup() {
        mapper = Mappers.getMapper(CimToSclMapper.class);
    }

    @Test
    void map_WhenWithCimData_ThenSclMapped() throws IOException {
        // This is a overall test to see the whole mapping working with a test CIM File.
        var converter = new ElementConverter();
        var reader = new CgmesCimReader(new CgmesDataValidator(), converter);
        var cimData = new CimData();
        cimData.setName("MiniGridTestConfiguration_BC_EQ_v3.0.0.xml");
        cimData.setRdf(converter.convertToElement(readFile(), "RDF", "http://www.w3.org/1999/02/22-rdf-syntax-ns#"));
        var cgmesModel = reader.readModel(List.of(cimData));

        var result = new SCL();
        mapper.mapToScl(result, new CimToSclMapperContext(cgmesModel));

        assertNotNull(result);

        assertEquals(5, result.getSubstation().size());
        var substation = result.getSubstation().get(0);
        assertEquals("_af9a4ae3-ba2e-4c34-8e47-5af894ee20f4", substation.getName());
        assertEquals("Sub1", substation.getDesc());

        assertEquals(3, substation.getVoltageLevel().size());
        var voltageLevel = substation.getVoltageLevel().get(0);
        assertEquals("S1 380kV", voltageLevel.getName());
        assertNotNull(voltageLevel.getVoltage());
        assertEquals(BigDecimal.valueOf(380.0), voltageLevel.getVoltage().getValue());

        assertEquals(3, voltageLevel.getBay().size());
        var bay = voltageLevel.getBay().get(0);
        assertEquals("BAY_T4_2", bay.getName());

        assertEquals(3, bay.getConnectivityNode().size());
        var connectivityNode = bay.getConnectivityNode().get(0);
        assertEquals("CONNECTIVITY_NODE83", connectivityNode.getName());
        assertEquals("_af9a4ae3-ba2e-4c34-8e47-5af894ee20f4/S1 380kV/BAY_T4_2/CONNECTIVITY_NODE83", connectivityNode.getPathName());

        assertEquals(3, bay.getConductingEquipment().size());
        var conductingEquipment = bay.getConductingEquipment().get(0);
        assertEquals("BREAKER25", conductingEquipment.getName());
        assertEquals("CBR", conductingEquipment.getType());
    }

    private String readFile() throws IOException {
        var resource = requireNonNull(getClass().getResource("/minigrid/MiniGridTestConfiguration_BC_EQ_v3.0.0.xml"));
        var path = Paths.get(resource.getPath());
        return String.join("\n", Files.readAllLines(path)).trim().replaceFirst("^([\\W]+)<", "<");
    }

    @Test
    void mapSubstationToTSubstation_WhenCalledWithSubstation_ThenPropertiesMappedToTSubstation() {
        var expectedId = UUID.randomUUID().toString();
        var expectedDesc = "Some description";

        when(cgmesSubstation.getId()).thenReturn(expectedId);
        when(cgmesSubstation.getOptionalName()).thenReturn(Optional.of(expectedDesc));

        var sclSubstation = mapper.mapSubstationToTSubstation(cgmesSubstation, context);

        assertNotNull(sclSubstation);
        assertEquals(expectedId, sclSubstation.getName());
        assertEquals(expectedDesc, sclSubstation.getDesc());
        verify(cgmesSubstation, atLeastOnce()).getId();
        verify(cgmesSubstation, atLeastOnce()).getOptionalName();
        verify(context, times(1)).addLast(sclSubstation);
        verifyNoMoreInteractions(cgmesSubstation);
    }

    @Test
    void mapVoltageLevelToTVoltageLevel_WhenCalledWithVoltageLevel_ThenPropertiesMappedToTVoltageLevel() {
        var expectedName = "TheName";
        var expectedVoltage = BigDecimal.valueOf(100.0);

        when(cgmesVoltageLevel.getNameOrId()).thenReturn(expectedName);
        when(cgmesVoltageLevel.getNominalV()).thenReturn(expectedVoltage.doubleValue());

        var sclVoltageLevel = mapper.mapVoltageLevelToTVoltageLevel(cgmesVoltageLevel, context);

        assertNotNull(sclVoltageLevel);
        assertEquals(expectedName, sclVoltageLevel.getName());
        assertEquals(expectedVoltage, sclVoltageLevel.getVoltage().getValue());
        verify(cgmesVoltageLevel, atLeastOnce()).getId();
        verify(cgmesVoltageLevel, atLeastOnce()).getNameOrId();
        verify(cgmesVoltageLevel, atLeastOnce()).getNominalV();
        verify(context, times(1)).addLast(sclVoltageLevel);
        verifyNoMoreInteractions(cgmesVoltageLevel);
    }

    @Test
    void mapBayToTBay_WhenCalledWithCgmesBay_ThenPropertiesMappedToTBay() {
        var expectedName = "TheName";

        when(cgmesBay.getNameOrId()).thenReturn(expectedName);

        var sclBay = mapper.mapBayToTBay(cgmesBay, context);

        assertNotNull(sclBay);
        assertEquals(expectedName, sclBay.getName());
        verify(cgmesBay, atLeastOnce()).getId();
        verify(cgmesBay, atLeastOnce()).getNameOrId();
        verify(context, times(1)).addLast(sclBay);
        verifyNoMoreInteractions(cgmesBay);
    }

    @Test
    void mapConnectivityNodeToTConnectivityNode_WhenCalledWithCgmesConnectivityNode_ThenPropertiesMappedToTConnectivityNode() {
        var expectedName = "TheName";
        var expectedPathName = "ThePathName";

        when(cgmesConnectivityNode.getNameOrId()).thenReturn(expectedName);
        when(context.createPathName()).thenReturn(expectedPathName);

        var sclConnectivityNode = mapper.mapConnectivityNodeToTConnectivityNode(cgmesConnectivityNode, context);

        assertNotNull(sclConnectivityNode);
        assertEquals(expectedName, sclConnectivityNode.getName());
        assertEquals(expectedPathName, sclConnectivityNode.getPathName());
        verify(cgmesConnectivityNode, atLeastOnce()).getNameOrId();
        verify(context, times(1)).addLast(sclConnectivityNode);
        verifyNoMoreInteractions(cgmesConnectivityNode);
    }

    @Test
    void mapSwitchToTConductingEquipment_WhenCalledWithCgmesSwitch_ThenPropertiesMappedToTConductingEquipment() {
        var expectedName = "TheName";
        var expectedType = SwitchType.CBR.name();

        when(cgmesSwitch.getNameOrId()).thenReturn(expectedName);
        when(cgmesSwitch.getType()).thenReturn(SwitchType.CBR.getCimTypes().get(0));

        var sclConductingEquipment = mapper.mapSwitchToTConductingEquipment(cgmesSwitch, context);

        assertNotNull(sclConductingEquipment);
        assertEquals(expectedName, sclConductingEquipment.getName());
        assertEquals(expectedType, sclConductingEquipment.getType());
        verify(cgmesSwitch, atLeastOnce()).getNameOrId();
        verify(cgmesSwitch, atLeastOnce()).getType();
        verify(context, times(1)).addLast(sclConductingEquipment);
        verifyNoMoreInteractions(cgmesSwitch);
    }

    @Test
    void optionalString_WhenCalledWithFilledOptional_ThenStringValueReturned() {
        var expectedValue = "Some string";

        var value = mapper.optionalString(Optional.of(expectedValue));

        assertEquals(expectedValue, value);
    }

    @Test
    void optionalString_WhenCalledWithEmptyOptional_ThenBlankStringReturned() {
        var expectedValue = "";

        var value = mapper.optionalString(Optional.empty());

        assertEquals(expectedValue, value);
    }
}