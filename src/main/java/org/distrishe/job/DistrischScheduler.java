package org.distrishe.job;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Created by adaolena on 13/01/16.
 */
@Service
public class DistrischScheduler {
    private SchedulerFactory schedulerFactory;
    private org.quartz.Scheduler scheduler;
    @PostConstruct
    public void init() throws Exception{
        //Create instance of factory
        schedulerFactory = new StdSchedulerFactory();

        //Get schedular
        scheduler = schedulerFactory.getScheduler();
    }

    public void schedule(Class jobClass, String job, String group, String cronExpression) throws Exception {
        //Create JobDetail object specifying which Job you want to execute
        JobDetail jobDetail = newJob(jobClass).withIdentity(job, group).build();

        //Associate Trigger to the Job
        //CronTrigger trigger=new CronTriggerImpl("cronTrigger","myJob1","0 0/1 * * * ?");
        Trigger trigger = newTrigger()
                .withIdentity("trigger1", "group1")
                .startNow()
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .build();

        //Pass JobDetail and trigger dependencies to schedular
        scheduler.scheduleJob(jobDetail, trigger);

        //Start schedular
        scheduler.start();
    }
}
