package org.distrishe.messaging;

import org.apache.activemq.broker.BrokerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by adaolena on 11/01/16.
 */
@Service
public class MessagingService {
    public static Logger LOGGER = LoggerFactory.getLogger(MessageListener.class);

    public MessagingService() {
        init();
    }

    private void init() {
        try {
            BrokerService broker = new BrokerService();
            broker.addConnector("tcp://localhost:61616");
            broker.start();
        } catch (Exception e) {
            LOGGER.error("Error in starting activemq broker", e);
        }

    }


}
