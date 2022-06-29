// SPDX-FileCopyrightText: 2022 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CgmesCommonEntityTest {
    @Test
    void getNameOrId_WhenCalledWithName_ThenNameIsReturned() {
        var id = UUID.randomUUID().toString();
        var name = "Some Name";
        var entity = new CgmesCommonEntityStub(id, name);

        assertEquals(name, entity.getNameOrId());
    }

    @Test
    void getNameOrId_WhenCalledWithNoName_ThenIdIsReturned() {
        var id = UUID.randomUUID().toString();
        var entity = new CgmesCommonEntityStub(id, null);

        assertEquals(id, entity.getNameOrId());
    }

    private record CgmesCommonEntityStub(String id, String name) implements CgmesCommonEntity {
    }
}