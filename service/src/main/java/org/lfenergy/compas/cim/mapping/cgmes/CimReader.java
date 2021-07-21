// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.cgmes;

import com.powsybl.cgmes.conversion.CgmesImport;
import com.powsybl.commons.datasource.ReadOnlyMemDataSource;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.NetworkFactory;

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * Class to read the CIM Model into a Java Object Model to be used further for converting it to IEC 61850.
 */
public class CimReader {
    /**
     * Use PowSyBl to convert a CIM XML InputStream to the PowSyBl IIDM Model.
     * Multiple InputStream Objects can be passed if needed.
     *
     * @param cimContents The different InputStream Objects that combined define the CIM Model.
     * @return The IIDM Network model that can be used to convert further to IEC 61850.
     */
    public Network readModel(Map<String, InputStream> cimContents) {
        var source = new ReadOnlyMemDataSource();
        if (cimContents != null) {
            cimContents.forEach(source::putData);
        }

        var cgmesImport = new CgmesImport();
        var importParameters = new Properties();
        var networkFactory = NetworkFactory.findDefault();
        return cgmesImport.importData(source, networkFactory, importParameters);
    }
}
