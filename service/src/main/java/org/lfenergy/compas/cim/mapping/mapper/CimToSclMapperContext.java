// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.mapper;

import com.powsybl.cgmes.model.CgmesModel;
import com.powsybl.iidm.network.Network;
import org.lfenergy.compas.cim.mapping.model.CgmesBay;
import org.lfenergy.compas.cim.mapping.model.CgmesConnectivityNode;
import org.lfenergy.compas.scl2007b4.model.SCL;
import org.lfenergy.compas.scl2007b4.model.TNaming;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

public class CimToSclMapperContext {
    private final CgmesModel cgmesModel;
    private final Network network;
    private final SCL scl;

    public CimToSclMapperContext(CgmesModel cgmesModel, Network network, SCL scl) {
        this.cgmesModel = cgmesModel;
        this.network = network;
        this.scl = scl;
    }

    public Network getNetwork() {
        return network;
    }

    public SCL getScl() {
        return scl;
    }

    /**
     * Search for bays that belong to a specific Voltage Level. Because CGMES Model doesn't support this
     * a SparQL is executed against the TripleStore.
     *
     * @param voltageLevelId The ID of the Voltage Level to filter on.
     * @return The list of converted CGMES Bay that were found.
     */
    public List<CgmesBay> getBaysByVoltageLevel(String voltageLevelId) {
        return cgmesModel.tripleStore().query(
                "SELECT *\n" +
                        "WHERE {\n" +
                        "GRAPH ?graph {\n" +
                        "    ?Bay\n" +
                        "        a cim:Bay ;\n" +
                        "        cim:IdentifiedObject.name ?name ;\n" +
                        "        cim:Bay.VoltageLevel ?VoltageLevel ;\n" +
                        " FILTER (str(?VoltageLevel) = \"http://default-cgmes-model/#" + voltageLevelId + "\") " +
                        "}}").stream()
                .map(bag -> new CgmesBay(bag.getId("Bay"), bag.get("name")))
                .collect(Collectors.toList());
    }

    /**
     * Search the CGMES Model for Connectivity Nodes that below to a specific container.
     *
     * @param id The ID of the Container.
     * @return The List of converted CGMES ConnectivityNode that were found.
     */
    public List<CgmesConnectivityNode> getConnectivityNode(String id) {
        return cgmesModel.connectivityNodes()
                .stream()
                .filter(bag -> id.equals(bag.getId("ConnectivityNodeContainer")))
                .map(bag -> new CgmesConnectivityNode(bag.getId("ConnectivityNode"), bag.get("name")))
                .collect(Collectors.toList());
    }

    /*
     * Below part contains methods to keep track of all the naming elements passed.
     * At the end this list is used to create a PathName for the ConnectivityNode.
     */
    // Stack holding all passed TNaming Elements.
    private Deque<TNaming> namingLevels = new ArrayDeque<>();

    /**
     * Adds the parameter to the stack. Called on the way down (BeforeMapping).
     *
     * @param tNaming The naming element to add.
     */
    public void push(TNaming tNaming) {
        namingLevels.push(tNaming);
    }

    /**
     * Remove the last added element from the stack. Called on the way back (AfterMapping).
     */
    public TNaming pop() {
        return namingLevels.pop();
    }

    /**
     * Uses the Stack to create a PathName for the ConnectivityNode. All names are joined separated by a slash.
     *
     * @return The created PathName from the Stack.
     */
    public String createPathName() {
        return namingLevels.stream()
                .map(TNaming::getName)
                .collect(Collectors.joining("/"));
    }
}
