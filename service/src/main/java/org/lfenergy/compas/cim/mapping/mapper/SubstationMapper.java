// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.mapper;

import com.powsybl.iidm.network.Substation;
import com.powsybl.iidm.network.VoltageLevel;
import org.lfenergy.compas.scl2007b4.model.TSubstation;
import org.lfenergy.compas.scl2007b4.model.TVoltageLevel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Optional;

/**
 * Interface for MapStruct to configure how a IIDM Substation is Mapped to a SCL Substation,
 * including the objects, like VoltageLevel.
 */
@Mapper
public interface SubstationMapper {
    SubstationMapper INSTANCE = Mappers.getMapper(SubstationMapper.class);

    @Mapping(source = "id", target = "name")
    @Mapping(source = "optionalName", target = "desc")
    @Mapping(source = "voltageLevelStream", target = "voltageLevel")
    TSubstation substationToTSubstation(Substation substation);

    @Mapping(source = "nameOrId", target = "name")
    @Mapping(source = "nominalV", target = "voltage.value")
    TVoltageLevel voltageLevelToTVoltageLevel(VoltageLevel voltageLevel);

    default String optionalString(Optional<String> value) {
        return value.orElse("");
    }
}
