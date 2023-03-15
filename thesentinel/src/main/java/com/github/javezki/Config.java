package com.github.javezki;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

public class Config {

    public static String CONFIG_FILE_NAME = "discord.properties";
    public static String DISCORD_KEY_VALUE = "discordKey";

    public Config() {

    }

    /**
     * @apiNote Sets a value to an existing key in discord.properties
     * @param key A key value
     * @param value Any value required
     */

    public static void setProperty(String key, String value) {
        if (propertiesExists()==false) createProperties();
        Properties properties = new Properties();
        FileOutputStream output = null;
        FileInputStream input = null;

        try {
            input = new FileInputStream(CONFIG_FILE_NAME);
            properties.load(input);
            properties.setProperty(key, value);
            output = new FileOutputStream(CONFIG_FILE_NAME);
            properties.store(output, null);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 
     * @param key The key value of the properties file
     * @return The String value of the key
     */
    public static String getValue(String key) {
        Properties prop = new Properties();
        FileInputStream input = null;
        try {
            input = new FileInputStream(Config.CONFIG_FILE_NAME);
            prop.load(input);
            return prop.getProperty(key);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

        public static void createProperties() {
        Properties properties = new Properties();
        try (FileOutputStream out = new FileOutputStream(CONFIG_FILE_NAME)) {
            properties.put(DISCORD_KEY_VALUE, "");
            properties.put(EventCommand.CHANNELID_KEY_VALUE, "");
            properties.put(EventCommand.LOGID_KEY_VALUE, "");
            properties.put(EventCommand.ROLEID_KEY_VALUE, "");
            properties.store(out, null);
        } catch (IOException e) {
            e.printStackTrace();
        };
        
    }

    public static boolean propertiesExists() {
        File file = new File(Config.CONFIG_FILE_NAME);
        if (file.exists()) return true;
        return false;
    }
       /**
     * 
     * @return Returns a string value based on user input
     */
    public static String userInputKey() {
        String key = "";
        Scanner sc = new Scanner(System.in);
        System.out.println("Discord Bot Key:");
        key = sc.nextLine();
        sc.close();
        return key;

    }
    
}
