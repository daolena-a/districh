package org.distrishe.job;

import org.apache.activemq.ActiveMQConnectionFactory;
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
            // Create a ConnectionFactory
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://dev.backend:61616");

            // Create a Connection
            Connection connection = connectionFactory.createConnection();
            connection.start();

            // Create a Session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create the destination (Topic or Queue)
            Destination destination = session.createQueue(serverRegistered.getQueueName());

            // Create a MessageProducer from the Session to the Topic or Queue
            MessageProducer producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            // Create a messages
            JSONObject json = new JSONObject();
            json.put("classifier","startJob");
            json.put("jobType",job.getName());
            TextMessage message = session.createTextMessage(json.toJSONString());

            // Tell the producer to send the message
            System.out.println("Sent message: " + message.hashCode() + " : " + Thread.currentThread().getName());
            producer.send(message);

            // Clean up
            session.close();
            connection.close();
        } catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }
    }
}
