package org.distrishe.command.register;

import org.distrishe.command.Command;
import org.json.simple.JSONObject;

public class RegisterCommand implements Command {
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