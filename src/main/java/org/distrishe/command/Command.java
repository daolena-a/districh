package org.distrishe.command;


import org.json.simple.JSONObject;

/**
 * Created by adaolena on 30/10/15.
 * Command interface should provde a command process and a classifier (name).
 */
public interface  Command {
    String getClassifier();
    Boolean process(JSONObject v);
    boolean hasToProcess(JSONObject v);
}
