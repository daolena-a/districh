package org.distrishe.messaging;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.stereotype.Service;

import javax.jms.*;

/**
 * Created by adaolena on 14/01/16.
 */
@Service
public class MessageSender {
    private Session session = null;
    private Connection connection = null;

    public MessageSender() {
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() throws Exception {

        // Create a ConnectionFactory
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://dev.backend:61616");

        // Create a Connection
        connection = connectionFactory.createConnection();
        connection.start();

        // Create a Session
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    public void sendMessage(String data, String queueName) throws Exception {


        // Create the destination (Topic or Queue)
        Destination destination = session.createQueue(queueName);

        // Create a MessageProducer from the Session to the Topic or Queue
        MessageProducer producer = session.createProducer(destination);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);


        TextMessage message = session.createTextMessage(data);

        // Tell the producer to send the message
        System.out.println("Sent message: " + message.hashCode() + " : " + Thread.currentThread().getName());
        producer.send(message);

        // Clean up
        session.close();
        connection.close();

    }

    public void createQueue(String queueName) throws Exception {
        session.createQueue(queueName);
    }
}
