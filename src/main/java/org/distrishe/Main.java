package org.distrishe;

import org.distrishe.web.WebConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by adaolena on 31/12/15.
 */

@Configuration
@ComponentScan({ "org.distrishe" })
public class Main {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(Main.class);

        ctx.registerShutdownHook();
    }
}

