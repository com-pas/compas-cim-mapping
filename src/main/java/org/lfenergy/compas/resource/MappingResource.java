// SPDX-FileCopyrightText: 2020 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.resource;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.lfenergy.compas.bean.Cim61850Mapping;
import org.lfenergy.compas.model.xml.rdf.RdfRoot;

@OpenAPIDefinition(
    info = @Info(
        title = "Mapping API",
        version = "0.0.1",
        description = "Using this API, you can do some mapping actions."
    )
)
@Path("/mapping")
public class MappingResource {

    @Inject
    private Cim61850Mapping mappingBean;
    
    @POST
    @Path("/rdf")
    @Consumes("application/xml")
    public Response rdfConfig(RdfRoot model) {
        mappingBean.get61850Mapping(model);
        return Response.status(200).entity(model).build();
    }
}
