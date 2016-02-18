package org.distrishe.job;

import org.distrishe.topology.ServerRegistered;
import org.eclipse.jetty.server.Server;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by adaolena on 25/01/16.
 */
public class RunningJobRegister {
    static private Map<UUID,RunningJob> registry = new ConcurrentHashMap<>();

    private static Lock masterLock = new ReentrantLock();
    private static Map<String, AtomicInteger> NUMBER_OF_JOB_BY_SERVER = new ConcurrentHashMap<>();

    public static RunningJob putRunningJob(JobType jobType, ServerRegistered serverRegistered){
        RunningJob runningJob = new RunningJob();
        runningJob.setRegistered(serverRegistered);
        runningJob.setJob(jobType);
        runningJob.setStartData(System.currentTimeMillis());
        registry.put(runningJob.getId(),runningJob);
        if(NUMBER_OF_JOB_BY_SERVER.get(serverRegistered.getName()) == null){
            masterLock.lock();
            if(NUMBER_OF_JOB_BY_SERVER.get(serverRegistered.getName()) == null){
                NUMBER_OF_JOB_BY_SERVER.put(serverRegistered.getName(),new AtomicInteger(0));
            }
            masterLock.unlock();
        }else{
            AtomicInteger numberOfRunningJobs = NUMBER_OF_JOB_BY_SERVER.get(serverRegistered.getName());
            numberOfRunningJobs.incrementAndGet();
        }

        return runningJob;
    }


    public static void removeJob(String uuid){
        UUID id = UUID.fromString(uuid);
        RunningJob runningJob = registry.get(id);
        ServerRegistered serverRegistered = runningJob.getRegistered();
        registry.remove(id);
        AtomicInteger numberOfRunningJobs = NUMBER_OF_JOB_BY_SERVER.get(serverRegistered.getName());
        numberOfRunningJobs.decrementAndGet();
    }

    public static int getNumberOfRunningJob(ServerRegistered serverRegistered){
        AtomicInteger number = NUMBER_OF_JOB_BY_SERVER.get(serverRegistered.getName());
        if(number != null){
            return number.get();
        }else{
            return 0;
        }
    }
}
