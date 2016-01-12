package org.distrishe.command.register;

import org.distrishe.command.Command;
import org.distrishe.command.CommandRegistry;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class RegisterCommand implements Command {
    @Autowired
    private CommandRegistry registry;

    public RegisterCommand() {

    }

    @PostConstruct
    public void register(){
        getRegistry().put(getClassifier(),this);
    }

    public CommandRegistry getRegistry() {
        return registry;
    }

    public void setRegistry(CommandRegistry registry) {
        this.registry = registry;
    }

    @Override
    public String getClassifier() {
        return "registerCommand";
    }

    @Override
    public Boolean process(JSONObject json) {
        return null;
    }

    @Override
    public boolean hasToProcess(JSONObject json) {
        if(json.containsKey("command") && json.get("command") != null){
            Object o = json.get("command");
            if(o instanceof String){
                if("register".equals((String)o)){
                    return true;
                }
            }
        }
        return false;
    }
}