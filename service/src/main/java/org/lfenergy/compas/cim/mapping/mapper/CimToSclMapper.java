// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.mapper;

import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.Substation;
import com.powsybl.iidm.network.VoltageLevel;
import org.lfenergy.compas.cim.mapping.model.CgmesBay;
import org.lfenergy.compas.cim.mapping.model.CgmesConnectivityNode;
import org.lfenergy.compas.scl2007b4.model.*;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

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

    /**
     * Top level mapping method to start the mapping of all known elements from Cgmes Model
     * and IIDM Network Model to the IEC SCL Model.
     *
     * @param context Holding all data from which the SCL (also passed) needs to be filled.
     */
    public void map(CimToSclMapperContext context) {
        mapNetworkToScl(context.getNetwork(), context.getScl(), context);
    }

    @BeforeMapping
    protected void beforeTNaming(@MappingTarget TNaming tNaming,
                                 @Context CimToSclMapperContext context) {
        // Remark: leave this method as first, so it will always be called as the first one of the BeforeMapping.
        context.push(tNaming);
    }

    @Mapping(target = "substation", source = "substationStream")
    protected abstract void mapNetworkToScl(Network network,
                                            @MappingTarget SCL scl,
                                            @Context CimToSclMapperContext context);

    @Mapping(source = "id", target = "name")
    @Mapping(source = "optionalName", target = "desc")
    @Mapping(source = "voltageLevelStream", target = "voltageLevel")
    protected abstract TSubstation mapSubstationToTSubstation(Substation substation,
                                                              @Context CimToSclMapperContext context);

    @Mapping(source = "nameOrId", target = "name")
    @Mapping(source = "nominalV", target = "voltage.value")
    protected abstract TVoltageLevel mapVoltageLevelToTVoltageLevel(VoltageLevel voltageLevel,
                                                                    @Context CimToSclMapperContext context);

    @AfterMapping
    protected void afterVoltageLevelToTVoltageLevel(VoltageLevel voltageLevel,
                                                    @MappingTarget TVoltageLevel tVoltageLevel,
                                                    @Context CimToSclMapperContext context) {
        // The bays need to be mapped in a special way, because IIDM doesn't know them.
        context.getBaysByVoltageLevel(voltageLevel.getId())
                .stream()
                .map(bay -> mapBayToTBay(bay, context))
                .forEach(tBay -> tVoltageLevel.getBay().add(tBay));
    }

    @Mapping(source = "nameOrId", target = "name")
    protected abstract TBay mapBayToTBay(CgmesBay bay,
                                         @Context CimToSclMapperContext context);

    @AfterMapping
    protected void afterBayToTBay(CgmesBay bay,
                                  @MappingTarget TBay tBay,
                                  @Context CimToSclMapperContext context) {
        context.getConnectivityNode(bay.getId())
                .stream()
                .map(cn -> mapConnectivityNodeToTConnectivityNode(cn, context))
                .forEach(tConnectivityNode -> tBay.getConnectivityNode().add(tConnectivityNode));
    }

    @Mapping(source = "nameOrId", target = "name")
    protected abstract TConnectivityNode mapConnectivityNodeToTConnectivityNode(CgmesConnectivityNode connectivityNode,
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
        context.pop();
    }
}
