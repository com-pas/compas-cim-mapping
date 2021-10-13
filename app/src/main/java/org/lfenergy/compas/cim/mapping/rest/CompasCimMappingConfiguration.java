// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.rest;

import com.powsybl.cgmes.model.*;
import com.powsybl.cgmes.model.triplestore.CgmesModelTripleStore;
import com.powsybl.commons.datasource.ReadOnlyDataSource;
import com.powsybl.commons.datasource.ReadOnlyMemDataSource;
import com.powsybl.triplestore.api.*;
import com.powsybl.triplestore.impl.rdf4j.TripleStoreFactoryServiceRDF4J;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.lfenergy.compas.cim.mapping.mapper.CimToSclMapper;
import org.lfenergy.compas.core.commons.ElementConverter;
import org.lfenergy.compas.scl2007b4.model.ObjectFactory;
import org.lfenergy.compas.scl2007b4.model.TDA;
import org.lfenergy.compas.scl2007b4.model.TDAI;
import org.lfenergy.compas.scl2007b4.model.TSDI;

import javax.enterprise.inject.Produces;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;

/**
 * Create Beans from other dependencies that are used in the application.
 */
@RegisterForReflection(targets = {TripleStoreFactoryServiceRDF4J.class, TripleStoreFactoryService.class, ObjectFactory.class,
        TDA.class, TSDI.class, TDAI.class, NormalizedStringAdapter.class, CollapsedStringAdapter.class})
public class CompasCimMappingConfiguration {
    @Produces
    public ElementConverter createElementConverter() {
        return new ElementConverter();
    }

    @Produces
    public CimToSclMapper createCimToSclMapper() {
        return CimToSclMapper.INSTANCE;
    }
}
