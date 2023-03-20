package com.github.javezki;

import org.jobrunr.configuration.JobRunr;
import org.jobrunr.storage.InMemoryStorageProvider;

import com.github.javezki.faction.Faction;

/**
 * Hello world!
 *
 */
public class FactionMain {


    public static void main(String[] args) {
        JobRunr.configure()
        .useStorageProvider(new InMemoryStorageProvider())
        .useBackgroundJobServer()
        .useDashboard(8000)
        .initialize();
        if (!(Config.propertiesExists())) Config.createProperties();
        if (Config.getValue(Config.DISCORD_KEY_VALUE).equals("")) 
            Config.setProperty(Config.DISCORD_KEY_VALUE, Config.userInputKey());

        new Faction(Config.getValue(Config.DISCORD_KEY_VALUE));
    }
}