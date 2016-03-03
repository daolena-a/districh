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
 * Running job register
 * TODO make it a spring service
 */
@Service
public class RunningJobRegister {
     private Map<UUID,RunningJob> registry = new ConcurrentHashMap<>();

    private  Lock masterLock = new ReentrantLock();
    private  Map<String, AtomicInteger> NUMBER_OF_JOB_BY_SERVER = new ConcurrentHashMap<>();

    /**
     * Put a  job in the running map.
     * @param jobType The job type
     * @param serverRegistered The server which is running the job
     * @return a RunningJob object.
     */
    public RunningJob putRunningJob(JobType jobType, ServerRegistered serverRegistered){
        RunningJob runningJob = new RunningJob();
        runningJob.setRegistered(serverRegistered);
        runningJob.setJob(jobType);
        runningJob.setStartData(System.currentTimeMillis());
        registry.put(runningJob.getId(),runningJob);
        if(NUMBER_OF_JOB_BY_SERVER.get(serverRegistered.getName()) == null){
            masterLock.lock();
            if(NUMBER_OF_JOB_BY_SERVER.get(serverRegistered.getName()) == null){
                NUMBER_OF_JOB_BY_SERVER.put(serverRegistered.getName(),new AtomicInteger(1));
            }
            serverRegistered.getNumberOfJobRunned().incrementAndGet();
            masterLock.unlock();
        }else{
            AtomicInteger numberOfRunningJobs = NUMBER_OF_JOB_BY_SERVER.get(serverRegistered.getName());
            numberOfRunningJobs.incrementAndGet();
            serverRegistered.getNumberOfJobRunned().incrementAndGet();
        }

        return runningJob;
    }

    /**
     * Remove a job from the running state
     * @param uuid the job id.
     */
    public void removeJob(String uuid){
        UUID id = UUID.fromString(uuid);
        RunningJob runningJob = registry.get(id);
        runningJob.setEndDate(System.currentTimeMillis());
        ServerRegistered serverRegistered = runningJob.getRegistered();
        registry.remove(id);
        AtomicInteger numberOfRunningJobs = NUMBER_OF_JOB_BY_SERVER.get(serverRegistered.getName());
        numberOfRunningJobs.decrementAndGet();
    }

    /**
     * Return the number of running job currently processing by the server in parameter
     * @param serverRegistered a server
     * @return the number of running job.
     */
    public int getNumberOfRunningJob(ServerRegistered serverRegistered){
        AtomicInteger number = NUMBER_OF_JOB_BY_SERVER.get(serverRegistered.getName());
        if(number != null){
            return number.get();
        }else{
            return 0;
        }
    }
}
