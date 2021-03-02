// SPDX-FileCopyrightText: 2020 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.resource;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.lfenergy.compas.model.xml.rdf.Rdf;
import org.lfenergy.compas.service.BaseXService;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

@OpenAPIDefinition(
    info = @Info(
        title = "CoMPAS API",
        version = "0.0.1",
        description = "The default API for all CoMPAS operations"
    )
)
@Path("/api")
public class CompasResource {

    private static final Logger LOGGER = Logger.getLogger(CompasResource.class);

    /**
     * Hardcoded BaseX choice
     */
    @Inject
    BaseXService service;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String initial() {
        LOGGER.info("initial");
        return service.executeCommand("list");
    }

    @DELETE
    @Path("/database/{database}")
    public String dropDatabase(@PathParam String database) {
        LOGGER.info("dropDatabase");
        return service.executeCommand("drop db ".concat(database));
    }

    @PUT
    @Path("/database/{database}")
    public String addDatabase(@PathParam String database, String file) {
        LOGGER.info("addDatabase");
        return service.executeCommand("create db ".concat(database).concat(" ").concat(file));
    }

    @POST
    @Path("/database/{database}/query/")
    public String query(@PathParam String database, String query) {
        LOGGER.info("query");
        return service.executeQuery(database, query);
    }

    @POST
    @Path("/command")
    public String command(String command) {
        LOGGER.info("command");
        return service.executeCommand(command);
    }

    @POST
    @Path("/rdf")
    @Consumes("application/xml")
    public Response rdfConfig(Rdf model) {
        LOGGER.info("rdfConfig: got " + model.toString());
        return Response.status(200).entity(model).build();
    }
}