// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.mapper;

import com.powsybl.cgmes.model.CgmesModel;
import com.powsybl.triplestore.api.PropertyBag;
import com.powsybl.triplestore.api.PropertyBags;
import com.powsybl.triplestore.api.TripleStore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.compas.scl2007b4.model.TConnectivityNode;
import org.lfenergy.compas.scl2007b4.model.TSubstation;
import org.lfenergy.compas.scl2007b4.model.TVoltageLevel;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CimToSclMapperContextTest {
    @Mock
    private CgmesModel cgmesModel;

    @InjectMocks
    private CimToSclMapperContext context;

    @Test
    void getSubstations_WhenCalled_ThenPropertyBagsIsConvertedToCgmesSubstation() {
        var substationId = "SubstationId";
        var substationName = "Name Substation";
        var bags = new PropertyBags();
        var bag = new PropertyBag(List.of("Substation", "name"));
        bag.put("Substation", substationId);
        bag.put("name", substationName);
        bags.add(bag);

        when(cgmesModel.substations()).thenReturn(bags);

        var result = context.getSubstations();
        assertNotNull(result);
        assertEquals(1, result.size());
        var substation = result.get(0);
        assertEquals(substationId, substation.getId());
        assertEquals(substationName, substation.getName());
    }

    @Test
    void getVoltageLevelsBySubstation_WhenCalledWithKnownId_ThenPropertyBagsIsFilteredOnIdAndConvertedToCgmesVoltageLevel() {
        var voltageLevelId = "VoltageLevelId";
        var voltageLevelName = "Name VoltageLevel";
        var substationId = "Known Substation ID";
        var bags = new PropertyBags();
        var bag = new PropertyBag(List.of("VoltageLevel", "name", "nominalVoltage", "Substation"));
        bag.put("VoltageLevel", voltageLevelId);
        bag.put("name", voltageLevelName);
        bag.put("nominalVoltage", "1.0");
        bag.put("Substation", substationId);
        bags.add(bag);

        bag = new PropertyBag(List.of("VoltageLevel", "name", "nominalVoltage", "Substation"));
        bag.put("VoltageLevel", "Other ID");
        bag.put("name", "Other Name");
        bag.put("nominalVoltage", "1.1");
        bag.put("Substation", "Unknown Container ID");
        bags.add(bag);

        when(cgmesModel.voltageLevels()).thenReturn(bags);

        var result = context.getVoltageLevelsBySubstation(substationId);
        assertNotNull(result);
        assertEquals(1, result.size());
        var voltageLevel = result.get(0);
        assertEquals(voltageLevelId, voltageLevel.getId());
        assertEquals(voltageLevelName, voltageLevel.getName());
    }

    @Test
    void getBaysByVoltageLevel_WhenSparQLReturnsBags_ThenPropertyBagIsConvertedToCgmesBay() {
        var bayId = "BayId";
        var bayName = "Name Bay";
        var bags = new PropertyBags();
        var bag = new PropertyBag(List.of("Bay", "name"));
        bag.put("Bay", bayId);
        bag.put("name", bayName);
        bags.add(bag);

        var tripleStore = mock(TripleStore.class);
        when(cgmesModel.tripleStore()).thenReturn(tripleStore);
        when(tripleStore.query(anyString())).thenReturn(bags);

        var result = context.getBaysByVoltageLevel("Random VoltageId");

        assertNotNull(result);
        assertEquals(1, result.size());
        var bay = result.get(0);
        assertEquals(bayId, bay.getId());
        assertEquals(bayName, bay.getName());
    }

    @Test
    void getTransformers_WhenSparQLReturnsBags_ThenPropertyBagIsConvertedToCgmesTransformer() {
        var pwId = "PowertransformerId";
        var pwName = "Name Powertransformer";
        var pwDesc = "Desc Powertransformer";
        var pwContainerId = "Known Container ID";
        var bags = new PropertyBags();
        var bag = new PropertyBag(List.of("PowerTransformer", "name", "description", "EquipmentContainer"));
        bag.put("PowerTransformer", pwId);
        bag.put("name", pwName);
        bag.put("description", pwDesc);
        bag.put("EquipmentContainer", pwContainerId);
        bags.add(bag);

        bag = new PropertyBag(List.of("PowerTransformer", "name", "description", "EquipmentContainer"));
        bag.put("PowerTransformer", "Other ID");
        bag.put("name", "Other name");
        bag.put("description", "Other desc");
        bag.put("EquipmentContainer", "Unknown Container ID");
        bags.add(bag);

        var tripleStore = mock(TripleStore.class);
        when(cgmesModel.tripleStore()).thenReturn(tripleStore);
        when(tripleStore.query(anyString())).thenReturn(bags);

        var result = context.getTransformers(pwContainerId);

        assertNotNull(result);
        assertEquals(1, result.size());
        var transformer = result.get(0);
        assertEquals(pwId, transformer.getId());
        assertEquals(pwName, transformer.getName());
        assertEquals(pwDesc, transformer.getDescription());
    }

    @Test
    void getConnectivityNode_WhenCalledWithKnownId_ThenPropertyBagsIsFilteredOnIdAndConvertedToCgmesConnectivityNode() {
        var ccnId = "CcnId";
        var ccnName = "Name Ccn";
        var ccnContainerId = "Known Container ID";
        var bags = new PropertyBags();
        var bag = new PropertyBag(List.of("ConnectivityNode", "name", "ConnectivityNodeContainer"));
        bag.put("ConnectivityNode", ccnId);
        bag.put("name", ccnName);
        bag.put("ConnectivityNodeContainer", ccnContainerId);
        bags.add(bag);

        bag = new PropertyBag(List.of("ConnectivityNode", "name", "ConnectivityNodeContainer"));
        bag.put("ConnectivityNode", "Other ID");
        bag.put("name", "Other Name");
        bag.put("ConnectivityNodeContainer", "Unknown Container ID");
        bags.add(bag);

        when(cgmesModel.connectivityNodes()).thenReturn(bags);

        var result = context.getConnectivityNode(ccnContainerId);
        assertNotNull(result);
        assertEquals(1, result.size());
        var ccn = result.get(0);
        assertEquals(ccnId, ccn.getId());
        assertEquals(ccnName, ccn.getName());
    }

    @Test
    void getSwitches_WhenCalledWithKnownId_ThenPropertyBagsIsFilteredOnIdAndConvertedToCgmesSwitch() {
        var switchId = "SwitchId";
        var switchName = "Name Switch";
        var containerId = "Known Container ID";
        var bags = new PropertyBags();
        var bag = new PropertyBag(List.of("Switch", "name", "type", "EquipmentContainer", "Terminal1", "Terminal2"));
        bag.put("Switch", switchId);
        bag.put("name", switchName);
        bag.put("type", "Breaker");
        bag.put("EquipmentContainer", containerId);
        bag.put("Terminal1", "Terminal1 ID");
        bag.put("Terminal2", "Terminal2 ID");
        bags.add(bag);

        bag = new PropertyBag(List.of("Switch", "name", "type", "EquipmentContainer", "Terminal1", "Terminal2"));
        bag.put("Switch", "Other ID");
        bag.put("name", "Other Name");
        bag.put("type", "Breaker");
        bag.put("EquipmentContainer", "Unknown Container ID");
        bags.add(bag);

        when(cgmesModel.switches()).thenReturn(bags);

        var result = context.getSwitches(containerId);
        assertNotNull(result);
        assertEquals(1, result.size());
        var switchEquipment = result.get(0);
        assertEquals(switchId, switchEquipment.getId());
        assertEquals(switchName, switchEquipment.getName());
    }

    @Test
    void getTerminals_WhenCalledWithKnownId_ThenPropertyBagsIsFilteredOnIdAndConvertedToCgmesTerminal() {
        var terminalId = "TerminalId";
        var terminalName = "Name Terminal";
        var ccnNode = "Connectivity Node ID";
        var containerId = "Known Container ID";
        var bags = new PropertyBags();
        var bag = new PropertyBag(List.of("Terminal", "name", "ConnectivityNode", "ConductingEquipment"));
        bag.put("Terminal", terminalId);
        bag.put("name", terminalName);
        bag.put("ConnectivityNode", ccnNode);
        bag.put("ConductingEquipment", containerId);
        bags.add(bag);

        bag = new PropertyBag(List.of("Terminal", "name", "ConnectivityNode", "ConductingEquipment"));
        bag.put("Terminal", "Other ID");
        bag.put("name", "Other Name");
        bag.put("ConnectivityNode", "Some Other ID");
        bag.put("ConductingEquipment", "Unknown Container ID");
        bags.add(bag);

        when(cgmesModel.terminals()).thenReturn(bags);

        var result = context.getTerminals(containerId);
        assertNotNull(result);
        assertEquals(1, result.size());
        var terminal = result.get(0);
        assertEquals(terminalId, terminal.getId());
        assertEquals(terminalName, terminal.getName());
        assertEquals(ccnNode, terminal.getConnectivityNodeId());
    }

    @Test
    void createPathName_WhenCalledWithNoStack_ThenEmptyStringIsReturned() {
        assertEquals("", context.createPathName());
    }

    @Test
    void createPathName_WhenCalledWithStack_ThenPathNameIsReturned() {
        var firstPartname = "Name 1";
        var tSubstation = new TSubstation();
        tSubstation.setName(firstPartname);
        context.addLast(tSubstation);

        var secondPartname = "Name 2";
        var tVoltageLevel = new TVoltageLevel();
        tVoltageLevel.setName(secondPartname);
        context.addLast(tVoltageLevel);

        // First look what is returned when both elements are on the stack.
        assertEquals(firstPartname + "/" + secondPartname, context.createPathName());

        // Now pop one from the stack and see what the name is then.
        context.removeLast();
        assertEquals(firstPartname, context.createPathName());
    }

    @Test
    void resetTConnectivityNodeMap_WhenCalledAfterRest_ThenPathNameOfCNCannotBeFoundAnymore() {
        var cnId = "CN ID";
        var cnPathName = "CN PATH NAME";
        var cn = new TConnectivityNode();
        cn.setPathName(cnPathName);

        context.saveTConnectivityNode(cnId, cn);
        context.saveTConnectivityNode("Other ID", new TConnectivityNode());

        context.resetTConnectivityNodeMap();
        var result = context.getPathnameFromConnectivityNode(cnId);

        assertFalse(result.isPresent());
    }

    @Test
    void getPathnameFromConnectivityNode_WhenCalledWithKnownId_ThenPathNameOfCNReturned() {
        var cnId = "CN ID";
        var cnPathName = "CN PATH NAME";
        var cn = new TConnectivityNode();
        cn.setPathName(cnPathName);

        context.saveTConnectivityNode(cnId, cn);
        context.saveTConnectivityNode("Other ID", new TConnectivityNode());
        var result = context.getPathnameFromConnectivityNode(cnId);

        assertTrue(result.isPresent());
        assertEquals(cnPathName, result.get());
    }

    @Test
    void getPathnameFromConnectivityNode_WhenCalledWithUnKnownId_ThenEmptyOptionalReturned() {
        var cnId = "CN ID";

        context.saveTConnectivityNode("Unknown ID", new TConnectivityNode());
        context.saveTConnectivityNode("Other ID", new TConnectivityNode());
        var result = context.getPathnameFromConnectivityNode(cnId);

        assertFalse(result.isPresent());
    }

    @Test
    void getNameFromConnectivityNode_WhenCalledWithKnownId_ThenPathNameOfCNReturned() {
        var cnId = "CN ID";
        var cnName = "CN NAME";
        var cn = new TConnectivityNode();
        cn.setName(cnName);

        context.saveTConnectivityNode(cnId, cn);
        context.saveTConnectivityNode("Other ID", new TConnectivityNode());
        var result = context.getNameFromConnectivityNode(cnId);

        assertTrue(result.isPresent());
        assertEquals(cnName, result.get());
    }

    @Test
    void getNameFromConnectivityNode_WhenCalledWithUnKnownId_ThenEmptyOptionalReturned() {
        var cnId = "CN ID";

        context.saveTConnectivityNode("Unknown ID", new TConnectivityNode());
        context.saveTConnectivityNode("Other ID", new TConnectivityNode());
        var result = context.getNameFromConnectivityNode(cnId);

        assertFalse(result.isPresent());
    }
}