package org.distrishe.topology;

import org.distrishe.job.JobType;

import java.util.*;

/**
 * Created by adaolena on 13/01/16.
 */
public class ServerRegistered {
    private String queueName;
    Map<String,JobType> jobs = new HashMap<>();
    private String name;

    public ServerRegistered(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addJob(JobType jobType){
        jobs.put(jobType.getName(), jobType);
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public Collection<JobType> getJobs(){
        return jobs.values();
    }

    public JobType getJob(String name){
        return jobs.get(name);
    }
}
