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
        JobDataMap dataMap = jobExecutionContext.getMergedJobDataMap();
        ServerRegistered serverRegistered = (ServerRegistered) dataMap.get("server");
        JobType job = (JobType) dataMap.get("server");
        try {


            // Create a messages
            JSONObject json = new JSONObject();
            json.put("classifier","startJob");
            json.put("jobType",job.getName());

            // Tell the producer to send the message
            MessageSender service = new MessageSender();
            service.sendMessage(json.toJSONString(),serverRegistered.getQueueName());

        } catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }
    }


}
