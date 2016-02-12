package org.distrishe.job;

import org.distrishe.topology.ServerRegistered;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by adaolena on 13/01/16.
 */
public class JobType {

    Map<String, ServerRegistered> serverRegisteredMap = new HashMap<>();
    String cron;
    List<ServerRegistered> serverRegistereds = new ArrayList<>();

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


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ServerRegistered> getServerRegistereds() {
        return serverRegistereds;
    }

    public void setServerRegistereds(List<ServerRegistered> serverRegistereds) {
        this.serverRegistereds = serverRegistereds;
    }
}
