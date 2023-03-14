package com.github.javezki;

import org.jobrunr.configuration.JobRunr;
import org.jobrunr.server.JobActivator;
import org.jobrunr.storage.InMemoryStorageProvider;

/**
 * Hello world!
 *
 */
public class SentinelMain {


    public static void main(String[] args) {
        JobRunr.configure()
        .useStorageProvider(new InMemoryStorageProvider())
        .useBackgroundJobServer()
        .useDashboard(8000)
        .initialize();
        Config config = new Config();
        if (!(config.propertiesExists())) config.createProperties();
        if (config.getKey(Config.DISCORD_KEY_VALUE).equals("")) 
            config.setProperty(Config.DISCORD_KEY_VALUE, config.userInputKey());

        Sentinel sentinel = new Sentinel(config.getKey(Config.DISCORD_KEY_VALUE));
    }
}