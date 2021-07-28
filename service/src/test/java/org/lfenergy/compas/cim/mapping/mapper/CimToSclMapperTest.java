// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.mapper;

import com.powsybl.iidm.network.Substation;
import com.powsybl.iidm.network.VoltageLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.compas.cim.mapping.cgmes.CgmesCimReader;
import org.lfenergy.compas.cim.mapping.cgmes.CgmesDataValidator;
import org.lfenergy.compas.cim.mapping.model.CgmesBay;
import org.lfenergy.compas.cim.mapping.model.CgmesConnectivityNode;
import org.lfenergy.compas.cim.mapping.model.CimData;
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
    private Substation substation;
    @Mock
    private VoltageLevel voltageLevel;
    @Mock
    private CgmesBay bay;
    @Mock
    private CgmesConnectivityNode connectivityNode;

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
        var data = reader.readModel(List.of(cimData));

        var result = new SCL();
        mapper.map(new CimToSclMapperContext(data.getCgmesModel(), data.getNetwork(), result));

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

        when(substation.getId()).thenReturn(expectedId);
        when(substation.getOptionalName()).thenReturn(Optional.of(expectedDesc));
        when(substation.getVoltageLevelStream()).thenReturn(null);

        var sclSubstation = mapper.mapSubstationToTSubstation(substation, context);

        assertNotNull(sclSubstation);
        assertEquals(expectedId, sclSubstation.getName());
        assertEquals(expectedDesc, sclSubstation.getDesc());
        verify(substation, atLeastOnce()).getId();
        verify(substation, atLeastOnce()).getOptionalName();
        verify(substation, atLeastOnce()).getVoltageLevelStream();
        verify(context, times(1)).push(sclSubstation);
        verifyNoMoreInteractions(substation);
    }

    @Test
    void mapVoltageLevelToTVoltageLevel_WhenCalledWithVoltageLevel_ThenPropertiesMappedToTVoltageLevel() {
        var expectedName = "TheName";
        var expectedVoltage = BigDecimal.valueOf(100.0);

        when(voltageLevel.getNameOrId()).thenReturn(expectedName);
        when(voltageLevel.getNominalV()).thenReturn(expectedVoltage.doubleValue());

        var sclVoltageLevel = mapper.mapVoltageLevelToTVoltageLevel(voltageLevel, context);

        assertNotNull(sclVoltageLevel);
        assertEquals(expectedName, sclVoltageLevel.getName());
        assertEquals(expectedVoltage, sclVoltageLevel.getVoltage().getValue());
        verify(voltageLevel, atLeastOnce()).getId();
        verify(voltageLevel, atLeastOnce()).getNameOrId();
        verify(voltageLevel, atLeastOnce()).getNominalV();
        verify(context, times(1)).push(sclVoltageLevel);
        verifyNoMoreInteractions(voltageLevel);
    }

    @Test
    void mapBayToTBay_WhenCalledWithCgmesBay_ThenPropertiesMappedToTBay() {
        var expectedName = "TheName";

        when(bay.getNameOrId()).thenReturn(expectedName);

        var sclBay = mapper.mapBayToTBay(bay, context);

        assertNotNull(sclBay);
        assertEquals(expectedName, sclBay.getName());
        verify(bay, atLeastOnce()).getId();
        verify(bay, atLeastOnce()).getNameOrId();
        verify(context, times(1)).push(sclBay);
        verifyNoMoreInteractions(bay);
    }

    @Test
    void mapConnectivityNodeToTConnectivityNode_WhenCalledWithCgmesConnectivityNode_ThenPropertiesMappedToTConnectivityNode() {
        var expectedName = "TheName";
        var expectedPathName = "ThePathName";

        when(connectivityNode.getNameOrId()).thenReturn(expectedName);
        when(context.createPathName()).thenReturn(expectedPathName);

        var sclConnectivityNode = mapper.mapConnectivityNodeToTConnectivityNode(connectivityNode, context);

        assertNotNull(sclConnectivityNode);
        assertEquals(expectedName, sclConnectivityNode.getName());
        assertEquals(expectedPathName, sclConnectivityNode.getPathName());
        verify(connectivityNode, atLeastOnce()).getNameOrId();
        verify(context, times(1)).push(sclConnectivityNode);
        verifyNoMoreInteractions(connectivityNode);
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