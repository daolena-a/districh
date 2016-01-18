package org.distrishe.job;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by adaolena on 13/01/16.
 */
public class JobTypeRegistry {
    private Map<String,JobType> registry = new ConcurrentHashMap<>();


}
