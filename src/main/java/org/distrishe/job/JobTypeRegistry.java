package org.distrishe.job;

import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by adaolena on 13/01/16.
 */
@Service
public class JobTypeRegistry {
    private Map<String, JobType> registry = new ConcurrentHashMap<>();

    public void put(String name, JobType jobType) {
        registry.put(name, jobType);
    }

    public Collection<JobType> getAll() {
        return registry.values();
    }

}
