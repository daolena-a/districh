package org.distrishe.job;

import org.distrishe.topology.ServerRegistered;
import org.eclipse.jetty.server.Server;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by adaolena on 25/01/16.
 */
public class RunningJobRegister {
    static private Map<UUID,RunningJob> registry = new ConcurrentHashMap<>();

    public static RunningJob putRunningJob(JobType jobType, ServerRegistered serverRegistered){
        RunningJob runningJob = new RunningJob();
        runningJob.setRegistered(serverRegistered);
        runningJob.setJob(jobType);
        runningJob.setStartData(System.currentTimeMillis());
        registry.put(runningJob.getId(),runningJob);
        return runningJob;
    }
    public static void removeJob(String uuid){
        UUID id = UUID.fromString(uuid);
        registry.remove(id);
    }
}
