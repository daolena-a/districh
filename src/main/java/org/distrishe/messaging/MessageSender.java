package org.distrishe.messaging;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.jms.*;

/**
 * Created by adaolena on 14/01/16.
 */
@Service
public class MessageSender {

    public static Logger LOGGER = LoggerFactory.getLogger(MessageListener.class);

    private Session session = null;
    private Connection connection = null;

    public MessageSender() {
        try {
            init();
        } catch (Exception e) {
            LOGGER.error("",e);
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

        producer.send(message);
        LOGGER.info("Message sent {}", data);

        // Clean up
        session.close();
        connection.close();

    }

    /**
     * Create queue
     * @param queueName
     * @throws Exception
     */
    public void createQueue(String queueName) throws Exception {
        session.createQueue(queueName);
    }
}
