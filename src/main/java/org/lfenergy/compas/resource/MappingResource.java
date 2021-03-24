// SPDX-FileCopyrightText: 2020 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;

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
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Namespace;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.util.Values;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.UnsupportedRDFormatException;
import org.jboss.logging.Logger;
import org.lfenergy.compas.generated.main.SCL;

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
    @Path("/rdf-cim-iec-61850-mapping")
    @Consumes("application/xml")
    @Produces("application/xml")
    public Response rdfCimIec61850Mapping(File file) throws RDFParseException, UnsupportedRDFormatException,
        FileNotFoundException, IOException, JAXBException {

        // Create a root SCL file
        SCL scl = new SCL();

        // Convert incoming RDF file
        Model model = Rio.parse(new FileInputStream(file), baseUri, RDFFormat.RDFXML);
        Set<Namespace> namespaces = model.getNamespaces();

        /*
         * SIMPLE
         * TEST
         */
        getNamespaceByPrefix(namespaces, "md").ifPresent(namespace -> {
            for (final Statement statement : model.filter(null, Values.iri(namespace, "Model.version"), null)) {
                Value object = statement.getObject();
                scl.setVersion(object.stringValue());
            }
        });

        // Get CIM namespace
        Optional<Namespace> cimNamespace = getNamespaceByPrefix(namespaces, "cim");

        cimNamespace.ifPresent(namespace -> {
            // Get the model version
            for (final Statement statement : model.filter(Values.iri(baseUri, "#_REGION_NL"), null, null)) {
                handleStatement(statement);
            }
        });

        File marshalledFile = marshalScl(scl);
        
        return Response.status(200).entity(marshalledFile).build();
    }

    /**
     * Marshall a Java object to a XML file.
     * @param sclModel model SCL java object
     * @return The marshalled XML file
     * @throws JAXBException
     */
    private File marshalScl(SCL sclModel) throws JAXBException {
        // Marshall everything to a XML file and create a SCL output file
        JAXBContext context = JAXBContext.newInstance(SCL.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        File output = new File("output.ssd");
        marshaller.marshal(sclModel, output);

        return output;
    }

    /**
     * Handle a single statement (triple)
     * @param statement the statement to handle
     */
    private void handleStatement(Statement statement) {

        LOGGER.info("handleStatement: handling new statement");
        LOGGER.info("-------------------");

        // Extract
        Resource subj = statement.getSubject();
        IRI pred = statement.getPredicate();
        Value obj = statement.getObject();

        LOGGER.info("subject: " + subj);
        LOGGER.info("predicate: " + pred);
        LOGGER.info("object: " + obj);
    }

    /**
     * Retrieve the possible namespace by prefix
     * @param setOfNamespaces the set of namespaces
     * @return the possible namespace
     */
    private Optional<Namespace> getNamespaceByPrefix(Set<Namespace> setOfNamespaces, String prefix) {
        return setOfNamespaces.stream()
                .filter(namespace -> namespace.getPrefix().equals(prefix))
                .findFirst();
    }
}
