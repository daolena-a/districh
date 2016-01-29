package org.distrishe.topology;

import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by adaolena on 18/01/16.
 */
@Service
public class ServerRegistry {
    private Map<String, ServerRegistered> servers = new ConcurrentHashMap<>();

    public void addServer(ServerRegistered server){
        servers.put(server.getName(),server);
    }

    public ServerRegistered get(String serverName){
        return servers.get(serverName);
    }

    public Collection<ServerRegistered> getAll(){
        return servers.values();
    }

}
