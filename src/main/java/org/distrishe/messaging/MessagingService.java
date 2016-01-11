package org.distrishe.messaging;

import org.apache.activemq.broker.BrokerService;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

/**
 * Created by adaolena on 11/01/16.
 */
@Service
public class MessagingService {
    public MessagingService() {
        init();
    }

    private void init() {
        try{
            BrokerService broker = new BrokerService();
            broker.addConnector("tcp://localhost:61616");
            broker.start();
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}
