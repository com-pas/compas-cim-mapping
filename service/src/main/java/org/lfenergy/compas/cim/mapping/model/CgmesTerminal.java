// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.model;

public record CgmesTerminal(String id, String name, String connectivityNodeId) implements CgmesCommonEntity {
}
