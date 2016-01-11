package org.distrishe.database;

import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.OServerMain;
import org.springframework.stereotype.Service;

/**
 * Created by adaolena on 11/01/16.
 */
@Service
public class DatabaseService {
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
            e.printStackTrace();
        }
    }


}
