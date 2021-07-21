// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.cgmes;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CimReaderTest {
    @Test
    void readModel_When_Then() {
        var cimContents = Map.of(
                "MiniGridTestConfiguration_BC_EQ_v3.0.0.xml", requireNonNull(getClass().getResourceAsStream("/minigrid/MiniGridTestConfiguration_BC_EQ_v3.0.0.xml")),
                "MiniGridTestConfiguration_BC_SV_v3.0.0.xml", requireNonNull(getClass().getResourceAsStream("/minigrid/MiniGridTestConfiguration_BC_SV_v3.0.0.xml")),
                "MiniGridTestConfiguration_BC_DL_v3.0.0.xml", requireNonNull(getClass().getResourceAsStream("/minigrid/MiniGridTestConfiguration_BC_DL_v3.0.0.xml")),
                "MiniGridTestConfiguration_BC_SSH_v3.0.0.xml", requireNonNull(getClass().getResourceAsStream("/minigrid/MiniGridTestConfiguration_BC_SSH_v3.0.0.xml")),
                "MiniGridTestConfiguration_BC_TP_v3.0.0.xml", requireNonNull(getClass().getResourceAsStream("/minigrid/MiniGridTestConfiguration_BC_TP_v3.0.0.xml")),
                "MiniGridTestConfiguration_EQ_BD_v3.0.0.xml", requireNonNull(getClass().getResourceAsStream("/minigrid/MiniGridTestConfiguration_EQ_BD_v3.0.0.xml")),
                "MiniGridTestConfiguration_TP_BD_v3.0.0.xml", requireNonNull(getClass().getResourceAsStream("/minigrid/MiniGridTestConfiguration_TP_BD_v3.0.0.xml")));
        var reader = new CimReader();
        var network = reader.readModel(cimContents);

        assertEquals(5, network.getSubstationStream().count());
    }
}