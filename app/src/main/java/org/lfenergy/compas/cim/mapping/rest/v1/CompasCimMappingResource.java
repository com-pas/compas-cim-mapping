// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.rest.v1;

import io.quarkus.security.Authenticated;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.lfenergy.compas.cim.mapping.rest.UserInfoProperties;
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
    private static final Logger LOGGER = LogManager.getLogger(CompasCimMappingResource.class);

    private CompasCimMappingService compasCimMappingService;

    @Inject
    JsonWebToken jsonWebToken;

    @Inject
    UserInfoProperties userInfoProperties;

    @Inject
    public CompasCimMappingResource(CompasCimMappingService compasCimMappingService) {
        this.compasCimMappingService = compasCimMappingService;
    }

    @POST
    @Path("/map")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public MapResponse map(@Valid MapRequest request) {
        String who = jsonWebToken.getClaim(userInfoProperties.who());
        LOGGER.trace("Username used for Who {}", who);

        var response = new MapResponse();
        response.setScl(compasCimMappingService.map(request.getCimData(), who));
        return response;
    }
}