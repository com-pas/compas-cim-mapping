// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.cgmes;

import com.powsybl.cgmes.model.CgmesModel;
import com.powsybl.iidm.network.Network;

public class CgmesCimReaderResult {
    private final CgmesModel cgmesModel;
    private final Network network;

    public CgmesCimReaderResult(CgmesModel cgmesModel, Network network) {
        this.cgmesModel = cgmesModel;
        this.network = network;
    }

    public CgmesModel getCgmesModel() {
        return cgmesModel;
    }

    public Network getNetwork() {
        return network;
    }
}
