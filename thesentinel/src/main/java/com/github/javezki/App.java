package com.github.javezki;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;

/**
 * Hello world!
 *
 */
public class App {

    private static String CONFIG_FILE_NAME = "discord.properties";
    private static String DISCORD_KEY_VALUE = "discordKey";

    public static void main(String[] args) {
        App app = new App();
        if (!(app.propertiesExists())) app.createProperties();
        if (app.getKey().equals("")) app.setKey(app.userInputKey());

        Sentinel sentinel = new Sentinel(app.getKey());
    }

    /**
     * 
     * @return A true or false based on whether or not the discord.properties file exists
     */
    public boolean propertiesExists() {
        File file = new File(CONFIG_FILE_NAME);
        if (!(file.exists())) return false;
        return true;
    }

    /**
     * Creates a blank discord.properties file
     */
    private void createProperties() {
        Properties properties = new Properties();
        try (FileOutputStream out = new FileOutputStream(CONFIG_FILE_NAME)) {
            properties.put(DISCORD_KEY_VALUE, "");
            properties.store(out, null);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        };
        
    }

    /**
     * 
     * @return Returns a string value based on user input
     */
    private String userInputKey() {
        String key = "";
        Scanner sc = new Scanner(System.in);
        System.out.println("Discord Bot Key:");
        key = sc.nextLine();
        sc.close();
        return key;

    }

    /**
     * This method will first check if the properties file exists and if it doesn't would return 
     * @param key The Discord key that will be saved to a config file on local
     * 
     */
    private void setKey(String key) {
        App app = new App();
        if (app.propertiesExists()==false) {
            try {
                throw new FileNotFoundException();
            } catch (FileNotFoundException e) {
                System.err.println("discord.properties does not exist!");
            }
        }
        Properties properties = new Properties();
        FileOutputStream output = null;
        FileInputStream input = null;

        try {
            input = new FileInputStream(CONFIG_FILE_NAME);
            output = new FileOutputStream(CONFIG_FILE_NAME);
            properties.load(input);
            properties.setProperty(DISCORD_KEY_VALUE, key);
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
        }
    }

    /**
     * 
     * @return The Discord Bot Key that was inputted from setKey();
     */

    public String getKey() {
        Properties prop = new Properties();
        InputStream input = null;
        try {
            input = new FileInputStream(CONFIG_FILE_NAME);
            prop.load(input);
            return prop.getProperty(DISCORD_KEY_VALUE);
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
}
