// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.model;

public class CgmesVoltageLevel extends AbstractCgmesEntity {
    private double nominalV;

    public CgmesVoltageLevel(String id, String name, double nominalV) {
        super(id, name);

        this.nominalV = nominalV;
    }

    public double getNominalV() {
        return nominalV;
    }

    public void setNominalV(double nominalV) {
        this.nominalV = nominalV;
    }
}
