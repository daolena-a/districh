package org.distrishe.job;

import org.distrishe.topology.ServerRegistered;
import org.eclipse.jetty.server.Server;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by adaolena on 25/01/16.
 */
public class RunningJobRegister {
    static private Map<ServerRegistered,RunningJob> registry = new ConcurrentHashMap<>();

    public static void putRunningJob(JobType jobType, ServerRegistered serverRegistered){
        RunningJob runningJob = new RunningJob();
        runningJob.setRegistered(serverRegistered);
        runningJob.setJob(jobType);
        runningJob.setStartData(System.currentTimeMillis());
        registry.put(serverRegistered,runningJob);
    }

}
