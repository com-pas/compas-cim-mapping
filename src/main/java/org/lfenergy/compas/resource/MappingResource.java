// SPDX-FileCopyrightText: 2020 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.UnsupportedRDFormatException;

import org.jboss.logging.Logger;

@OpenAPIDefinition(
    info = @Info(
        title = "Mapping API",
        version = "0.0.1",
        description = "Using this API, you can do some mapping actions."
    )
)
@Path("/mapping")
public class MappingResource {

    private static final Logger LOGGER = Logger.getLogger(MappingResource.class);

    @ConfigProperty(name = "rdf4j.baseuri")
    String baseUri;
    
    @POST
    @Path("/rio/rdf")
    @Consumes("application/xml")
    public Response rdf(File file) throws RDFParseException, UnsupportedRDFormatException, FileNotFoundException, IOException {
        Model model = Rio.parse(new FileInputStream(file), baseUri, RDFFormat.RDFXML);

        model.objects()
            .stream()
            .forEach(LOGGER::info);
        
        return Response.status(200).entity("Done, see log").build();
    }
}
