package org.distrishe.job;

import org.distrishe.topology.ServerRegistered;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import java.util.Comparator;
import java.util.Map;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Created by adaolena on 13/01/16.
 */
@Service
public class DistrischScheduler {
    private SchedulerFactory schedulerFactory;
    private org.quartz.Scheduler scheduler;

    @Autowired
    private JobTypeRegistry jobTypeRegistry;

    @PostConstruct
    public void init() throws Exception{
        //Create instance of factory
        schedulerFactory = new StdSchedulerFactory();

        //Get schedular
        scheduler = schedulerFactory.getScheduler();
        //Start schedular
        scheduler.start();
    }

    public void schedule(Class jobClass, String job, String group, String cronExpression) throws Exception {
        System.out.println("job"+job);
        System.out.println("cron"+cronExpression);
        //Create JobDetail object specifying which Job you want to execute
        JobType jobType = jobTypeRegistry.getByName(job);
        ServerRegistered serverRegisteredToRun = null;
        int min = Integer.MAX_VALUE;
        for (ServerRegistered serverRegistered : jobType.getServerRegistereds()) {
            int running = RunningJobRegister.getNumberOfRunningJob(serverRegistered);
            if(running < min) {
                min = running;
                serverRegisteredToRun = serverRegistered;
            }
        }
        JobDataMap datas = new JobDataMap(serverRegisteredToRun.getParam(job));
        datas.put("server",serverRegisteredToRun);
        datas.put("jobType",serverRegisteredToRun.getJob(job));

        JobDetail jobDetail = newJob(jobClass).withIdentity(job, group).setJobData(datas).build();

        //Associate Trigger to the Job
        //CronTrigger trigger=new CronTriggerImpl("cronTrigger","myJob1","0 0/1 * * * ?");
        Trigger trigger = newTrigger()
                .withIdentity("trigger1", "group1")
                .startNow()
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .build();

        //Pass JobDetail and trigger dependencies to schedular
        scheduler.scheduleJob(jobDetail, trigger);


    }
}
