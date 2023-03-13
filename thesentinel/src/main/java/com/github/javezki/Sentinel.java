package com.github.javezki;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;


public class Sentinel{

    private static JDA jda;

    public Sentinel(String botKey) {
        initBot(botKey);
    }

    /**
     * 
     * @param botKey The token that the bot runs on
     */
    private void initBot(String botKey) {
        JDABuilder builder = JDABuilder.createDefault(botKey);
        builder.setActivity(Activity.playing("Testing things for Javezki lol"));
        jda = builder
        .addEventListeners(new EchoCommand())
        .build();
        initSlashCommands();
    }

    private void initSlashCommands() {
        jda.updateCommands().addCommands(
            Commands.slash("say", "Repeats messages back to you")
            .addOption(OptionType.STRING, "message", "The message to repeat", true)
        ).queue();
    }
    
}
