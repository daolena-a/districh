package org.distrishe.command.register;

import org.distrishe.command.Command;
import org.distrishe.command.CommandRegistry;
import org.distrishe.job.DistrischJob;
import org.distrishe.job.DistrischScheduler;
import org.distrishe.job.JobType;
import org.distrishe.topology.ServerRegistered;
import org.distrishe.topology.ServerRegistry;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RegisterCommand implements Command {
    @Autowired
    private CommandRegistry registry;

    @Autowired
    private ServerRegistry serverRegistry;


    @Autowired
    private DistrischScheduler scheduler;

    public ServerRegistry getServerRegistry() {
        return serverRegistry;
    }

    public void setServerRegistry(ServerRegistry serverRegistry) {
        this.serverRegistry = serverRegistry;
    }

    public RegisterCommand() {

    }

    public DistrischScheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(DistrischScheduler scheduler) {
        this.scheduler = scheduler;
    }

    @PostConstruct
    public void register() {
        getRegistry().put(getClassifier(), this);
    }

    public CommandRegistry getRegistry() {
        return registry;
    }

    public void setRegistry(CommandRegistry registry) {
        this.registry = registry;
    }

    @Override
    public String getClassifier() {
        return "registerCommand";
    }

    @Override
    public Boolean process(JSONObject json) {
        if (json.containsKey("classifier") && json.get("classifier") != null) {
            Object o = json.get("classifier");
            if (o instanceof String) {
                if ("registerCommand".equals((String) o)) {
                    String serverName = (String) json.get("serverName");
                    ServerRegistered newServer = new ServerRegistered(serverName);
                    newServer.setQueueName(serverName + "_queue");
                    List<JSONObject> jobs = (List<JSONObject>) json.get("jobsType");
                    for (JSONObject jsonObject : jobs) {
                        String cronExpression = (String)jsonObject.get("cronExpression");
                        String name = (String)jsonObject.get("name");

                        List<JSONObject> params = (List<JSONObject>) jsonObject.get("params");
                        Map<String,String> paramsMap = new HashMap<>();
                        params.forEach((param) -> {
                            paramsMap.put((String)param.get("key"),(String)param.get("value"));
                        });
                        JobType jobType = new JobType();
                        jobType.setCron(cronExpression);
                        jobType.setName(name);
                        jobType.setParameters(paramsMap);

                        newServer.addJob(jobType);
                    }
                    serverRegistry.addServer(newServer);
                    newServer.getJobs().forEach(jobType -> {
                        try{
                            scheduler.schedule(DistrischJob.class,jobType.getName(),"districh",jobType.getCron(),jobType.getParameters(),newServer);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    });
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean hasToProcess(JSONObject json) {
       return true;
    }
}