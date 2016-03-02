package org.distrishe.job;

import org.distrishe.messaging.MessageSender;
import org.distrishe.topology.ServerRegistered;
import org.json.simple.JSONObject;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by adaolena on 13/01/16.
 */
public class DistrischJob implements Job {
    public static Logger LOGGER = LoggerFactory.getLogger(DistrischJob.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {


        JobDataMap dataMap = jobExecutionContext.getMergedJobDataMap();
        ServerRegistered serverRegistered = (ServerRegistered) dataMap.get("server");
        if (serverRegistered.getLastSeen() > 0 && System.currentTimeMillis() - serverRegistered.getLastSeen() > 60000) {
            //log error
            LOGGER.error("long time no see {}", serverRegistered.getName());
            return;
        }
        JobType job = (JobType) dataMap.get("jobType");
        LOGGER.info("job {}, has triggered", job.getName());
        int min = Integer.MAX_VALUE;
        /**
         * Just a round robin test
         */
        if (job.getServerRegistereds() != null) {
            for (ServerRegistered server : job.getServerRegistereds()) {
                int running = RunningJobRegister.getNumberOfRunningJob(serverRegistered);
                if (running < min) {
                    LOGGER.info("chosing between "+server.getName()+running);


                    min = running;
                    serverRegistered = server;
                }else{
                    LOGGER.info("not chose "+server.getName()+running);

                }
            }
        }
        if(min == 0) {
            min = Integer.MAX_VALUE;
            for (ServerRegistered server : job.getServerRegistereds()) {
                if(server.getNumberOfJobRunned().get() < min){
                    LOGGER.info("chosing between "+server.getName()+server.getNumberOfJobRunned().get());

                    serverRegistered = server;
                    min = server.getNumberOfJobRunned().get();
                }else{
                    LOGGER.info("not chose "+server.getName()+server.getNumberOfJobRunned().get());

                }
            }
        }

        LOGGER.info("chose "+serverRegistered.getName());
        RunningJob runningJob = RunningJobRegister.putRunningJob(job, serverRegistered);
        try {
            // Create a messages
            JSONObject json = new JSONObject();
            json.put("classifier", "startJob");
            json.put("jobType", job.getName());
            json.put("jobId", runningJob.getId().toString());
            JSONObject paramO = new JSONObject();
            Map<String, String> param = serverRegistered.getConf(job.getName());
            param.forEach((key, value) -> {
                paramO.put(key, value);
            });

            json.put("parameters", paramO);

            // Tell the producer to send the message
            MessageSender service = new MessageSender();
            service.sendMessage(json.toJSONString(), serverRegistered.getQueueName());

        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }


}
