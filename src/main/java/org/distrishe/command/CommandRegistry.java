package org.distrishe.command;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by adaolena on 11/01/16.
 */
@Service
@Scope("singleton")
public class CommandRegistry {
    Map<String,Command> commandRegistry = new ConcurrentHashMap<>();

    public Command get(String key){
        return commandRegistry.get(key);
    }

    public Command put(String key, Command command){
        return commandRegistry.put(key,command);
    }

    public int size(){
        return commandRegistry.size();
    }
}
