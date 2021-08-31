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
import org.lfenergy.compas.scl2007b4.model.*;
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
import static org.lfenergy.compas.cim.mapping.CimMappingConstants.DC_LINE_SEGMENT_TYPE;
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
    private CgmesTransformer cgmesTransformer;
    @Mock
    private CgmesTransformerEnd cgmesTransformerEnd;
    @Mock
    private CgmesTapChanger cgmesTapChanger;
    @Mock
    private CgmesConnectivityNode cgmesConnectivityNode;
    @Mock
    private CgmesSwitch cgmesSwitch;
    @Mock
    private CgmesTerminal cgmesTerminal;

    @Mock
    private TVoltageLevel tVoltageLevel;

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

        assertEquals(2, substation.getPowerTransformer().size());
        var powerTransformer = substation.getPowerTransformer().get(1);
        assertPowerTransformer(powerTransformer);

        assertEquals(3, powerTransformer.getTransformerWinding().size());
        var transformerWinding = powerTransformer.getTransformerWinding().get(0);
        assertTransformerWinding(transformerWinding);
        assertTapChanger(transformerWinding.getTapChanger());
        assertTerminal(transformerWinding.getTerminal(), 1, "T3_0", "CONNECTIVITY_NODE88",
                "_af9a4ae3-ba2e-4c34-8e47-5af894ee20f4/S1 380kV/BAY_T3_0/CONNECTIVITY_NODE88");

        assertEquals(3, substation.getVoltageLevel().size());
        var voltageLevel = substation.getVoltageLevel().get(0);
        assertEquals("S1 380kV", voltageLevel.getName());
        assertNotNull(voltageLevel.getVoltage());
        assertNull(voltageLevel.getNomFreq());
        assertEquals(BigDecimal.valueOf(380.0), voltageLevel.getVoltage().getValue());
        assertEquals("k", voltageLevel.getVoltage().getMultiplier());
        assertEquals("V", voltageLevel.getVoltage().getUnit());

        assertEquals(3, voltageLevel.getBay().size());
        var bay = voltageLevel.getBay().get(0);
        assertEquals("BAY_T4_2", bay.getName());

        assertEquals(4, bay.getConnectivityNode().size());
        assertConnectivityNode(bay.getConnectivityNode().get(0));

        assertEquals(3, bay.getConductingEquipment().size());
        var conductingEquipment = bay.getConductingEquipment().get(0);
        assertConductingEquipment(conductingEquipment);

        assertTerminal(conductingEquipment.getTerminal(), 2, "T4_2_ADDB1", "CONNECTIVITY_NODE83",
                "_af9a4ae3-ba2e-4c34-8e47-5af894ee20f4/S1 380kV/BAY_T4_2/CONNECTIVITY_NODE83");
    }

    private void assertPowerTransformer(TPowerTransformer powerTransformer) {
        assertNotNull(powerTransformer);
        assertEquals("T3", powerTransformer.getName());
        assertEquals(TPowerTransformerEnum.PTR, powerTransformer.getType());
        assertEquals("Trafo-5", powerTransformer.getDesc());
    }

    private void assertTransformerWinding(TTransformerWinding powerTransformerEnd) {
        assertNotNull(powerTransformerEnd);
        assertEquals("T3", powerTransformerEnd.getName());
        assertEquals(TTransformerWindingEnum.PTW, powerTransformerEnd.getType());
    }

    private void assertConnectivityNode(TConnectivityNode connectivityNode) {
        assertNotNull(connectivityNode);
        assertEquals("CONNECTIVITY_NODE82", connectivityNode.getName());
        assertEquals("_af9a4ae3-ba2e-4c34-8e47-5af894ee20f4/S1 380kV/BAY_T4_2/CONNECTIVITY_NODE82", connectivityNode.getPathName());
    }

    private void assertConductingEquipment(TConductingEquipment conductingEquipment) {
        assertNotNull(conductingEquipment);
        assertEquals("BREAKER25", conductingEquipment.getName());
        assertEquals("CBR", conductingEquipment.getType());
    }

    private void assertTapChanger(TTapChanger tapChanger) {
        assertNotNull(tapChanger);
        assertEquals("T3", tapChanger.getName());
        assertEquals("LTC", tapChanger.getType());
    }

    private void assertTerminal(List<TTerminal> terminals, int size, String name, String nodeName, String connectivityNode) {
        assertEquals(size, terminals.size());
        var terminal = terminals.get(0);
        assertEquals(name, terminal.getName());
        assertEquals(nodeName, terminal.getCNodeName());
        assertEquals(connectivityNode, terminal.getConnectivityNode());
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
        when(cgmesSubstation.getName()).thenReturn(expectedDesc);

        var sclSubstation = mapper.mapSubstationToTSubstation(cgmesSubstation, context);

        assertNotNull(sclSubstation);
        assertEquals(expectedId, sclSubstation.getName());
        assertEquals(expectedDesc, sclSubstation.getDesc());
        verify(cgmesSubstation, times(3)).getId();
        verify(cgmesSubstation, times(1)).getName();
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
        verify(cgmesVoltageLevel, times(2)).getId();
        verify(cgmesVoltageLevel, times(1)).getNameOrId();
        verify(cgmesVoltageLevel, times(1)).getNominalV();
        verify(context, times(1)).addLast(sclVoltageLevel);
        verifyNoMoreInteractions(cgmesVoltageLevel);
    }

    @Test
    void mapBayToTBay_WhenCalledWithCgmesBay_ThenPropertiesMappedToTBay() {
        var expectedName = "TheName";

        when(cgmesBay.getNameOrId()).thenReturn(expectedName);

        var sclBay = mapper.mapBayToTBay(cgmesBay, cgmesVoltageLevel, tVoltageLevel, context);

        assertNotNull(sclBay);
        assertEquals(expectedName, sclBay.getName());
        verify(cgmesBay, times(3)).getId();
        verify(cgmesBay, times(1)).getNameOrId();
        verify(context, times(1)).addLast(sclBay);
        verifyNoMoreInteractions(cgmesBay);
    }

    @Test
    void mapTransformerToTPowerTransformer_WhenCalledWithCgmesTransformer_ThenPropertiesMappedToTPowerTransformer() {
        var expectedName = "TheName";
        var expectedDesc = "Desc";

        when(cgmesTransformer.getNameOrId()).thenReturn(expectedName);
        when(cgmesTransformer.getDescription()).thenReturn(expectedDesc);

        var sclPowerTransformer = mapper.mapTransformerToTPowerTransformer(cgmesTransformer, context);

        assertNotNull(sclPowerTransformer);
        assertEquals(expectedName, sclPowerTransformer.getName());
        assertEquals(expectedDesc, sclPowerTransformer.getDesc());
        assertEquals(TPowerTransformerEnum.PTR, sclPowerTransformer.getType());
        verify(cgmesTransformer, times(1)).getId();
        verify(cgmesTransformer, times(1)).getNameOrId();
        verify(cgmesTransformer, times(1)).getDescription();
        verifyNoMoreInteractions(cgmesTransformer);
    }

    @Test
    void mapTransformerEndToTTransformerWinding_WhenCalledWithCgmesTransformerEnd_ThenPropertiesMappedToTTransformerWinding() {
        var expectedName = "TheName";

        when(cgmesTransformerEnd.getNameOrId()).thenReturn(expectedName);

        var sclTransformerWinding = mapper.mapTransformerEndToTTransformerWinding(cgmesTransformerEnd, context);

        assertNotNull(sclTransformerWinding);
        assertEquals(expectedName, sclTransformerWinding.getName());
        assertEquals(TTransformerWindingEnum.PTW, sclTransformerWinding.getType());
        verify(cgmesTransformerEnd, times(1)).getId();
        verify(cgmesTransformerEnd, times(1)).getNameOrId();
        verify(cgmesTransformerEnd, times(1)).getTerminalId();
        verifyNoMoreInteractions(cgmesTransformerEnd);
    }

    @Test
    void mapTapChangerToTTapChanger_WhenCalledWithCgmesTapChanger_ThenPropertiesMappedToTTapChanger() {
        var expectedName = "TheName";

        when(cgmesTapChanger.getNameOrId()).thenReturn(expectedName);

        var sclTapChanger = mapper.mapTapChangerToTTapChanger(cgmesTapChanger, context);

        assertNotNull(sclTapChanger);
        assertEquals(expectedName, sclTapChanger.getName());
        assertEquals("LTC", sclTapChanger.getType());
        verify(cgmesTapChanger, times(1)).getNameOrId();
        verifyNoMoreInteractions(cgmesTapChanger);
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
    void mapSwitchToTConductingEquipment_WhenCalledWithCgmesSwitchOtherType_ThenPropertiesMappedToTConductingEquipment() {
        var expectedName = "TheName";
        var expectedType = SwitchType.CBR.name();

        when(cgmesSwitch.getNameOrId()).thenReturn(expectedName);
        when(cgmesSwitch.getType()).thenReturn(SwitchType.CBR.getCimTypes().get(0));

        var sclConductingEquipment = mapper.mapSwitchToTConductingEquipment(cgmesSwitch, tVoltageLevel, context);

        assertNotNull(sclConductingEquipment);
        assertEquals(expectedName, sclConductingEquipment.getName());
        assertEquals(expectedType, sclConductingEquipment.getType());
        verify(cgmesSwitch, times(1)).getId();
        verify(cgmesSwitch, times(1)).getNameOrId();
        verify(cgmesSwitch, times(2)).getType();
        verify(tVoltageLevel, never()).setNomFreq(any(BigDecimal.class));
        verify(context, times(1)).addLast(sclConductingEquipment);
        verifyNoMoreInteractions(cgmesSwitch);
    }

    @Test
    void mapSwitchToTConductingEquipment_WhenCalledWithCgmesSwitchDCLineSegment_ThenPropertiesMappedToTConductingEquipment() {
        var expectedName = "TheName";
        var expectedType = SwitchType.CAB.name();

        when(cgmesSwitch.getNameOrId()).thenReturn(expectedName);
        when(cgmesSwitch.getType()).thenReturn(DC_LINE_SEGMENT_TYPE);

        var sclConductingEquipment = mapper.mapSwitchToTConductingEquipment(cgmesSwitch, tVoltageLevel, context);

        assertNotNull(sclConductingEquipment);
        assertEquals(expectedName, sclConductingEquipment.getName());
        assertEquals(expectedType, sclConductingEquipment.getType());
        verify(cgmesSwitch, times(1)).getId();
        verify(cgmesSwitch, times(1)).getNameOrId();
        verify(cgmesSwitch, times(2)).getType();
        verify(tVoltageLevel, times(1)).setNomFreq(BigDecimal.ZERO);
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
}