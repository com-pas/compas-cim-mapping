// SPDX-FileCopyrightText: 2020 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.resource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.UnsupportedRDFormatException;

import org.lfenergy.compas.model.SclRoot;

@OpenAPIDefinition(
    info = @Info(
        title = "Mapping API",
        version = "0.0.1",
        description = "Using this API, you can do some mapping actions."
    )
)
@Path("/mapping")
public class MappingResource {

    // private static final Logger LOGGER = Logger.getLogger(MappingResource.class);

    @ConfigProperty(name = "rdf4j.baseuri")
    String baseUri;
    
    @POST
    @Path("/cim2iec61850")
    @Consumes("application/xml")
    @Produces("application/xml")
    public Response cim2iec61850(File file) throws RDFParseException, UnsupportedRDFormatException,
        FileNotFoundException, IOException, JAXBException {
        // Model model = Rio.parse(new FileInputStream(file), baseUri, RDFFormat.RDFXML);
        // model.filter(subj, pred, obj, contexts)
        // model.stream()
        //     .forEach(LOGGER::info);

        // Get a root SCL file (without full mapping)
        SclRoot scl = new SclRoot();

        // Marshall everything to a XML file and create a SCL output file
        JAXBContext context = JAXBContext.newInstance(SclRoot.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        File output = new File("output.scl");
        marshaller.marshal(scl, output);
        
        return Response.status(200).entity(output).build();
    }
}
