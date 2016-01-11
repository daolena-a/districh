package org.distrishe.command;


import org.json.simple.JSONObject;

/**
 * Created by adaolena on 30/10/15.
 */
public interface  Command {
    String getClassifier();
    Boolean process(JSONObject v);
    boolean hasToProcess(JSONObject v);
}
