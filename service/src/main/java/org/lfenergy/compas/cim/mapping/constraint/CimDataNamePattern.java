// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.constraint;

import org.lfenergy.compas.cim.mapping.constraint.impl.CimDataNamePatternValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation to mark that a field should follow the naming needed to read the CIM File with PowSyBl.
 */
@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE, TYPE_USE})
@Retention(RUNTIME)
@Constraint(validatedBy = {CimDataNamePatternValidator.class})
@Documented
public @interface CimDataNamePattern {
    String message() default "{org.lfenergy.compas.CimDataNamePattern.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
