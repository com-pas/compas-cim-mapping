// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.mapper;

import com.powsybl.cgmes.model.CgmesModel;
import com.powsybl.iidm.network.Network;
import com.powsybl.triplestore.api.PropertyBag;
import com.powsybl.triplestore.api.PropertyBags;
import com.powsybl.triplestore.api.TripleStore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.compas.scl2007b4.model.SCL;
import org.lfenergy.compas.scl2007b4.model.TSubstation;
import org.lfenergy.compas.scl2007b4.model.TVoltageLevel;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CimToSclMapperContextTest {
    @Mock
    private CgmesModel cgmesModel;
    @Mock
    private Network network;
    @Mock
    private SCL scl;

    @InjectMocks
    private CimToSclMapperContext context;

    @Test
    void constructorAndGetters_WhenConstructorCalled_ThenGettersShouldReturnObjects() {
        // Constructor is called by the @InjectMocks.
        assertEquals(network, context.getNetwork());
        assertEquals(scl, context.getScl());
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
}