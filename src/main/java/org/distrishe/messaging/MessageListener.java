package org.distrishe.messaging;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.distrishe.command.Command;
import org.distrishe.command.CommandRegistry;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.jms.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by adaolena on 11/01/16.
 */
@Service
public class MessageListener {

    public static Logger LOGGER = LoggerFactory.getLogger(MessageListener.class);

    @Autowired
    private CommandRegistry commandRegistry;

    private Connection connection;
    private String url = "tcp://localhost:61616";
    MessageConsumer consumer;

    public MessageListener(){
    }

    @PostConstruct
    public void init() {
        try {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
            Destination destination = new ActiveMQQueue("distrische.command");
            connection = connectionFactory.createConnection();

            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            consumer = session.createConsumer(destination);
            new Thread(new ListenerThread()).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void cleanup() {
        try {
            connection.close();
            run.set(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    AtomicBoolean run = new AtomicBoolean(true);
    private class ListenerThread implements Runnable{
        @Override
        public void run() {
            while(run.get()){
                try {
                    Message message = consumer.receive(5000);
                    if(message instanceof  TextMessage){
                        System.out.println("received" + message.toString());
                        TextMessage registerCommand = (TextMessage) message;
                        JSONObject root = (JSONObject) new JSONParser().parse(registerCommand.getText());
                        String commandClassifier = (String)root.get("classifier");
                        System.out.println(commandClassifier);
                        System.out.println(getCommandRegistry().size());
                        Command command = getCommandRegistry().get(commandClassifier);
                        if(command.hasToProcess(root)){
                            command.process(root);
                        }
                        if(command == null){
                            LOGGER.error("Command not found for classifier {}", commandClassifier);
                        }

                    }
                } catch (Exception e) {
                    LOGGER.error("Error in message listener thread", e);
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
