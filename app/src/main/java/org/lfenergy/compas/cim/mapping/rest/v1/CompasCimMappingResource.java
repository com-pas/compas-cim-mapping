// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.rest.v1;

import org.lfenergy.compas.cim.mapping.rest.model.MapRequest;
import org.lfenergy.compas.cim.mapping.rest.model.MapResponse;
import org.lfenergy.compas.cim.mapping.service.CompasCimMappingService;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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
    public MapResponse getMessage(MapRequest request) {
        var response = new MapResponse();
        response.setScl(compasCimMappingService.map(request.getCimData()));
        return response;
    }
}