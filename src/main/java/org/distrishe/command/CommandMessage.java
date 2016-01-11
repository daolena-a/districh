package org.distrishe.command;

import org.json.simple.JSONObject;

/**
 * Created by adaolena on 31/12/15.
 */
public abstract class CommandMessage {
    JSONObject message;

    public CommandMessage(JSONObject object) {
        message = object;
    }

    public abstract String getClassifier();

    protected abstract void extract(JSONObject json);


}
