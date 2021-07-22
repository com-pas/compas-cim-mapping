// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.exception;

import org.lfenergy.compas.core.commons.exception.CompasException;

/**
 * More specific Compas Exception for the Cim Mapping Project.
 */
public class CompasCimMappingException extends CompasException {
    public CompasCimMappingException(String errorCode, String msg) {
        super(errorCode, msg);
    }

    public CompasCimMappingException(String errorCode, String msg, Throwable throwable) {
        super(errorCode, msg, throwable);
    }
}
