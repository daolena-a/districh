package org.distrishe.command.register;

import org.distrishe.command.Command;
import org.distrishe.command.CommandRegistry;
import org.distrishe.job.DistrischJob;
import org.distrishe.job.DistrischScheduler;
import org.distrishe.job.JobType;
import org.distrishe.job.JobTypeRegistry;
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

    @Autowired
    private JobTypeRegistry jobTypeRegistry;

    public JobTypeRegistry getJobTypeRegistry() {
        return jobTypeRegistry;
    }

    public void setJobTypeRegistry(JobTypeRegistry jobTypeRegistry) {
        this.jobTypeRegistry = jobTypeRegistry;
    }

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
                    final ServerRegistered newServer = serverRegistry.getOrCreateServer(serverName);
                    newServer.setQueueName(serverName + "_queue");
                    List<JSONObject> jobs = (List<JSONObject>) json.get("jobsType");
                    boolean newJob = true;
                    for (JSONObject jsonObject : jobs) {
                        String cronExpression = (String) jsonObject.get("cronExpression");
                        final JobType jobType;
                        String name = (String) jsonObject.get("name");
                        if (jobTypeRegistry.getByName(name) != null) {
                            System.out.println("update job");
                            jobType = jobTypeRegistry.getByName(name);
                            newJob = false;
                        } else {
                            jobType = new JobType();
                            jobType.setCron(cronExpression);
                            jobType.setName(name);
                            jobTypeRegistry.put(name, jobType);
                            System.out.println("new job");

                        }

                        newServer.addJob(jobType);
                        jobType.getServerRegistereds().add(newServer);
                        List<JSONObject> params = (List<JSONObject>) jsonObject.get("params");
                        Map<String, String> paramsMap = new HashMap<>();
                        params.forEach((param) -> {
                            newServer.addConf(name, (String) param.get("key"), (String) param.get("value"));
                        });
                        try {
                            if(newJob){
                                scheduler.schedule(DistrischJob.class, jobType.getName(), "districh", jobType.getCron());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

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