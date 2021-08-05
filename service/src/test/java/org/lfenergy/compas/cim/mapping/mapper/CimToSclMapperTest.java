// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.compas.cim.mapping.cgmes.CgmesCimReader;
import org.lfenergy.compas.cim.mapping.model.*;
import org.lfenergy.compas.core.commons.ElementConverter;
import org.lfenergy.compas.scl2007b4.model.SCL;
import org.lfenergy.compas.scl2007b4.model.TConnectivityNode;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.lfenergy.compas.cim.mapping.CimMappingConstants.RDF_NS_URI;
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
    @Mock
    private CgmesTerminal cgmesTerminal;

    private CimToSclMapper mapper;

    @BeforeEach
    void setup() {
        mapper = Mappers.getMapper(CimToSclMapper.class);
    }

    @Test
    void map_WhenWithCimData_ThenSclMapped() throws IOException {
        // This is a overall test to see the whole mapping working with a test CIM File.
        var converter = new ElementConverter();
        var reader = new CgmesCimReader(converter);
        var cimData = new CimData();
        cimData.setName("MiniGridTestConfiguration_BC_EQ_v3.0.0.xml");
        cimData.setRdf(
                List.of(converter.convertToElement(readFile(), "RDF", RDF_NS_URI)));
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

        assertEquals(4, bay.getConnectivityNode().size());
        var connectivityNode = bay.getConnectivityNode().get(0);
        assertEquals("CONNECTIVITY_NODE82", connectivityNode.getName());
        assertEquals("_af9a4ae3-ba2e-4c34-8e47-5af894ee20f4/S1 380kV/BAY_T4_2/CONNECTIVITY_NODE82", connectivityNode.getPathName());

        assertEquals(3, bay.getConductingEquipment().size());
        var conductingEquipment = bay.getConductingEquipment().get(0);
        assertEquals("BREAKER25", conductingEquipment.getName());
        assertEquals("CBR", conductingEquipment.getType());

        assertEquals(2, conductingEquipment.getTerminal().size());
        var terminal = conductingEquipment.getTerminal().get(0);
        assertEquals("T4_2_ADDB1", terminal.getName());
        assertEquals("CONNECTIVITY_NODE83", terminal.getCNodeName());
        assertEquals("_af9a4ae3-ba2e-4c34-8e47-5af894ee20f4/S1 380kV/BAY_T4_2/CONNECTIVITY_NODE83", terminal.getConnectivityNode());
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
        verify(cgmesSubstation, times(2)).getId();
        verify(cgmesSubstation, times(1)).getOptionalName();
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
        verify(cgmesVoltageLevel, times(1)).getId();
        verify(cgmesVoltageLevel, times(1)).getNameOrId();
        verify(cgmesVoltageLevel, times(1)).getNominalV();
        verify(context, times(1)).addLast(sclVoltageLevel);
        verifyNoMoreInteractions(cgmesVoltageLevel);
    }

    @Test
    void mapBayToTBay_WhenCalledWithCgmesBay_ThenPropertiesMappedToTBay() {
        var expectedName = "TheName";

        when(cgmesBay.getNameOrId()).thenReturn(expectedName);

        var sclBay = mapper.mapBayToTBay(cgmesBay, cgmesVoltageLevel, context);

        assertNotNull(sclBay);
        assertEquals(expectedName, sclBay.getName());
        verify(cgmesBay, times(2)).getId();
        verify(cgmesBay, times(1)).getNameOrId();
        verify(context, times(1)).addLast(sclBay);
        verifyNoMoreInteractions(cgmesBay);
    }

    @Test
    void mapConnectivityNodeToTConnectivityNode_WhenCalledWithCgmesConnectivityNode_ThenPropertiesMappedToTConnectivityNode() {
        var expectedId = "Id";
        var expectedName = "TheName";
        var expectedPathName = "ThePathName";

        when(cgmesConnectivityNode.getId()).thenReturn(expectedId);
        when(cgmesConnectivityNode.getNameOrId()).thenReturn(expectedName);
        when(context.createPathName()).thenReturn(expectedPathName);

        var sclConnectivityNode = mapper.mapConnectivityNodeToTConnectivityNode(cgmesConnectivityNode, context);

        assertNotNull(sclConnectivityNode);
        assertEquals(expectedName, sclConnectivityNode.getName());
        assertEquals(expectedPathName, sclConnectivityNode.getPathName());
        verify(cgmesConnectivityNode, times(1)).getId();
        verify(cgmesConnectivityNode, times(1)).getNameOrId();
        verify(context, times(1)).addLast(sclConnectivityNode);
        verify(context, times(1)).saveTConnectivityNode(eq(expectedId), any(TConnectivityNode.class));
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
        verify(cgmesSwitch, times(1)).getId();
        verify(cgmesSwitch, times(1)).getNameOrId();
        verify(cgmesSwitch, times(1)).getType();
        verify(context, times(1)).addLast(sclConductingEquipment);
        verifyNoMoreInteractions(cgmesSwitch);
    }

    @Test
    void mapTerminalToTTerminal_WhenCalledWithCgmesTerminal_ThenPropertiesMappedToTTerminal() {
        var expectedName = "TheName";
        var expectedConnectivityNode = "CN ID";
        var expectedCNPathName = "CN Pathname";
        var expectedCNodeName = "CN Name";

        when(cgmesTerminal.getNameOrId()).thenReturn(expectedName);
        when(cgmesTerminal.getConnectivityNodeId()).thenReturn(expectedConnectivityNode);
        when(context.getPathnameFromConnectivityNode(expectedConnectivityNode)).thenReturn(Optional.of(expectedCNPathName));
        when(context.getNameFromConnectivityNode(expectedConnectivityNode)).thenReturn(Optional.of(expectedCNodeName));

        var sclTerminal = mapper.mapTerminalToTTerminal(cgmesTerminal, context);

        assertNotNull(sclTerminal);
        assertEquals(expectedName, sclTerminal.getName());
        assertEquals(expectedCNPathName, sclTerminal.getConnectivityNode());
        assertEquals(expectedCNodeName, sclTerminal.getCNodeName());
        verify(cgmesTerminal, times(1)).getNameOrId();
        verify(cgmesTerminal, times(2)).getConnectivityNodeId();
        verify(context, times(1)).getPathnameFromConnectivityNode(expectedConnectivityNode);
        verify(context, times(1)).getNameFromConnectivityNode(expectedConnectivityNode);
        verifyNoMoreInteractions(cgmesTerminal, context);

    }

    @Test
    void optionalString_WhenCalledWithFilledOptional_ThenStringValueReturned() {
        var expectedValue = "Some string";

        var value = mapper.optionalString(Optional.of(expectedValue));

        assertEquals(expectedValue, value);
    }

    @Test
    void optionalString_WhenCalledWithEmptyOptional_ThenBlankStringReturned() {
        var value = mapper.optionalString(Optional.empty());

        assertNull(value);
    }
}