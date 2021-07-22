// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.cgmes;

import com.powsybl.cgmes.conversion.CgmesImport;
import com.powsybl.commons.datasource.ReadOnlyMemDataSource;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.NetworkFactory;
import org.lfenergy.compas.cim.mapping.model.CimData;
import org.lfenergy.compas.core.commons.ElementConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Class to read the CIM Model into a Java Object Model to be used further for converting it to IEC 61850.
 */
@ApplicationScoped
public class CgmesCimReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(CgmesCimReader.class);

    private final CgmesDataValidator cgmesDataValidator;
    private final ElementConverter converter;

    @Inject
    public CgmesCimReader(CgmesDataValidator cgmesDataValidator, ElementConverter converter) {
        this.cgmesDataValidator = cgmesDataValidator;
        this.converter = converter;
    }

    /**
     * Use PowSyBl to convert a CIM XML InputStream to the PowSyBl IIDM Model.
     * Multiple InputStream Objects can be passed if needed.
     *
     * @param cimData The different InputStream Objects that combined define the CIM Model.
     * @return The IIDM Network model that can be used to convert further to IEC 61850.
     */
    public Network readModel(List<CimData> cimData) {
        LOGGER.info("Check the data passed, PowSyBl is quite sensitive about naming.");
        cgmesDataValidator.validateData(cimData);
        var cimContents = convertCimDataToMap(cimData);

        LOGGER.debug("Create a ReadOnlyDataSource from the input data.");
        var source = new ReadOnlyMemDataSource();
        cimContents.forEach(source::putData);

        LOGGER.debug("Use the CGMES Import Class to convert the XML Data using RDF4J to IIDM.");
        var cgmesImport = new CgmesImport();
        var importParameters = new Properties();
        var networkFactory = NetworkFactory.findDefault();
        return cgmesImport.importData(source, networkFactory, importParameters);
    }

    Map<String, InputStream> convertCimDataToMap(List<CimData> cimData) {
        return cimData.stream().collect(
                Collectors.toMap(
                        CimData::getName,
                        cimRecord -> {
                            var xml = converter.convertToString(cimRecord.getRdf(), false);
                            return new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
                        })
        );
    }
}
