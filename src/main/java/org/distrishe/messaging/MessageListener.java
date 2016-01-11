package org.distrishe.messaging;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.distrishe.command.Command;
import org.distrishe.command.CommandRegistry;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.jms.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by adaolena on 11/01/16.
 */
public class MessageListener {
    @Autowired
    CommandRegistry commandRegistry;
    private Connection connection;
    private String url;
    MessageConsumer consumer;

    public MessageListener(String url) {
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
            new ListenerThread().run();

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

    private class ListenerThread implements Runnable{
        AtomicBoolean run = new AtomicBoolean(true);
        @Override
        public void run() {
            while(run.get()){
                try {
                    Message message = consumer.receive(5000);
                    if(message instanceof  TextMessage){
                        TextMessage registerCommand = (TextMessage) message;
                        JSONObject root = (JSONObject) new JSONParser().parse(registerCommand.getText());
                        String commandClassifier = (String)root.get("classifier");
                        Command command = commandRegistry.get(commandClassifier);
                        if(command.hasToProcess(root)){
                            command.process(root);
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public CommandRegistry getCommandRegistry() {
        return commandRegistry;
    }

    public void setCommandRegistry(CommandRegistry commandRegistry) {
        this.commandRegistry = commandRegistry;
    }
}
