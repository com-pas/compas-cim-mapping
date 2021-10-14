// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.rest;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.lfenergy.compas.cim.mapping.mapper.CimToSclMapper;

import javax.enterprise.inject.Produces;

/**
 * Create Beans from other dependencies that are used in the application.
 *
 * Also, for native compilation we need to register certain classes for reflection.
 * This is done by using the RegisterForReflection annotation.
 */
@RegisterForReflection(targets = {
        com.powsybl.triplestore.impl.rdf4j.TripleStoreFactoryServiceRDF4J.class,
        org.lfenergy.compas.scl2007b4.model.ObjectFactory.class,
        org.lfenergy.compas.scl2007b4.model.TDA.class,
        org.lfenergy.compas.scl2007b4.model.TSDI.class,
        org.lfenergy.compas.scl2007b4.model.TDAI.class,
        javax.xml.bind.annotation.adapters.NormalizedStringAdapter.class,
        javax.xml.bind.annotation.adapters.CollapsedStringAdapter.class,
        javax.xml.bind.annotation.W3CDomHandler.class,
})
public class CompasCimMappingConfiguration {
    @Produces
    public CimToSclMapper createCimToSclMapper() {
        return CimToSclMapper.INSTANCE;
    }
}
