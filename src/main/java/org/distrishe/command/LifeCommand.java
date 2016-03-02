package org.distrishe.command;

import org.distrishe.topology.ServerRegistered;
import org.distrishe.topology.ServerRegistry;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by adaolena on 22/01/16.
 */
@Component
public class LifeCommand  implements Command {
    public static Logger logger = LoggerFactory.getLogger(LifeCommand.class);
    @Autowired
    private ServerRegistry serverRegistry;

    @Autowired
    private CommandRegistry registry;

    @Override
    public String getClassifier() {
        return "lifeCommand";
    }

    @Override
    public Boolean process(JSONObject v) {
        long time = Long.valueOf((String)v.get("time"));
        String serverName = (String) v.get("serverName");
        ServerRegistered server = serverRegistry.get(serverName);
        if(server != null){
            long currentTime = System.currentTimeMillis();
            if((currentTime - time) > 10000){
                // platform is slow
                logger.info("The scheduler seems to be slow...");
            }
            server.updateLastSeen(time);
            return true;
        } else{
            logger.info("server not exists: "+serverName);
            return true;
        }

    }

    @Override
    public boolean hasToProcess(JSONObject v) {
        return true;
    }

    @PostConstruct
    public void register() {
        registry.put(getClassifier(), this);
    }

    public CommandRegistry getRegistry() {
        return registry;
    }

    public void setRegistry(CommandRegistry registry) {
        this.registry = registry;
    }
}
