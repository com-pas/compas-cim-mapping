// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.mapper;

import com.powsybl.iidm.network.Network;
import org.lfenergy.compas.scl2007b4.model.SCL;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * Class to Map all Data from the PowSyBl Network Model on the IEC SCL Class.
 */
@ApplicationScoped
public class CimToSclMapper {
    private final SubstationMapper substationMapper;

    @Inject
    public CimToSclMapper(SubstationMapper substationMapper) {
        this.substationMapper = substationMapper;
    }

    /**
     * Map the Network Model to the SCL Model using the MapStruct Framework.
     *
     * @param network The source Network Model.
     * @param scl     The target SCL Model.
     */
    public void mapCimToScl(Network network, SCL scl) {
        network.getSubstationStream()
                .forEach(substation -> {
                    var sclSubStation = substationMapper.substationToTSubstation(substation);
                    scl.getSubstation().add(sclSubStation);
                });
    }
}
