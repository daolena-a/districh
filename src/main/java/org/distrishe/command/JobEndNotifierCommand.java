package org.distrishe.command;

import org.distrishe.job.RunningJobRegister;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


/**
 * Created by adaolena on 25/01/16.
 * Process an end job message sent once a job is done
 */
@Component
public class JobEndNotifierCommand implements Command {

    public static Logger logger = LoggerFactory.getLogger(JobEndNotifierCommand.class);
    @Autowired
    private CommandRegistry registry;
    @Autowired
    private RunningJobRegister runningJobRegister;


    @Override
    public String getClassifier() {
        return "jobEndNotifierCommand";
    }

    @Override
    public Boolean process(JSONObject v) {
        String jobId = (String) v.get("jobId");
        logger.info("job " + jobId + " ends");
        runningJobRegister.removeJob(jobId);
        return true;
    }

    @Override
    public boolean hasToProcess(JSONObject v) {
        return true;
    }

    @PostConstruct
    public void register() {
        registry.put(getClassifier(), this);
    }

    public CommandRegistry getRegistry() {
        return registry;
    }

    public RunningJobRegister getRunningJobRegister() {
        return runningJobRegister;
    }

    public void setRunningJobRegister(RunningJobRegister runningJobRegister) {
        this.runningJobRegister = runningJobRegister;
    }

    public void setRegistry(CommandRegistry registry) {
        this.registry = registry;
    }
}
