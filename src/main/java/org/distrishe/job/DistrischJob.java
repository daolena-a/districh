package org.distrishe.job;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.distrishe.messaging.MessageSender;
import org.distrishe.messaging.MessagingService;
import org.distrishe.topology.ServerRegistered;
import org.json.simple.JSONObject;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.jms.*;

/**
 * Created by adaolena on 13/01/16.
 */
public class DistrischJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        System.out.println("triggered");
        JobDataMap dataMap = jobExecutionContext.getMergedJobDataMap();
        ServerRegistered serverRegistered = (ServerRegistered) dataMap.get("server");
        if(serverRegistered.getLastSeen() > 0 && System.currentTimeMillis() - serverRegistered.getLastSeen() > 60000 ){
            //log error
            return;
        }
        JobType job = (JobType) dataMap.get("jobType");
        RunningJob runningJob = RunningJobRegister.putRunningJob(job, serverRegistered);
        try {
            // Create a messages
            JSONObject json = new JSONObject();
            json.put("classifier","startJob");
            json.put("jobType",job.getName());
            json.put("jobId",runningJob.getId().toString());
            JSONObject paramO = new JSONObject();
            for(String key : dataMap.keySet()){
                if(dataMap.get(key) instanceof String){
                    paramO.put(key,dataMap.get(key).toString());
                }
            }
            json.put("parameters",paramO);

            // Tell the producer to send the message
            MessageSender service = new MessageSender();
            service.sendMessage(json.toJSONString(),serverRegistered.getQueueName());

        } catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }
    }


}
