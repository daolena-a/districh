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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Register command, register a job in distrisch
 */
@Component
public class RegisterCommand implements Command {
    public static Logger LOGGER = LoggerFactory.getLogger(RegisterCommand.class);

    /**
     * The command registry
     */
    @Autowired
    private CommandRegistry registry;

    /**
     * The server registry
     */
    @Autowired
    private ServerRegistry serverRegistry;

    /**
     * The scheduler
     */
    @Autowired
    private DistrischScheduler scheduler;

    /**
     * The job type registry
     */
    @Autowired
    private JobTypeRegistry jobTypeRegistry;

    /**
     * Return the JobTypeRegistry
     * @return the JobTypeRegistry
     */
    public JobTypeRegistry getJobTypeRegistry() {
        return jobTypeRegistry;
    }

    /**
     * Set the JobTypeRegistry
     * @param jobTypeRegistry the JobTypeRegistry
     */
    public void setJobTypeRegistry(JobTypeRegistry jobTypeRegistry) {
        this.jobTypeRegistry = jobTypeRegistry;
    }

    /**
     * Return the server registry
     * @return the server registry
     */
    public ServerRegistry getServerRegistry() {
        return serverRegistry;
    }

    /**
     * Set the server registry
     * @param serverRegistry the server registry
     */
    public void setServerRegistry(ServerRegistry serverRegistry) {
        this.serverRegistry = serverRegistry;
    }

    /**
     * Constructor
     */
    public RegisterCommand() {

    }

    /**
     * Set the Distrisch scheduler
     * @return the scheduler
     */
    public DistrischScheduler getScheduler() {
        return scheduler;
    }

    /**
     *
     * @param scheduler
     */
    public void setScheduler(DistrischScheduler scheduler) {
        this.scheduler = scheduler;
    }

    /**
     * Regiister this command to the registry
     */
    @PostConstruct
    public void register() {
        getRegistry().put(getClassifier(), this);
    }

    /**
     * Return the command registry
     * @return the command registry
     */
    public CommandRegistry getRegistry() {
        return registry;
    }

    /**
     * Set the command registry
     * @param registry the command registry
     */
    public void setRegistry(CommandRegistry registry) {
        this.registry = registry;
    }

    /**
     * Return the command name
     * @return The command name.
     */
    @Override
    public String getClassifier() {
        return "registerCommand";
    }

    /**
     * Process the json message, construct a server and jobtypes and schedule job.
     * @param json The register message.
     * @return a flag for success or failure.
     */
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
                            LOGGER.info("New job registrer {} with cron {}", name, cronExpression);

                        }

                        newServer.addJob(jobType);
                        jobType.getServerRegistereds().add(newServer);
                        List<JSONObject> params = (List<JSONObject>) jsonObject.get("params");
                        params.forEach((param) -> {
                            newServer.addConf(name, (String) param.get("key"), (String) param.get("value"));
                        });
                        try {
                            if (newJob) {
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