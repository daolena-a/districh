package org.distrishe.database;

import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.OServerMain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by adaolena on 11/01/16.
 */
@Service
public class DatabaseService {

    public static Logger logger = LoggerFactory.getLogger(DatabaseService.class);

    public DatabaseService() {
        init();
    }

    private void init(){
        OServer server = null;
        try {
            server = OServerMain.create();
            server.startup(getClass().getResourceAsStream("db.xml"));
            server.activate();
        } catch (Exception e) {
           logger.error("error starting orient db", e);
        }
    }


}
