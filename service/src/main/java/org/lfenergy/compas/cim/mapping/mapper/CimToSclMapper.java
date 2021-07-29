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

import java.util.Optional;

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
     * and IIDM Network Model to the IEC SCL Model.
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

    @Mapping(target = "name", source = "id")
    @Mapping(target = "desc", source = "optionalName")
    protected abstract TSubstation mapSubstationToTSubstation(CgmesSubstation substation,
                                                              @Context CimToSclMapperContext context);

    @AfterMapping
    protected void afterSubstationToTSubstation(CgmesSubstation substation,
                                                @MappingTarget TSubstation tSubstation,
                                                @Context CimToSclMapperContext context) {
        // The bays need to be mapped in a special way, because IIDM doesn't know them.
        context.getVoltageLevelsBySubstation(substation.getId())
                .stream()
                .map(voltageLevel -> mapVoltageLevelToTVoltageLevel(voltageLevel, context))
                .forEach(tVoltageLevel -> tSubstation.getVoltageLevel().add(tVoltageLevel));

    }

    @Mapping(target = "name", source = "nameOrId")
    @Mapping(target = "voltage.value", source = "nominalV")
    protected abstract TVoltageLevel mapVoltageLevelToTVoltageLevel(CgmesVoltageLevel voltageLevel,
                                                                    @Context CimToSclMapperContext context);

    @AfterMapping
    protected void afterVoltageLevelToTVoltageLevel(CgmesVoltageLevel voltageLevel,
                                                    @MappingTarget TVoltageLevel tVoltageLevel,
                                                    @Context CimToSclMapperContext context) {
        // The bays need to be mapped in a special way, because IIDM doesn't know them.
        context.getBaysByVoltageLevel(voltageLevel.getId())
                .stream()
                .map(bay -> mapBayToTBay(bay, context))
                .forEach(tBay -> tVoltageLevel.getBay().add(tBay));
    }

    @Mapping(target = "name", source = "nameOrId")
    protected abstract TBay mapBayToTBay(CgmesBay cgmesBay,
                                         @Context CimToSclMapperContext context);

    @AfterMapping
    protected void afterBayToTBay(CgmesBay cgmesBay,
                                  @MappingTarget TBay tBay,
                                  @Context CimToSclMapperContext context) {
        context.getSwitches(cgmesBay.getId())
                .stream()
                .map(cgmesSwitch -> mapSwitchToTConductingEquipment(cgmesSwitch, context))
                .forEach(tConductingEquipment -> tBay.getConductingEquipment().add(tConductingEquipment));
        context.getConnectivityNode(cgmesBay.getId())
                .stream()
                .map(cn -> mapConnectivityNodeToTConnectivityNode(cn, context))
                .forEach(tConnectivityNode -> tBay.getConnectivityNode().add(tConnectivityNode));
    }

    @Mapping(target = "name", source = "nameOrId")
    @Mapping(target = "type", expression = "java( org.lfenergy.compas.cim.mapping.model.SwitchType.convertSwitchType(cgmesSwitch.getType()).name() )")
    protected abstract TConductingEquipment mapSwitchToTConductingEquipment(CgmesSwitch cgmesSwitch,
                                                                            @Context CimToSclMapperContext context);

    @Mapping(target = "name", source = "nameOrId")
    protected abstract TConnectivityNode mapConnectivityNodeToTConnectivityNode(CgmesConnectivityNode cgmesConnectivityNode,
                                                                                @Context CimToSclMapperContext context);

    @AfterMapping
    protected void afterConnectivityNodeToTConnectivityNode(@MappingTarget TConnectivityNode tConnectivityNode,
                                                            @Context CimToSclMapperContext context) {
        tConnectivityNode.setPathName(context.createPathName());
    }

    protected String optionalString(Optional<String> value) {
        return value.orElse("");
    }

    @AfterMapping
    protected void afterTNaming(@MappingTarget TNaming tNaming,
                                @Context CimToSclMapperContext context) {
        // Remark: leave this method as last, so it will always be called as the last one of the AfterMapping.
        LOGGER.trace("Removing the Named Element {}", tNaming.getName());
        context.removeLast();
    }
}
