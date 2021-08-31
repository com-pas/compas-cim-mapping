// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AbstractCgmesEntityTest extends AbstractPojoTester {
    @Override
    protected Class<?> getClassToBeTested() {
        return AbstractCgmesEntity.class;
    }

    @Test
    void getNameOrId_WhenCalledWithName_ThenNameReturned() {
        var id = "id";
        var name = "name";
        var model = new CgmesBay(id, name);

        assertEquals(name, model.getNameOrId());
    }

    @Test
    void getNameOrId_WhenCalledWithoutName_ThenIdReturned() {
        var id = "id";
        var model = new CgmesBay(id, null);

        assertEquals(id, model.getNameOrId());
    }
}
