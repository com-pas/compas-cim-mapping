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
import org.lfenergy.compas.scl2007b4.model.THitem;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Mapping Service to process the passed CIM XML(s) (RDF Format) and convert these to a Cgmes Model
 * that is used to create an IEC SCL Model, including some basic data being filled in the Header.
 */
@ApplicationScoped
public class CompasCimMappingService {
    private static final String INITIAL_VERSION = "0.0.1";
    private static final String INITIAL_REVISION = "";

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
     * @param who     The name of the user who created the SCL from the CIM Data.
     * @return The created SCL Model.
     */
    public SCL map(List<CimData> cimData, String who) {
        var scl = createBasicSCL(cimData, who);

        if (cimData != null && !cimData.isEmpty()) {
            // Convert the Data to the Network Model from PowSyBl
            var cgmesModel = cgmesCimReader.readModel(cimData);
            cimToSclMapper.mapToScl(scl, new CimToSclMapperContext(cgmesModel));
        }

        return scl;
    }

    /**
     * Create a basic SCL Object with common values filled.
     *
     * @param cimData The CIM XML Data.
     * @param who     The name of the user who created the SCL from the CIM Data.
     * @return The created SCL Model.
     */
    SCL createBasicSCL(List<CimData> cimData, String who) {
        var formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        var factory = new ObjectFactory();

        // Create the SCL and set some default values.
        var scl = factory.createSCL();
        scl.setVersion("2007");
        scl.setRevision("B");
        scl.setRelease((short) 4);

        // Create the Header and set some default values.
        var header = factory.createTHeader();
        header.setId(UUID.randomUUID().toString());
        header.setVersion(INITIAL_VERSION);
        header.setRevision(INITIAL_REVISION);
        header.setHistory(new THeader.History());
        scl.setHeader(header);

        // Add a History Item with info about who/when/what created the SCL.
        var item = new THitem();
        item.setVersion(INITIAL_VERSION);
        item.setRevision(INITIAL_REVISION);
        item.setWhen(formatter.format(new Date()));
        item.setWho(who);

        // Add all CIM filenames that where used to create the SCL Content.
        var what = "SCL created from CIM File(s)";
        if (cimData != null && !cimData.isEmpty()) {
            what += ": " + cimData.stream()
                    .map(CimData::getName)
                    .collect(Collectors.joining(", "));
        }
        item.setWhat(what);
        header.getHistory().getHitem().add(item);

        return scl;
    }
}
