// SPDX-FileCopyrightText: 2020 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

/**
 * Default uri for all cim namespaces.
 */
@XmlSchema( 
    namespace = "http://iec.ch/TC57/2013/CIM-schema-cim16#", 
    elementFormDefault = XmlNsForm.QUALIFIED)
package org.lfenergy.compas.model.xml.cim;

import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;