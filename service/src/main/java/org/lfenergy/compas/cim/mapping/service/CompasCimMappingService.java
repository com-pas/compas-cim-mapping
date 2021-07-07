// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.cim.mapping.service;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CompasCimMappingService {
    public String getMessage(String name) {
        return "Hello " + ((name != null && !name.isBlank()) ? name : "world");
    }
}
