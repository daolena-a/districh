package org.distrishe.job;

import org.distrishe.topology.ServerRegistered;
import org.eclipse.jetty.server.Server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by adaolena on 13/01/16.
 */
public class JobType {
    Map<String,ServerRegistered> serverRegisteredMap = new HashMap<>();
    String cron;
    Map<String,String> parameters = new HashMap<>();
    private String name;

    public Map<String, ServerRegistered> getServerRegisteredMap() {
        return serverRegisteredMap;
    }

    public void setServerRegisteredMap(Map<String, ServerRegistered> serverRegisteredMap) {
        this.serverRegisteredMap = serverRegisteredMap;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
