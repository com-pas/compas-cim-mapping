// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.mapper;

import com.powsybl.cgmes.model.CgmesModel;
import org.lfenergy.compas.cim.mapping.model.*;
import org.lfenergy.compas.scl2007b4.model.TNaming;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class CimToSclMapperContext {
    private final CgmesModel cgmesModel;

    public CimToSclMapperContext(CgmesModel cgmesModel) {
        this.cgmesModel = cgmesModel;
    }

    /**
     * Search the CGMES Model for all Substations that below to the network.
     *
     * @return The List of converted CGMES Substation that were found.
     */
    public List<CgmesSubstation> getSubstations() {
        return cgmesModel.substations()
                .stream()
                .map(propertyBag -> new CgmesSubstation(
                        propertyBag.getId("Substation"),
                        propertyBag.get("name")))
                .collect(Collectors.toList());
    }

    /**
     * Search the CGMES Model for VoltageLevels that below to a specific substation.
     *
     * @param substationId The ID of the Substation.
     * @return The List of converted CGMES VoltageLevel that were found.
     */
    public List<CgmesVoltageLevel> getVoltageLevelsBySubstation(String substationId) {
        return cgmesModel.voltageLevels()
                .stream()
                .filter(propertyBag -> substationId.equals(propertyBag.getId("Substation")))
                .map(propertyBag -> new CgmesVoltageLevel(
                        propertyBag.getId("VoltageLevel"),
                        propertyBag.get("name"),
                        propertyBag.asDouble("nominalVoltage")))
                .collect(Collectors.toList());
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
     * @param containerId The ID of the Container.
     * @return The List of converted CGMES ConnectivityNode that were found.
     */
    public List<CgmesConnectivityNode> getConnectivityNode(String containerId) {
        return cgmesModel.connectivityNodes()
                .stream()
                .filter(propertyBag -> containerId.equals(propertyBag.getId("ConnectivityNodeContainer")))
                .map(propertyBag -> new CgmesConnectivityNode(
                        propertyBag.getId("ConnectivityNode"),
                        propertyBag.get("name")))
                .collect(Collectors.toList());
    }

    /**
     * Search the CGMES Model for Switches (Breakers, Disconnector and more) that below to a specific container.
     *
     * @param containerId The ID of the Container.
     * @return The List of converted CGMES Switches that were found.
     */
    public List<CgmesSwitch> getSwitches(String containerId) {
        return cgmesModel.switches()
                .stream()
                .filter(propertyBag -> containerId.equals(propertyBag.getId("EquipmentContainer")))
                .map(propertyBag -> new CgmesSwitch(
                        propertyBag.getId("Switch"),
                        propertyBag.get("name"),
                        propertyBag.getLocal("type"),
                        propertyBag.getId("Terminal1"),
                        propertyBag.getId("Terminal2")))
                .collect(Collectors.toList());
    }

    /*
     * Below part contains methods to keep track of all the naming elements passed.
     * At the end this list is used to create a PathName for the ConnectivityNode.
     */
    // List holding all passed TNaming Elements
    private LinkedList<TNaming> namingLevels = new LinkedList<>();

    /**
     * Adds the parameter to the stack. Called on the way down (BeforeMapping).
     *
     * @param tNaming The naming element to add.
     */
    public void addLast(TNaming tNaming) {
        namingLevels.addLast(tNaming);
    }

    /**
     * Remove the last added element from the stack. Called on the way back (AfterMapping).
     */
    public TNaming removeLast() {
        return namingLevels.removeLast();
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
