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
import static org.lfenergy.compas.cim.mapping.mapper.CimToSclMapperContext.*;
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
        var bag = new PropertyBag(List.of(SUBSTATION_PROP, NAME_PROP));
        bag.put(SUBSTATION_PROP, substationId);
        bag.put(NAME_PROP, substationName);
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
        var bag = new PropertyBag(List.of(VOLTAGE_LEVEL_PROP, NAME_PROP, NOMINAL_VOLTAGE_PROP, SUBSTATION_PROP));
        bag.put(VOLTAGE_LEVEL_PROP, voltageLevelId);
        bag.put(NAME_PROP, voltageLevelName);
        bag.put(NOMINAL_VOLTAGE_PROP, "1.0");
        bag.put(SUBSTATION_PROP, substationId);
        bags.add(bag);

        bag = new PropertyBag(List.of(VOLTAGE_LEVEL_PROP, NAME_PROP, NOMINAL_VOLTAGE_PROP, SUBSTATION_PROP));
        bag.put(VOLTAGE_LEVEL_PROP, "Other ID");
        bag.put(NAME_PROP, "Other Name");
        bag.put(NOMINAL_VOLTAGE_PROP, "1.1");
        bag.put(SUBSTATION_PROP, "Unknown Container ID");
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
        var bag = new PropertyBag(List.of(BAY_PROP, NAME_PROP));
        bag.put(BAY_PROP, bayId);
        bag.put(NAME_PROP, bayName);
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
        var bag = new PropertyBag(List.of(POWER_TRANSFORMER_PROP, NAME_PROP, DESCRIPTION_PROP, EQUIPMENT_CONTAINER_PROP));
        bag.put(POWER_TRANSFORMER_PROP, pwId);
        bag.put(NAME_PROP, pwName);
        bag.put(DESCRIPTION_PROP, pwDesc);
        bag.put(EQUIPMENT_CONTAINER_PROP, pwContainerId);
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
    void getTransformerEnds_WhenCalledWithKnownId_ThenPropertyBagsIsFilteredOnIdAndConvertedToCgmesTransformerEnd() {
        var tfeId = "TfeId";
        var tfeName = "Name Tfe";
        var terminalId = "Known Terminal ID";
        var tfId = "Known Transformer ID";
        var endNumber = "1";
        var bags = new PropertyBags();
        var bag = new PropertyBag(List.of(TRANSFORMER_END_PROP, NAME_PROP, POWER_TRANSFORMER_PROP, TERMINAL_PROP, ENDNUMBER_PROP));
        bag.put(TRANSFORMER_END_PROP, tfeId);
        bag.put(NAME_PROP, tfeName);
        bag.put(POWER_TRANSFORMER_PROP, tfId);
        bag.put(TERMINAL_PROP, terminalId);
        bag.put(ENDNUMBER_PROP, endNumber);
        bags.add(bag);

        bag = new PropertyBag(List.of(TRANSFORMER_END_PROP, NAME_PROP, POWER_TRANSFORMER_PROP, TERMINAL_PROP));
        bag.put(TRANSFORMER_END_PROP, "Other ID");
        bag.put(NAME_PROP, "Other Name");
        bag.put(POWER_TRANSFORMER_PROP, "Unknown Transformer ID");
        bag.put(TERMINAL_PROP, "Other Terminal ID");
        bag.put(ENDNUMBER_PROP, "2");
        bags.add(bag);

        when(cgmesModel.transformerEnds()).thenReturn(bags);

        var result = context.getTransformerEnds(tfId);
        assertNotNull(result);
        assertEquals(1, result.size());
        var ccn = result.get(0);
        assertEquals(tfeId, ccn.getId());
        assertEquals(tfeName + "_" + endNumber, ccn.getUniqueName());
        assertEquals(tfeName, ccn.getName());
        assertEquals(terminalId, ccn.getTerminalId());
        assertEquals(endNumber, ccn.getEndNumber());
    }

    @Test
    void getTapChanger_WhenNoTapChangersFound_ThenEmptyOptionalReturned() {
        var tfeId = "Known Transformer End ID";

        when(cgmesModel.ratioTapChangers()).thenReturn(new PropertyBags());
        when(cgmesModel.phaseTapChangers()).thenReturn(new PropertyBags());

        var result = context.getTapChanger(tfeId);
        assertFalse(result.isPresent());
    }

    @Test
    void getTapChanger_WhenRatioTapChangersFound_ThenConvertedRatioTapChangerReturned() {
        var tcId = "TapChangerId";
        var tcName = "Name TapChanger";
        var tfeId = "Known Transformer End ID";

        var bags = new PropertyBags();
        var bag = new PropertyBag(List.of(RATIO_TAP_CHANGER_PROP, NAME_PROP, TRANSFORMER_END_PROP));
        bag.put(RATIO_TAP_CHANGER_PROP, tcId);
        bag.put(NAME_PROP, tcName);
        bag.put(TRANSFORMER_END_PROP, tfeId);
        bags.add(bag);
        when(cgmesModel.ratioTapChangers()).thenReturn(bags);

        var result = context.getTapChanger(tfeId);
        assertTrue(result.isPresent());
        var tapChanger = result.get();
        assertEquals(tcId, tapChanger.getId());
        assertEquals(tcName, tapChanger.getName());
    }

    @Test
    void getTapChanger_WhenNoRatioTapChangerFoundButPhaseTapChangersFound_ThenConvertedPhaseTapChangerReturned() {
        var tcId = "TapChangerId";
        var tcName = "Name TapChanger";
        var tfeId = "Known Transformer End ID";

        when(cgmesModel.ratioTapChangers()).thenReturn(new PropertyBags());

        var bags = new PropertyBags();
        var bag = new PropertyBag(List.of(PHASE_TAP_CHANGER_PROP, NAME_PROP, TRANSFORMER_END_PROP));
        bag.put(PHASE_TAP_CHANGER_PROP, tcId);
        bag.put(NAME_PROP, tcName);
        bag.put(TRANSFORMER_END_PROP, tfeId);
        bags.add(bag);
        when(cgmesModel.phaseTapChangers()).thenReturn(bags);

        var result = context.getTapChanger(tfeId);
        assertTrue(result.isPresent());
        var tapChanger = result.get();
        assertEquals(tcId, tapChanger.getId());
        assertEquals(tcName, tapChanger.getName());
    }

    @Test
    void getConnectivityNode_WhenCalledWithKnownId_ThenPropertyBagsIsFilteredOnIdAndConvertedToCgmesConnectivityNode() {
        var ccnId = "CcnId";
        var ccnName = "Name Ccn";
        var ccnContainerId = "Known Container ID";
        var bags = new PropertyBags();
        var bag = new PropertyBag(List.of(CONNECTIVITY_NODE_PROP, NAME_PROP, CONNECTIVITY_NODE_CONTAINER_PROP));
        bag.put(CONNECTIVITY_NODE_PROP, ccnId);
        bag.put(NAME_PROP, ccnName);
        bag.put(CONNECTIVITY_NODE_CONTAINER_PROP, ccnContainerId);
        bags.add(bag);

        bag = new PropertyBag(List.of(CONNECTIVITY_NODE_PROP, NAME_PROP, CONNECTIVITY_NODE_CONTAINER_PROP));
        bag.put(CONNECTIVITY_NODE_PROP, "Other ID");
        bag.put(NAME_PROP, "Other Name");
        bag.put(CONNECTIVITY_NODE_CONTAINER_PROP, "Unknown Container ID");
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
        var bag = new PropertyBag(List.of(SWITCH_PROP, NAME_PROP, TYPE_PROP, EQUIPMENT_CONTAINER_PROP, TERMINAL_1_PROP, TERMINAL_2_PROP));
        bag.put(SWITCH_PROP, switchId);
        bag.put(NAME_PROP, switchName);
        bag.put(TYPE_PROP, "Breaker");
        bag.put(EQUIPMENT_CONTAINER_PROP, containerId);
        bag.put(TERMINAL_1_PROP, "Terminal1 ID");
        bag.put(TERMINAL_2_PROP, "Terminal2 ID");
        bags.add(bag);

        bag = new PropertyBag(List.of(SWITCH_PROP, NAME_PROP, TYPE_PROP, EQUIPMENT_CONTAINER_PROP, TERMINAL_1_PROP, TERMINAL_2_PROP));
        bag.put(SWITCH_PROP, "Other ID");
        bag.put(NAME_PROP, "Other Name");
        bag.put(TYPE_PROP, "Breaker");
        bag.put(EQUIPMENT_CONTAINER_PROP, "Unknown Container ID");
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
        var bag = new PropertyBag(List.of(TERMINAL_PROP, NAME_PROP, CONNECTIVITY_NODE_PROP, CONDUCTING_EQUIPMENT_PROP));
        bag.put(TERMINAL_PROP, terminalId);
        bag.put(NAME_PROP, terminalName);
        bag.put(CONNECTIVITY_NODE_PROP, ccnNode);
        bag.put(CONDUCTING_EQUIPMENT_PROP, containerId);
        bags.add(bag);

        bag = new PropertyBag(List.of(TERMINAL_PROP, NAME_PROP, CONNECTIVITY_NODE_PROP, CONDUCTING_EQUIPMENT_PROP));
        bag.put(TERMINAL_PROP, "Other ID");
        bag.put(NAME_PROP, "Other Name");
        bag.put(CONNECTIVITY_NODE_PROP, "Some Other ID");
        bag.put(CONDUCTING_EQUIPMENT_PROP, "Unknown Container ID");
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
    void getTerminal_WhenCalledWithKnownId_ThenPropertyBagsIsFilteredOnIdAndConvertedToCgmesTerminal() {
        var terminalId = "TerminalId";
        var terminalName = "Name Terminal";
        var ccnNode = "Connectivity Node ID";
        var containerId = "Known Container ID";
        var bags = new PropertyBags();
        var bag = new PropertyBag(List.of(TERMINAL_PROP, NAME_PROP, CONNECTIVITY_NODE_PROP, CONDUCTING_EQUIPMENT_PROP));
        bag.put(TERMINAL_PROP, terminalId);
        bag.put(NAME_PROP, terminalName);
        bag.put(CONNECTIVITY_NODE_PROP, ccnNode);
        bag.put(CONDUCTING_EQUIPMENT_PROP, containerId);
        bags.add(bag);

        bag = new PropertyBag(List.of(TERMINAL_PROP, NAME_PROP, CONNECTIVITY_NODE_PROP, CONDUCTING_EQUIPMENT_PROP));
        bag.put(TERMINAL_PROP, "Other ID");
        bag.put(NAME_PROP, "Other Name");
        bag.put(CONNECTIVITY_NODE_PROP, "Some Other ID");
        bag.put(CONDUCTING_EQUIPMENT_PROP, "Unknown Container ID");
        bags.add(bag);

        when(cgmesModel.terminals()).thenReturn(bags);

        var result = context.getTerminal(terminalId);
        assertNotNull(result);
        var terminal = result.get();
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