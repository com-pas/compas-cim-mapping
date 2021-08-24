// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.rest.v1;

import io.quarkus.security.Authenticated;
import org.lfenergy.compas.cim.mapping.rest.v1.model.MapRequest;
import org.lfenergy.compas.cim.mapping.rest.v1.model.MapResponse;
import org.lfenergy.compas.cim.mapping.service.CompasCimMappingService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Authenticated
@RequestScoped
@Path("/cim/v1/")
public class CompasCimMappingResource {
    private CompasCimMappingService compasCimMappingService;

    @Inject
    public CompasCimMappingResource(CompasCimMappingService compasCimMappingService) {
        this.compasCimMappingService = compasCimMappingService;
    }

    @POST
    @Path("/map")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public MapResponse mapCimToScl(@Valid MapRequest request) {
        var response = new MapResponse();
        response.setScl(compasCimMappingService.map(request.getCimData()));
        return response;
    }
}