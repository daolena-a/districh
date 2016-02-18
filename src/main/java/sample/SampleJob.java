package sample;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.jms.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by adaolena on 22/01/16.
 */
public class SampleJob {


    ScheduledExecutorService scheduledExecutorService =
            Executors.newScheduledThreadPool(5);

    /**
     * Created by adaolena on 12/01/16.
     */
    public static void main(String[] args) {

        SampleJob m = new SampleJob();
        m.init();
        try {
            // Create a ConnectionFactory
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");

            // Create a Connection
            Connection connection = connectionFactory.createConnection();
            connection.start();

            // Create a Session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create the destination (Topic or Queue)
            Destination destination = session.createQueue("distrische.command");

            // Create a MessageProducer from the Session to the Topic or Queue
            MessageProducer producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            // Create a messages
            String text = "{\n" +
                    "\"classifier\":\"registerCommand\",\n" +
                    "\"serverName\":\"local\"," +
                    "\"jobsType\":[{\n" +
                    "\"cronExpression\":\"0 0/1 * 1/1 * ? *\",\n" +
                    "\"name\":\"printHello\",\n" +
                    "\"params\":[{\n" +
                    "\"key\":\"param1\",\n" +
                    "\"value\":\"pouet\",\n" +
                    "},{\n" +
                    "\"key\":\"param2\",\n" +
                    "\"value\":\"taktak\"\n" +
                    "}]\n" +
                    "}]\n" +
                    "}\n";
            TextMessage message = session.createTextMessage(text);

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

    private Connection connection;
    MessageConsumer consumer;
    LifeThread lifeThread;


    public void init() {
        try {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
            Destination destination = new ActiveMQQueue("local_queue");
            connection = connectionFactory.createConnection();

            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            consumer = session.createConsumer(destination);
            new Thread(new ListenerThread()).start();

            lifeThread = new LifeThread();
            scheduledExecutorService.scheduleAtFixedRate(lifeThread, 0, 30, TimeUnit.SECONDS);
            new Thread(lifeThread).start();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private class ListenerThread implements Runnable {
        AtomicBoolean run = new AtomicBoolean(true);

        @Override
        public void run() {
            while (run.get()) {
                try {
                    Message message = consumer.receive(5000);
                    if (message instanceof TextMessage) {
                        System.out.println("received" + message.toString());
                        TextMessage registerCommand = (TextMessage) message;
                        JSONObject root = (JSONObject) new JSONParser().parse(registerCommand.getText());
                        String jobId = (String) root.get("jobId");
                        System.out.println(root.toString());
                        // Create a Session
                        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                        Destination destination = session.createQueue("distrische.command");
                        MessageProducer producer = session.createProducer(destination);
                        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

                        String text = "{\n" +
                                "\"classifier\":\"jobEndNotifierCommand\",\n" +
                                "\"jobId\":\"" + jobId + "\"," +

                                "}\n";
                        TextMessage send = session.createTextMessage(text);

                        producer.send(send);
                        session.close();


                    }
                } catch (Exception e) {
                    run.set(false);
                    e.printStackTrace();
                }
            }
        }
    }


    private class LifeThread implements Runnable {

        @Override
        public void run() {
            try {
                // Create a Session
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

                // Create the destination (Topic or Queue)
                Destination destination = session.createQueue("distrische.command");

                // Create a MessageProducer from the Session to the Topic or Queue
                MessageProducer producer = session.createProducer(destination);
                producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

                // Create a messages
                String text = "{\n" +
                        "\"classifier\":\"lifeCommand\",\n" +
                        "\"serverName\":\"local\"," +
                        "\"time\":\"" + System.currentTimeMillis() + "\"\n" +
                        "}\n";
                TextMessage message = session.createTextMessage(text);

                // Tell the producer to send the message
                System.out.println("Sent life: " + System.currentTimeMillis());
                producer.send(message);

                // Clean up
                session.close();
            } catch (Exception e) {
                System.out.println("Caught: " + e);
                e.printStackTrace();
            }

        }
    }
}


