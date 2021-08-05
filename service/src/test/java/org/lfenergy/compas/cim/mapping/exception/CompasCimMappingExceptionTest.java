// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.lfenergy.compas.cim.mapping.exception.CompasCimMappingErrorCode.UNKNOWN_TYPE_ERROR_CODE;

class CompasCimMappingExceptionTest {
    @Test
    void constructor_WhenCalledWithOnlyMessage_ThenMessageCanBeRetrieved() {
        var expectedMessage = "The message";
        var exception = new CompasCimMappingException(UNKNOWN_TYPE_ERROR_CODE, expectedMessage);

        assertEquals(UNKNOWN_TYPE_ERROR_CODE, exception.getErrorCode());
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void constructor_WhenCalledWithMessageAndThrowable_ThenMessageAndThrowableCanBeRetrieved() {
        var expectedMessage = "The message";
        var expectedThrowable = new NullPointerException();
        var exception = new CompasCimMappingException(UNKNOWN_TYPE_ERROR_CODE, expectedMessage, expectedThrowable);

        assertEquals(UNKNOWN_TYPE_ERROR_CODE, exception.getErrorCode());
        assertEquals(expectedMessage, exception.getMessage());
        assertEquals(expectedThrowable, exception.getCause());
    }
}