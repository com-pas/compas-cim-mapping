// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.mapper;

import org.lfenergy.compas.cim.mapping.model.*;
import org.lfenergy.compas.scl2007b4.model.*;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

import static org.lfenergy.compas.cim.mapping.CimMappingConstants.DC_LINE_SEGMENT_TYPE;

/**
 * Interface for MapStruct to configure how a Cim Model is Mapped to a SCL IEC Model,
 * including the objects, like Substation, VoltageLevel and more.
 * <p>
 * Remark: Don't create a single INSTANCE variable, because we are using class variables
 * that change every request.
 */
@Mapper
public abstract class CimToSclMapper {
    public static final CimToSclMapper INSTANCE = Mappers.getMapper(CimToSclMapper.class);

    private static final Logger LOGGER = LoggerFactory.getLogger(CimToSclMapper.class);

    /**
     * Top level mapping method to start the mapping of all known elements from Cgmes Model
     * to the IEC SCL Model.
     *
     * @param context Holding all data from which the SCL (also passed) needs to be filled.
     */
    public void mapToScl(SCL scl, CimToSclMapperContext context) {
        LOGGER.info("Mapping the CIM Content to SCL Content");
        context.getSubstations()
                .stream()
                .map(substation -> mapSubstationToTSubstation(substation, context))
                .forEach(tSubstation -> scl.getSubstation().add(tSubstation));
    }

    @BeforeMapping
    protected void beforeTNaming(@MappingTarget TNaming tNaming,
                                 @Context CimToSclMapperContext context) {
        // Remark: leave this method as first, so it will always be called as the first one of the BeforeMapping.
        LOGGER.trace("Adding the Named Element {}", tNaming.getName());
        context.addLast(tNaming);
    }

    @BeforeMapping
    protected void beforeSubstationToTSubstation(@MappingTarget TSubstation tSubstation,
                                                 @Context CimToSclMapperContext context) {
        // Reset the Map of ConnectivityNodes, because we only need them inside processing of the Substation.
        context.resetTConnectivityNodeMap();
    }

    @Mapping(target = "name", source = "id")
    @Mapping(target = "desc", source = "name")
    protected abstract TSubstation mapSubstationToTSubstation(CgmesSubstation substation,
                                                              @Context CimToSclMapperContext context);

    @AfterMapping
    protected void afterSubstationToTSubstation(CgmesSubstation substation,
                                                @MappingTarget TSubstation tSubstation,
                                                @Context CimToSclMapperContext context) {
        context.getVoltageLevelsBySubstation(substation.id())
                .stream()
                .map(voltageLevel -> mapVoltageLevelToTVoltageLevel(voltageLevel, context))
                .forEach(tVoltageLevel -> tSubstation.getVoltageLevel().add(tVoltageLevel));

        // PowerTransformers coupled to the Bay Level.
        context.getTransformers(substation.id())
                .stream()
                .map(transformer -> mapTransformerToTPowerTransformer(transformer, context))
                .forEach(tPowerTransformer -> tSubstation.getPowerTransformer().add(tPowerTransformer));
    }

    @Mapping(target = "name", source = "nameOrId")
    @Mapping(target = "voltage.value", source = "nominalV")
    @Mapping(target = "voltage.multiplier", constant = "k")
    @Mapping(target = "voltage.unit", constant = "V")
    protected abstract TVoltageLevel mapVoltageLevelToTVoltageLevel(CgmesVoltageLevel voltageLevel,
                                                                    @Context CimToSclMapperContext context);

    @AfterMapping
    protected void afterVoltageLevelToTVoltageLevel(CgmesVoltageLevel cgmesVoltageLevel,
                                                    @MappingTarget TVoltageLevel tVoltageLevel,
                                                    @Context CimToSclMapperContext context) {
        // First we need to process the BusbarSections before the Bays.
        // This way the Connectivity Nodes from the BusbarSections are known to the Terminals in the Bay.
        context.getBusbarSectionsByEquipmentContainer(cgmesVoltageLevel.id())
                .stream()
                .map(cgmesBusbarSection -> mapBusbarSectionBayToTBay(cgmesBusbarSection, cgmesVoltageLevel, tVoltageLevel, context))
                .forEach(tBay -> tVoltageLevel.getBay().add(tBay));

        context.getBaysByVoltageLevel(cgmesVoltageLevel.id())
                .stream()
                .map(bay -> mapBayToTBay(bay, cgmesVoltageLevel, tVoltageLevel, context))
                .forEach(tBay -> tVoltageLevel.getBay().add(tBay));

        // PowerTransformers coupled to the Bay Level.
        context.getTransformers(cgmesVoltageLevel.id())
                .stream()
                .map(transformer -> mapTransformerToTPowerTransformer(transformer, context))
                .forEach(tPowerTransformer -> tVoltageLevel.getPowerTransformer().add(tPowerTransformer));
    }

    @Mapping(target = "name", source = "nameOrId")
    protected abstract TBay mapBusbarSectionBayToTBay(CgmesBusbarSection cgmesBusbarSection,
                                                      @Context CgmesVoltageLevel cgmesVoltageLevel,
                                                      @Context TVoltageLevel tVoltageLevel,
                                                      @Context CimToSclMapperContext context);

    @AfterMapping
    protected void afterBusbarSectionBayToTBay(CgmesBusbarSection cgmesBusbarSection,
                                               @MappingTarget TBay tBay,
                                               @Context CimToSclMapperContext context) {
        context.getConnectivityNodeByBusbarSection(cgmesBusbarSection.id()).stream()
                .filter(cn -> !context.containsTConnectivityNode(cn.id()))
                .map(cn -> mapConnectivityNodeToTConnectivityNode(cn, context))
                .forEach(tConnectivityNode -> tBay.getConnectivityNode().add(tConnectivityNode));
    }

    @Mapping(target = "name", source = "nameOrId")
    protected abstract TBay mapBayToTBay(CgmesBay cgmesBay,
                                         @Context CgmesVoltageLevel cgmesVoltageLevel,
                                         @Context TVoltageLevel tVoltageLevel,
                                         @Context CimToSclMapperContext context);

    @AfterMapping
    protected void afterBayToTBay(CgmesBay cgmesBay,
                                  @MappingTarget TBay tBay,
                                  @Context TVoltageLevel tVoltageLevel,
                                  @Context CimToSclMapperContext context) {
        // First we will process the Connectivity Nodes, because their path names are needed in the Terminal
        // of a Conduction Equipment.
        context.getConnectivityNodeByBay(cgmesBay.id())
                .stream()
                .filter(cn -> !context.containsTConnectivityNode(cn.id()))
                .map(cn -> mapConnectivityNodeToTConnectivityNode(cn, context))
                .forEach(tConnectivityNode -> tBay.getConnectivityNode().add(tConnectivityNode));

        // Now we can process the Conduction Equipment with their terminals.
        context.getSwitches(cgmesBay.id())
                .stream()
                .map(cgmesSwitch -> mapSwitchToTConductingEquipment(cgmesSwitch, tVoltageLevel, context))
                .forEach(tConductingEquipment -> tBay.getConductingEquipment().add(tConductingEquipment));

        // PowerTransformers coupled to the Bay Level.
        context.getTransformers(cgmesBay.id())
                .stream()
                .map(transformer -> mapTransformerToTPowerTransformer(transformer, context))
                .forEach(tPowerTransformer -> tBay.getPowerTransformer().add(tPowerTransformer));
    }

    @Mapping(target = "name", source = "nameOrId")
    @Mapping(target = "desc", source = "description")
    @Mapping(target = "type", constant = "PTR")
    protected abstract TPowerTransformer mapTransformerToTPowerTransformer(CgmesTransformer transformer,
                                                                           @Context CimToSclMapperContext context);

    @AfterMapping
    protected void afterTransformerToTPowerTransformer(CgmesTransformer transformer,
                                                       @MappingTarget TPowerTransformer tPowerTransformer,
                                                       @Context CimToSclMapperContext context) {
        // PowerTransformer Ends coupled to the PowerTransformer.
        context.getTransformerEnds(transformer.id())
                .stream()
                .map(transformerEnd -> mapTransformerEndToTTransformerWinding(transformerEnd, context))
                .forEach(tTransformerWinding -> tPowerTransformer.getTransformerWinding().add(tTransformerWinding));
    }

    @Mapping(target = "name", source = "uniqueName")
    @Mapping(target = "type", constant = "PTW")
    protected abstract TTransformerWinding mapTransformerEndToTTransformerWinding(CgmesTransformerEnd transformerEnd,
                                                                                  @Context CimToSclMapperContext context);

    @AfterMapping
    protected void afterTransformerEndToTTransformerWinding(CgmesTransformerEnd transformerEnd,
                                                            @MappingTarget TTransformerWinding tTransformerWinding,
                                                            @Context CimToSclMapperContext context) {
        // Convert the Ratio-/PhaseTapChanger from IEC CIM to IEC 61850.
        context.getTapChanger(transformerEnd.id())
                .ifPresent(cgmesTapChanger -> {
                    var tTapChanger = mapTapChangerToTTapChanger(cgmesTapChanger, context);
                    tTransformerWinding.setTapChanger(tTapChanger);
                });

        context.getTerminalById(transformerEnd.terminalId())
                .ifPresent(cgmesTerminal -> {
                    var tTerminal = mapTerminalToTTerminal(cgmesTerminal, context);
                    tTransformerWinding.getTerminal().add(tTerminal);
                });

    }

    @Mapping(target = "name", source = "nameOrId")
    @Mapping(target = "type", constant = "LTC")
    protected abstract TTapChanger mapTapChangerToTTapChanger(CgmesTapChanger tapChanger,
                                                              @Context CimToSclMapperContext context);

    @Mapping(target = "name", source = "nameOrId")
    protected abstract TConnectivityNode mapConnectivityNodeToTConnectivityNode(CgmesConnectivityNode cgmesConnectivityNode,
                                                                                @Context CimToSclMapperContext context);

    @AfterMapping
    protected void afterConnectivityNodeToTConnectivityNode(CgmesConnectivityNode cgmesConnectivityNode,
                                                            @MappingTarget TConnectivityNode tConnectivityNode,
                                                            @Context CimToSclMapperContext context) {
        var pathName = context.createPathName();
        tConnectivityNode.setPathName(pathName);
        context.saveTConnectivityNode(cgmesConnectivityNode.id(), tConnectivityNode);
    }

    @Mapping(target = "name", source = "nameOrId")
    @Mapping(target = "type", expression = "java( org.lfenergy.compas.cim.mapping.model.SwitchType.convertSwitchType(cgmesSwitch.type()).name() )")
    protected abstract TConductingEquipment mapSwitchToTConductingEquipment(CgmesSwitch cgmesSwitch,
                                                                            @Context TVoltageLevel tVoltageLevel,
                                                                            @Context CimToSclMapperContext context);

    @AfterMapping
    protected void afterSwitchToTConductingEquipment(CgmesSwitch cgmesSwitch,
                                                     @MappingTarget TConductingEquipment tConductingEquipment,
                                                     @Context TVoltageLevel tVoltageLevel,
                                                     @Context CimToSclMapperContext context) {
        // For DCLineSegment the nomFreq from the Voltage Level to 0
        if (DC_LINE_SEGMENT_TYPE.equals(cgmesSwitch.type())) {
            tVoltageLevel.setNomFreq(BigDecimal.ZERO);
        }

        context.getTerminalsByConductingEquipment(cgmesSwitch.id())
                .stream()
                .map(cgmesTerminal -> mapTerminalToTTerminal(cgmesTerminal, context))
                .forEach(tTerminal -> tConductingEquipment.getTerminal().add(tTerminal));
    }


    @Mapping(target = "name", source = "nameOrId")
    @Mapping(target = "connectivityNode", expression = "java( context.getPathnameFromConnectivityNode(cgmesTerminal.connectivityNodeId()).orElse(null) )")
    @Mapping(target = "CNodeName", expression = "java( context.getNameFromConnectivityNode(cgmesTerminal.connectivityNodeId()).orElse(null) )")
    protected abstract TTerminal mapTerminalToTTerminal(CgmesTerminal cgmesTerminal,
                                                        @Context CimToSclMapperContext context);

    @AfterMapping
    protected void afterTNaming(@MappingTarget TNaming tNaming,
                                @Context CimToSclMapperContext context) {
        // Remark: leave this method as last, so it will always be called as the last one of the AfterMapping.
        LOGGER.trace("Removing the Named Element {}", tNaming.getName());
        context.removeLast();
    }
}
