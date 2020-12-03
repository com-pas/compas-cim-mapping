package com.alliander.resource;

import java.io.IOException;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.alliander.service.BaseXService;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

@OpenAPIDefinition(
    info = @Info(
        title = "CoMPAS API",
        version = "0.0.1",
        description = "The default API for all CoMPAS operations"
    )
)
@Path("/compas")
public class CompasResource {

    /**
     * Hardcoded BaseX choice
     */
    @Inject
    BaseXService service;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String initial() throws IOException {
        return service.executeCommand("list");
    }

    @DELETE
    @Path("/{database}")
    public String dropDatabase(@PathParam String database) throws IOException {
        return service.executeCommand("drop db ".concat(database));
    }

    @PUT
    @Path("/{database}")
    public String addDatabase(@PathParam String database, String file) throws IOException {
        return service.executeCommand("create db ".concat(database).concat(" ").concat(file));
    }

    @POST
    @Path("/{database}/query/")
    public String query(@PathParam String database, String query) throws IOException {
        return service.executeQuery(database, query);
    }

    @POST
    @Path("/command")
    public String command(String command) throws IOException {
        return service.executeCommand(command);
    }
}