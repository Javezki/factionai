package com.github.javezki;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class Sentinel {

    public static JDA jda;

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
                .addEventListeners(new EchoCommand(), 
                new EventCommand()
                )
                .build();
        initSlashCommands();
    }

    private void initSlashCommands() {
        jda.updateCommands().addCommands(
                Commands.slash("say", "Repeats messages back to you")
                        .addOption(OptionType.STRING, "message", "The message to repeat", true),
                Commands.slash("seteventchannel", "Sets the current channel as the main event channel")
                .setGuildOnly(true),
                Commands.slash("createevent", "Creates a new event")
                        .addOption(OptionType.STRING, "type", "The type of event (raid, patrol, blitz etc.)", true)
                        .addOption(OptionType.INTEGER, "time", "In how long the event will begin (minutes)", true)
                        .addOption(OptionType.STRING, "code", "The private server code you are going to use", true)
                        .addOption(OptionType.INTEGER, "atendees", "The amount of atendees required", false)
                        .addOption(OptionType.STRING, "description", "A description of the event", false)
                        .addOption(OptionType.STRING, "co-host", "A list of co-hosts")
                        .setGuildOnly(true),
                Commands.slash("setlogchannel", "This command will set the current channel as the log channel")

        ).queue();
    }

}
