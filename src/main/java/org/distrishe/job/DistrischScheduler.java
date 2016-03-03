package org.distrishe.job;

import org.distrishe.topology.ServerRegistered;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Created by adaolena on 13/01/16.
 */
@Service
public class DistrischScheduler {

    public static Logger LOGGER = LoggerFactory.getLogger(DistrischJob.class);
    private SchedulerFactory schedulerFactory;
    private org.quartz.Scheduler scheduler;

    @Autowired
    private  RunningJobRegister runningJobRegister;
    @Autowired
    private JobTypeRegistry jobTypeRegistry;

    public JobTypeRegistry getJobTypeRegistry() {
        return jobTypeRegistry;
    }

    public void setJobTypeRegistry(JobTypeRegistry jobTypeRegistry) {
        this.jobTypeRegistry = jobTypeRegistry;
    }

    public RunningJobRegister getRunningJobRegister() {
        return runningJobRegister;
    }

    public void setRunningJobRegister(RunningJobRegister runningJobRegister) {
        this.runningJobRegister = runningJobRegister;
    }

    @PostConstruct
    public void init() throws Exception {
        schedulerFactory = new StdSchedulerFactory();
        scheduler = schedulerFactory.getScheduler();
        scheduler.start();
    }

    public void schedule(Class jobClass, String job, String group, String cronExpression) throws Exception {

        LOGGER.info("scheduling job" + job);
        LOGGER.info("cron" + cronExpression);
        //Create JobDetail object specifying which Job you want to execute
        JobType jobType = jobTypeRegistry.getByName(job);
        ServerRegistered serverRegisteredToRun = null;
        int min = Integer.MAX_VALUE;
        if (jobType.getServerRegistereds() != null) {
            for (ServerRegistered serverRegistered : jobType.getServerRegistereds()) {
                int running = runningJobRegister.getNumberOfRunningJob(serverRegistered);
                if (running < min) {
                    min = running;
                    serverRegisteredToRun = serverRegistered;
                }
            }
        }
        if (serverRegisteredToRun != null) {
            JobDataMap datas = new JobDataMap(serverRegisteredToRun.getParam(job));
            datas.put("server", serverRegisteredToRun);
            datas.put("jobType", serverRegisteredToRun.getJob(job));

            JobDetail jobDetail = newJob(jobClass).withIdentity(job, group).setJobData(datas).build();

            //Associate Trigger to the Job
            //CronTrigger trigger=new CronTriggerImpl("cronTrigger","myJob1","0 0/1 * * * ?");
            Trigger trigger = newTrigger()
                    .withIdentity("trigger1", "group1")
                    .startNow()
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                    .build();

            //Pass JobDetail and trigger dependencies to scheduler
            scheduler.scheduleJob(jobDetail, trigger);
        }


    }
}
