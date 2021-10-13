// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.cgmes;

import com.powsybl.cgmes.model.CgmesModel;
import com.powsybl.cgmes.model.CgmesModelFactory;
import com.powsybl.commons.datasource.ReadOnlyMemDataSource;
import com.powsybl.triplestore.api.TripleStoreFactory;
import org.apache.commons.io.input.ReaderInputStream;
import org.lfenergy.compas.cim.mapping.model.CimData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class to read the CIM Model into a Java Object Model to be used further for converting it to IEC 61850.
 */
@ApplicationScoped
public class CgmesCimReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(CgmesCimReader.class);

    /**
     * Use PowSyBl to convert a CIM XML InputStream to the PowSyBl Cgmes Model.
     * Multiple InputStream Objects can be passed if needed.
     *
     * @param cimData The different InputStream Objects that combined define the CIM Model.
     * @return The Cgmes Model that can be used to convert further to IEC 61850.
     */
    public CgmesModel readModel(List<CimData> cimData) {
        LOGGER.debug("Create a ReadOnlyDataSource from the input data.");
        var cimContents = convertCimDataToMap(cimData);
        var source = new ReadOnlyMemDataSource();
        cimContents.forEach(source::putData);

        LOGGER.debug("First create a CgmesModel from the InputStream (RDF File).");
        var tripStoreImpl = TripleStoreFactory.defaultImplementation();
        return CgmesModelFactory.create(source, tripStoreImpl);
    }

    Map<String, InputStream> convertCimDataToMap(List<CimData> cimData) {
        return cimData.stream()
                .collect(
                        Collectors.toMap(
                                CimData::getName,
                                cimRecord -> new ReaderInputStream(
                                        new StringReader(cimRecord.getRdfData()), StandardCharsets.UTF_8))
                );
    }
}
