package org.distrishe.web;

import org.distrishe.job.JobType;
import org.distrishe.job.JobTypeRegistry;
import org.distrishe.topology.ServerRegistered;
import org.distrishe.topology.ServerRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import spark.ModelAndView;
import spark.template.thymeleaf.ThymeleafTemplateEngine;

import java.util.*;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.*;

/**
 * Created by adaolena on 11/01/16.
 * The web config.
 */
@Component
public class WebConfig {

    @Autowired
    private ServerRegistry serverRegistry;

    /**
     * Constructor set spark web route and conf.
     */
    public WebConfig() {
        staticFileLocation("/public");
        setupRoutes();
    }


    private void setupRoutes() {
        get("/", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            return 	new ModelAndView(map, "timeline.ftl");
        });

        get("/servers", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("servers",serverRegistry.getAll());
           return new ModelAndView(map, "server");
        }, new ThymeleafTemplateEngine());

        get("/jobs", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            List<JobType> values = new ArrayList<JobType>();
            for(ServerRegistered serverRegistered : serverRegistry.getAll()){
                values.addAll(serverRegistered.getJobs());
            }

            map.put("jobs",values);
            return 	new ModelAndView(map, "jobs");
        }, new ThymeleafTemplateEngine());


        post("/postCommand", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            return new ModelAndView(map, "timeline.ftl");
        });
    }

    /**
     *  Return the ServerRegistry
     * @return the server registry
     */
    public ServerRegistry getServerRegistry() {
        return serverRegistry;
    }

    /**
     *  Set the server registry
     * @param serverRegistry the server registry.
     */
    public void setServerRegistry(ServerRegistry serverRegistry) {
        this.serverRegistry = serverRegistry;
    }


}
