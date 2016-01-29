package org.distrishe.job;

import org.distrishe.topology.ServerRegistered;
import org.json.simple.JSONObject;

/**
 * Created by adaolena on 29/01/16.
 */
public class JobUtil {
    public JSONObject createStartJobCommandJson(JobType jobType, ServerRegistered serverRegistered) {
        RunningJob runningJob = RunningJobRegister.putRunningJob(jobType, serverRegistered);
        JSONObject json = new JSONObject();
        json.put("classifier", "startJob");
        json.put("jobType", jobType.getName());
        json.put("jobId", runningJob.getId().toString());
        JSONObject paramO = new JSONObject();
        for (String key : jobType.getParameters().keySet()) {
            paramO.put(key, jobType.getParameters().get(key).toString());

        }
        json.put("parameters", paramO);
        return json;
    }
}
