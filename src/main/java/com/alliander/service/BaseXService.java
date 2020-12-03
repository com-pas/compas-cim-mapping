package com.alliander.service;

import java.io.IOException;

import javax.enterprise.context.ApplicationScoped;

import com.alliander.database.BaseXClient;
import com.alliander.database.BaseXClient.Query;

@ApplicationScoped
public class BaseXService implements DatabaseService {

    @Override
    public String executeCommand(String command) {
        try (BaseXClient client = new BaseXClient("localhost", 1984, "admin", "admin")) {
            return client.execute(command);
        } catch (IOException exception) {
            return exception.getLocalizedMessage();
        }
    }

    @Override
    public String executeQuery(String database, String query) {
        try (BaseXClient client = new BaseXClient("localhost", 1984, "admin", "admin")) {
            String response = "";
            client.execute("open ".concat(database));
            try (Query queryToRun = client.query(query)) {
                while(queryToRun.more()) {
                    response += queryToRun.next();
                }
            }
            client.execute("close");
            return response;
        }catch (IOException exception) {
            return exception.getLocalizedMessage();
        }
    }

}