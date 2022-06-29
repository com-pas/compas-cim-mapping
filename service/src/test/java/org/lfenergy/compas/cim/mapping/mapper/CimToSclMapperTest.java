// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.compas.cim.mapping.cgmes.CgmesCimReader;
import org.lfenergy.compas.cim.mapping.model.*;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CimToSclMapperTest {
    @Mock
    private CimToSclMapperContext context;

    private CimToSclMapper mapper;

    @BeforeEach
    void setup() {
        mapper = Mappers.getMapper(CimToSclMapper.class);
    }

    @Test
    void map_WhenWithCimData_ThenSclMapped() throws IOException {
        // This is an overall test to see the whole mapping working with a test CIM File.
        var reader = new CgmesCimReader();
        var cimData = new CimData();
        cimData.setName("MiniGridTestConfiguration_BC_EQ_v3.0.0.xml");
        cimData.setRdfData(readFile());
        var cgmesModel = reader.readModel(List.of(cimData));

        var result = new SCL();
        mapper.mapToScl(result, new CimToSclMapperContext(cgmesModel));

        assertNotNull(result);

        assertEquals(5, result.getSubstation().size());
        var substation = result.getSubstation().get(0);
        assertSubstation(substation);

        assertEquals(2, substation.getPowerTransformer().size());
        var powerTransformer = substation.getPowerTransformer().get(1);
        assertPowerTransformer(powerTransformer);

        assertEquals(3, powerTransformer.getTransformerWinding().size());
        var transformerWinding = powerTransformer.getTransformerWinding().get(0);
        assertTransformerWinding(transformerWinding);
        assertTapChanger(transformerWinding.getTapChanger());
        assertTerminal(transformerWinding.getTerminal(), 1, "T3_0", "CONNECTIVITY_NODE88",
                "af9a4ae3-ba2e-4c34-8e47-5af894ee20f4/S1 380kV/BAY_T3_0/CONNECTIVITY_NODE88");

        assertEquals(3, substation.getVoltageLevel().size());
        var voltageLevel = substation.getVoltageLevel().get(0);
        assertVoltageLevel(voltageLevel);

        assertEquals(4, voltageLevel.getBay().size());
        // There is one busbarSection converted to a bay, this will be the first entry.
        var busbarSection = voltageLevel.getBay().get(0);
        assertBay(busbarSection, "BUSBAR10", 1, 0);
        assertConnectivityNode(busbarSection.getConnectivityNode().get(0), "CONNECTIVITY_NODE82",
                "af9a4ae3-ba2e-4c34-8e47-5af894ee20f4/S1 380kV/BUSBAR10/CONNECTIVITY_NODE82");

        // The others bay are actual bays from CIM.
        var bay = voltageLevel.getBay().get(1);
        assertBay(bay, "BAY_T4_2", 3, 3);

        assertConnectivityNode(bay.getConnectivityNode().get(0), "CONNECTIVITY_NODE83",
                "af9a4ae3-ba2e-4c34-8e47-5af894ee20f4/S1 380kV/BAY_T4_2/CONNECTIVITY_NODE83");

        var conductingEquipment = bay.getConductingEquipment().get(0);
        assertConductingEquipment(conductingEquipment);

        assertTerminal(conductingEquipment.getTerminal(), 2, "T4_2_ADDB1", "CONNECTIVITY_NODE83",
                "af9a4ae3-ba2e-4c34-8e47-5af894ee20f4/S1 380kV/BAY_T4_2/CONNECTIVITY_NODE83");
    }

    private void assertBay(TBay tBay, String name, int numberOfConnectivityNodes, int numberOfConductionEquipment) {
        assertEquals(name, tBay.getName());
        assertEquals(numberOfConnectivityNodes, tBay.getConnectivityNode().size());
        assertEquals(numberOfConductionEquipment, tBay.getConductingEquipment().size());
    }

    private void assertSubstation(TSubstation substation) {
        assertEquals("af9a4ae3-ba2e-4c34-8e47-5af894ee20f4", substation.getName());
        assertEquals("Sub1", substation.getDesc());
    }

    private void assertPowerTransformer(TPowerTransformer powerTransformer) {
        assertNotNull(powerTransformer);
        assertEquals("T3", powerTransformer.getName());
        assertEquals(TPowerTransformerEnum.PTR, powerTransformer.getType());
        assertEquals("Trafo-5", powerTransformer.getDesc());
    }

    private void assertTransformerWinding(TTransformerWinding powerTransformerEnd) {
        assertNotNull(powerTransformerEnd);
        assertEquals("T3_1", powerTransformerEnd.getName());
        assertEquals(TTransformerWindingEnum.PTW, powerTransformerEnd.getType());
    }

    private void assertVoltageLevel(TVoltageLevel voltageLevel) {
        assertEquals("S1 380kV", voltageLevel.getName());
        assertNotNull(voltageLevel.getVoltage());
        assertNull(voltageLevel.getNomFreq());
        assertEquals(BigDecimal.valueOf(380.0), voltageLevel.getVoltage().getValue());
        assertEquals("k", voltageLevel.getVoltage().getMultiplier());
        assertEquals("V", voltageLevel.getVoltage().getUnit());
    }

    private void assertConnectivityNode(TConnectivityNode connectivityNode, String name, String pathName) {
        assertNotNull(connectivityNode);
        assertEquals(name, connectivityNode.getName());
        assertEquals(pathName, connectivityNode.getPathName());
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
        var id = UUID.randomUUID().toString();
        var name = "Some description";
        var cgmesSubstation = new CgmesSubstation(id, name);

        var sclSubstation = mapper.mapSubstationToTSubstation(cgmesSubstation, context);

        assertNotNull(sclSubstation);
        assertEquals(id, sclSubstation.getName());
        assertEquals(name, sclSubstation.getDesc());
        verify(context, times(1)).addLast(sclSubstation);
    }

    @Test
    void mapVoltageLevelToTVoltageLevel_WhenCalledWithVoltageLevel_ThenPropertiesMappedToTVoltageLevel() {
        var cgmesVoltageLevel = createCgmesVoltageLevel();

        var sclVoltageLevel = mapper.mapVoltageLevelToTVoltageLevel(cgmesVoltageLevel, context);

        assertNotNull(sclVoltageLevel);
        assertEquals(cgmesVoltageLevel.name(), sclVoltageLevel.getName());
        assertEquals(cgmesVoltageLevel.nominalV(), sclVoltageLevel.getVoltage().getValue().doubleValue());
        verify(context, times(1)).addLast(sclVoltageLevel);
    }

    private CgmesVoltageLevel createCgmesVoltageLevel() {
        var id = UUID.randomUUID().toString();
        var name = "TheVLName";
        var voltage = BigDecimal.valueOf(100.0);
        return new CgmesVoltageLevel(id, name, voltage.doubleValue());
    }

    @Test
    void mapBusbarSectionBayToTBay_WhenCalledWithCgmesBusbarSection_ThenPropertiesMappedToTBay() {
        var id = UUID.randomUUID().toString();
        var name = "TheBSName";
        var cgmesBusbarSection = new CgmesBusbarSection(id, name);
        var cgmesVoltageLevel = createCgmesVoltageLevel();
        var tVoltageLevel = new TVoltageLevel();

        var sclBay = mapper.mapBusbarSectionBayToTBay(cgmesBusbarSection, cgmesVoltageLevel, tVoltageLevel, context);

        assertNotNull(sclBay);
        assertEquals(cgmesBusbarSection.name(), sclBay.getName());
        verify(context, times(1)).addLast(sclBay);
    }

    @Test
    void mapBayToTBay_WhenCalledWithCgmesBay_ThenPropertiesMappedToTBay() {
        var id = UUID.randomUUID().toString();
        var name = "TheBayName";
        var cgmesBay = new CgmesBay(id, name);
        var cgmesVoltageLevel = createCgmesVoltageLevel();
        var tVoltageLevel = new TVoltageLevel();

        var sclBay = mapper.mapBayToTBay(cgmesBay, cgmesVoltageLevel, tVoltageLevel, context);

        assertNotNull(sclBay);
        assertEquals(cgmesBay.name(), sclBay.getName());
        verify(context, times(1)).addLast(sclBay);
    }

    @Test
    void mapTransformerToTPowerTransformer_WhenCalledWithCgmesTransformer_ThenPropertiesMappedToTPowerTransformer() {
        var id = UUID.randomUUID().toString();
        var name = "TheName";
        var desc = "Desc";
        var cgmesTransformer = new CgmesTransformer(id, name, desc);

        var sclPowerTransformer = mapper.mapTransformerToTPowerTransformer(cgmesTransformer, context);

        assertNotNull(sclPowerTransformer);
        assertEquals(cgmesTransformer.name(), sclPowerTransformer.getName());
        assertEquals(cgmesTransformer.description(), sclPowerTransformer.getDesc());
        assertEquals(TPowerTransformerEnum.PTR, sclPowerTransformer.getType());
    }

    @Test
    void mapTransformerEndToTTransformerWinding_WhenCalledWithCgmesTransformerEnd_ThenPropertiesMappedToTTransformerWinding() {
        var id = UUID.randomUUID().toString();
        var name = "TheName";
        var desc = "Desc";
        var terminalId = "12345";
        var endNumber = "1";
        var cgmesTransformerEnd = new CgmesTransformerEnd(id, name, terminalId, endNumber);

        var sclTransformerWinding = mapper.mapTransformerEndToTTransformerWinding(cgmesTransformerEnd, context);

        assertNotNull(sclTransformerWinding);
        assertEquals(cgmesTransformerEnd.getUniqueName(), sclTransformerWinding.getName());
        assertEquals(TTransformerWindingEnum.PTW, sclTransformerWinding.getType());
    }

    @Test
    void mapTapChangerToTTapChanger_WhenCalledWithCgmesTapChanger_ThenPropertiesMappedToTTapChanger() {
        var id = UUID.randomUUID().toString();
        var name = "TheName";
        var cgmesTapChanger = new CgmesTapChanger(id, name);

        var sclTapChanger = mapper.mapTapChangerToTTapChanger(cgmesTapChanger, context);

        assertNotNull(sclTapChanger);
        assertEquals(cgmesTapChanger.name(), sclTapChanger.getName());
        assertEquals("LTC", sclTapChanger.getType());
    }

    @Test
    void mapConnectivityNodeToTConnectivityNode_WhenCalledWithCgmesConnectivityNode_ThenPropertiesMappedToTConnectivityNode() {
        var id = UUID.randomUUID().toString();
        var name = "TheName";
        var pathName = "ThePathName";
        var cgmesConnectivityNode = new CgmesConnectivityNode(id, name);

        when(context.createPathName()).thenReturn(pathName);

        var sclConnectivityNode = mapper.mapConnectivityNodeToTConnectivityNode(cgmesConnectivityNode, context);

        assertNotNull(sclConnectivityNode);
        assertEquals(cgmesConnectivityNode.name(), sclConnectivityNode.getName());
        assertEquals(pathName, sclConnectivityNode.getPathName());
        verify(context, times(1)).addLast(sclConnectivityNode);
        verify(context, times(1)).saveTConnectivityNode(eq(id), any(TConnectivityNode.class));
    }

    @Test
    void mapSwitchToTConductingEquipment_WhenCalledWithCgmesSwitchOtherType_ThenPropertiesMappedToTConductingEquipment() {
        var id = UUID.randomUUID().toString();
        var name = "TheName";
        var type = "Breaker";
        var cgmesSwitch = new CgmesSwitch(id, name, type);

        var tVoltageLevel = new TVoltageLevel();
        tVoltageLevel.setNomFreq(BigDecimal.ONE);

        var sclConductingEquipment = mapper.mapSwitchToTConductingEquipment(cgmesSwitch, tVoltageLevel, context);

        assertNotNull(sclConductingEquipment);
        assertEquals(cgmesSwitch.name(), sclConductingEquipment.getName());
        assertEquals(SwitchType.convertSwitchType(type).name(), sclConductingEquipment.getType());
        assertEquals(BigDecimal.ONE, tVoltageLevel.getNomFreq());
        verify(context, times(1)).addLast(sclConductingEquipment);
    }

    @Test
    void mapSwitchToTConductingEquipment_WhenCalledWithCgmesSwitchDCLineSegment_ThenPropertiesMappedToTConductingEquipment() {
        var id = UUID.randomUUID().toString();
        var name = "TheName";
        var type = DC_LINE_SEGMENT_TYPE;
        var cgmesSwitch = new CgmesSwitch(id, name, type);

        var tVoltageLevel = new TVoltageLevel();
        tVoltageLevel.setNomFreq(BigDecimal.ONE);

        var sclConductingEquipment = mapper.mapSwitchToTConductingEquipment(cgmesSwitch, tVoltageLevel, context);

        assertNotNull(sclConductingEquipment);
        assertEquals(cgmesSwitch.name(), sclConductingEquipment.getName());
        assertEquals(SwitchType.convertSwitchType(type).name(), sclConductingEquipment.getType());
        assertEquals(BigDecimal.ZERO, tVoltageLevel.getNomFreq());
        verify(context, times(1)).addLast(sclConductingEquipment);
    }

    @Test
    void mapTerminalToTTerminal_WhenCalledWithCgmesTerminal_ThenPropertiesMappedToTTerminal() {
        var id = UUID.randomUUID().toString();
        var name = "TheName";
        var connectivityNode = "CN ID";
        var cgmesTerminal = new CgmesTerminal(id, name, connectivityNode);

        var expectedCNPathName = "CN Pathname";
        var expectedCNodeName = "CN Name";
        when(context.getPathnameFromConnectivityNode(connectivityNode)).thenReturn(Optional.of(expectedCNPathName));
        when(context.getNameFromConnectivityNode(connectivityNode)).thenReturn(Optional.of(expectedCNodeName));

        var sclTerminal = mapper.mapTerminalToTTerminal(cgmesTerminal, context);

        assertNotNull(sclTerminal);
        assertEquals(cgmesTerminal.name(), sclTerminal.getName());
        assertEquals(expectedCNPathName, sclTerminal.getConnectivityNode());
        assertEquals(expectedCNodeName, sclTerminal.getCNodeName());
        verify(context, times(1)).getPathnameFromConnectivityNode(connectivityNode);
        verify(context, times(1)).getNameFromConnectivityNode(connectivityNode);
    }
}