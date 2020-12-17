package org.lfenergy.compas.service;

import java.io.IOException;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.lfenergy.compas.database.BaseXClient;
import org.lfenergy.compas.database.BaseXClient.Query;

@ApplicationScoped
public class BaseXService implements DatabaseService {

    private static final Logger LOGGER = Logger.getLogger(BaseXService.class);

    @ConfigProperty(name = "basex.host")
    private String baseXHost;

    @ConfigProperty(name = "basex.port")
    private Integer baseXPort;

    @ConfigProperty(name = "basex.username")
    private String baseXUsername;

    @ConfigProperty(name = "basex.password")
    private String baseXPassword;

    @Override
    public String executeCommand(String command) {
        try (BaseXClient client = new BaseXClient(baseXHost, baseXPort, baseXUsername, baseXPassword)) {
            return client.execute(command);
        } catch (IOException exception) {
            final String exceptionMessage = exception.getLocalizedMessage();
            LOGGER.errorv("executeCommand: {0}", exceptionMessage);
            return exceptionMessage;
        }
    }

    @Override
    public String executeQuery(String database, String query) {
        try (BaseXClient client = new BaseXClient(baseXHost, baseXPort, baseXUsername, baseXPassword)) {
            StringBuilder response = new StringBuilder();
            client.execute("open ".concat(database));
            try (Query queryToRun = client.query(query)) {
                while(queryToRun.more()) {
                    response.append(queryToRun.next());
                }
            }
            client.execute("close");
            return response.toString();
        }catch (IOException exception) {
            final String exceptionMessage = exception.getLocalizedMessage();
            LOGGER.errorv("executeQuery: {0}", exceptionMessage);
            return exceptionMessage;
        }
    }

}