package org.distrishe.job;

import org.distrishe.topology.ServerRegistered;

/**
 * Created by adaolena on 13/01/16.
 */
public class RunningJob {
    private ServerRegistered registered;
    private JobType job;
    private long startData;
    private long endDate;

    public ServerRegistered getRegistered() {
        return registered;
    }

    public void setRegistered(ServerRegistered registered) {
        this.registered = registered;
    }

    public JobType getJob() {
        return job;
    }

    public void setJob(JobType job) {
        this.job = job;
    }

    public long getStartData() {
        return startData;
    }

    public void setStartData(long startData) {
        this.startData = startData;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }
}
