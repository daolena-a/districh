package org.distrishe.topology;

import org.distrishe.job.JobType;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by adaolena on 13/01/16.
 */
public class ServerRegistered {
    private String queueName;
    Map<String, JobType> jobs = new HashMap<>();
    Map<String, Map<String,String>> jobsConfs = new ConcurrentHashMap<>();

    private long lastSeen = 0;
    private String name;

    public ServerRegistered(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addJob(JobType jobType) {
        if (!jobs.containsKey(jobType.getName())) {
            jobs.put(jobType.getName(), jobType);
            jobsConfs.put(jobType.getName(),new ConcurrentHashMap<>());
        }
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public Collection<JobType> getJobs() {
        return jobs.values();
    }

    public JobType getJob(String name) {
        return jobs.get(name);
    }

    public void updateLastSeen(long timestamp) {
        lastSeen = timestamp;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public void addConf(String name, String key , String value){
        if(jobsConfs.containsKey(name)){
            jobsConfs.get(name).put(key,value);
        }
    }

    public Map<String,String> getParam(String jobTypeName){
        return jobsConfs.get(jobTypeName);
    }
}
