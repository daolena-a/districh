package org.distrishe.command;

import org.json.simple.JSONObject;

/**
 * Created by adaolena on 25/01/16.
 */
public class JobEndNotifierCommand implements Command {
    @Override
    public String getClassifier() {
        return null;
    }

    @Override
    public Boolean process(JSONObject v) {
        return null;
    }

    @Override
    public boolean hasToProcess(JSONObject v) {
        return false;
    }
}
