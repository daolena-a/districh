package org.distrishe.web;

import spark.ModelAndView;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.before;
import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.post;
import static spark.Spark.*;

/**
 * Created by adaolena on 11/01/16.
 */
public class WebConfig {
    public WebConfig() {
        staticFileLocation("/public");
        setupRoutes();
    }
    private void setupRoutes() {
        get("/", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            return 	new ModelAndView(map, "timeline.ftl");
        });
    }
}
