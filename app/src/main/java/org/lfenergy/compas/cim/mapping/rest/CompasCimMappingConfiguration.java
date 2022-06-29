// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.rest;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.lfenergy.compas.cim.mapping.mapper.CimToSclMapper;

import javax.enterprise.inject.Produces;

/**
 * Create Beans from other dependencies that are used in the application.
 */
@RegisterForReflection(targets = {
        com.powsybl.triplestore.impl.rdf4j.TripleStoreFactoryServiceRDF4J.class,
        org.lfenergy.compas.core.jaxrs.model.ErrorResponse.class,
        org.lfenergy.compas.core.jaxrs.model.ErrorMessage.class,
        javax.xml.namespace.QName.class
})
public class CompasCimMappingConfiguration {
    @Produces
    public CimToSclMapper createCimToSclMapper() {
        return CimToSclMapper.INSTANCE;
    }
}
