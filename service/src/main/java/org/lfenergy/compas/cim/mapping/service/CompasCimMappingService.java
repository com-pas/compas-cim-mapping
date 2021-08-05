// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.service;

import org.lfenergy.compas.cim.mapping.cgmes.CgmesCimReader;
import org.lfenergy.compas.cim.mapping.mapper.CimToSclMapper;
import org.lfenergy.compas.cim.mapping.mapper.CimToSclMapperContext;
import org.lfenergy.compas.cim.mapping.model.CimData;
import org.lfenergy.compas.scl2007b4.model.ObjectFactory;
import org.lfenergy.compas.scl2007b4.model.SCL;
import org.lfenergy.compas.scl2007b4.model.THeader;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

/**
 * Mapping Service to process the passed CIM XML(s) (RDF Format) and convert these to a IIDM Model
 * that is used to create a IEC SCL Model, including some basic data being filled in the Header.
 */
@ApplicationScoped
public class CompasCimMappingService {
    private final CgmesCimReader cgmesCimReader;
    private final CimToSclMapper cimToSclMapper;

    @Inject
    public CompasCimMappingService(CgmesCimReader cgmesCimReader,
                                   CimToSclMapper cimToSclMapper) {
        this.cgmesCimReader = cgmesCimReader;
        this.cimToSclMapper = cimToSclMapper;
    }

    /**
     * Map the passed CIM XML to IEC SCL Model.
     *
     * @param cimData The CIM XML Data.
     * @return The created SCL Model.
     */
    public SCL map(List<CimData> cimData) {
        var scl = createBasicSCL();

        if (cimData != null && !cimData.isEmpty()) {
            // Convert the Data to the Network Model from PowSyBl
            var cgmesModel = cgmesCimReader.readModel(cimData);
            cimToSclMapper.mapToScl(scl, new CimToSclMapperContext(cgmesModel));
        }

        return scl;
    }

    /**
     * Create a basic SCL Obejct with common values filled.
     *
     * @return The created SCL Model.
     */
    SCL createBasicSCL() {
        ObjectFactory factory = new ObjectFactory();

        var scl = factory.createSCL();
        scl.setVersion("2007");
        scl.setRevision("B");
        scl.setRelease((short) 4);

        THeader header = factory.createTHeader();
        header.setId(UUID.randomUUID().toString());
        header.setVersion("1.0.0");
        header.setHistory(new THeader.History());
        scl.setHeader(header);

        return scl;
    }
}
