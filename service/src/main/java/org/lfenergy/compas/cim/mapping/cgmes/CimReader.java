// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.cgmes;

//import com.powsybl.triplestore.api.PropertyBags;

import com.powsybl.cgmes.conversion.CgmesImport;
import com.powsybl.commons.datasource.ReadOnlyMemDataSource;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.NetworkFactory;

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

public class CimReader {
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
