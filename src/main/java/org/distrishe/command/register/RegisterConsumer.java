package org.distrishe.command.register;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.jms.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by adaolena on 31/12/15.
 */
public class RegisterConsumer {
    private Connection connection;
    private String url;
    MessageConsumer consumer;

    public RegisterConsumer(String url) {
        this.url = url;
    }

    @PostConstruct
    public void init() {
        try {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
            Destination destination = new ActiveMQQueue("distrishe.command");
            connection = connectionFactory.createConnection();
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            consumer = session.createConsumer(destination);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @PreDestroy
    public void cleanup() {
        try {
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private class RegisterThread implements Runnable{
        AtomicBoolean run = new AtomicBoolean(true);
        @Override
        public void run() {
            while(run.get()){
                try {
                    Message message = consumer.receive(5000);
                    if(message instanceof  TextMessage){
                        TextMessage registerCommand = (TextMessage) message;
                        JSONObject root = (JSONObject) new JSONParser().parse(registerCommand.getText());

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}