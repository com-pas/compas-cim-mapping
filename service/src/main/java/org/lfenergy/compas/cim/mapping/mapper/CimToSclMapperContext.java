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

import java.util.List;
import java.util.Stack;
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

    public List<CgmesConnectivityNode> getConnectivityNode(String id) {
        return cgmesModel.connectivityNodes()
                .stream()
                .filter(bag -> id.equals(bag.getId("ConnectivityNodeContainer")))
                .map(bag -> new CgmesConnectivityNode(bag.getId("ConnectivityNode"), bag.get("name")))
                .collect(Collectors.toList());
    }

    private Stack<TNaming> namingLevels = new Stack<>();

    public void push(TNaming tNaming) {
        namingLevels.push(tNaming);
    }

    public void pop() {
        namingLevels.pop();
    }

    public String createPathName() {
        return namingLevels.stream()
                .map(TNaming::getName)
                .collect(Collectors.joining("/"));
    }
}
