package com.github.javezki;

import com.github.javezki.listeners.CommandListeners;
import com.github.javezki.listeners.CurrentEventListener;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class Faction {

    public static JDA jda;

    public Faction(String botKey) {
        initBot(botKey);
    }

    /**
     * 
     * @param botKey The token that the bot runs on
     */
    private void initBot(String botKey) {
        JDABuilder builder = JDABuilder.createDefault(botKey);
        builder.setActivity(Activity.playing("With Punch's Balls"));
        jda = builder
                .addEventListeners(new EchoCommand(), 
                new CommandListeners(),
                new CurrentEventListener()
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
                        .addOption(OptionType.STRING, "squad-colour", "The squad colour that people will spawn", true)
                        .addOption(OptionType.STRING, "spawn-location", "The place the players will spawn at", true)
                        .addOption(OptionType.STRING, "voice-channel", "The voice channel you will be hosting in", true)
                        .addOption(OptionType.INTEGER, "atendees", "The amount of atendees required", false)
                        .addOption(OptionType.STRING, "description", "A description of the event", false)
                        .addOption(OptionType.STRING, "co-host", "A list of co-hosts")
                        .addOption(OptionType.STRING, "notes", "Misc info")
                        .addOption(OptionType.STRING, "rules", "The rules of the event")
                        .setGuildOnly(true),
                Commands.slash("setlogchannel", "This command will set the current channel as the log channel")
                        .setGuildOnly(true),
                Commands.slash("seteventaccess", "This command will set the role that will have access to the permission")
                        .setGuildOnly(true)
                        .addOption(OptionType.ROLE, "role", "The role that will have access", true),
                Commands.slash("setroleping", "This command will set the role that will be pinged on event start")
                        .addOption(OptionType.ROLE, "role", "The role that will be pinged", true)
                        .setGuildOnly(true),
                Commands.slash("cancelevent", "Cancels an event")
                        .addOption(OptionType.STRING, "eventid", "The embed ID of the event you created", true)
                        .setGuildOnly(true),
                Commands.slash("delayevent", "Delays an event by X amount of minutes")
                        .addOption(OptionType.STRING,"eventid", "The event ID, it is the message ID of the generated event", true)
                        .addOption(OptionType.INTEGER, "delaytime", "Delay event by X amount of minutes", true)
                        .setGuildOnly(true)
        ).queue();
    }

}
