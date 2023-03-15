package com.github.javezki;

import java.util.List;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class SentinelEmbeds {

    private SlashCommandInteractionEvent ev;
    private List<OptionMapping> nonBlankOptions;

    public SentinelEmbeds(SlashCommandInteractionEvent ev, List<OptionMapping> nonBlankOptions) {
        this.nonBlankOptions = nonBlankOptions;
        this.ev = ev;
    }
    
    public void embedOptionals(EmbedBuilder eBuilder) {
        for (OptionMapping opt : nonBlankOptions) {
            String temp = opt.getName();
            temp = temp.substring(0,1 ).toUpperCase() + temp.substring(1);
            eBuilder.addField(temp, opt.getAsString(), false);
        }
    }

    public void embedTime(EmbedBuilder eBuilder) {
        int time = ev.getOption("time").getAsInt();
        int curentTime =(int)( System.currentTimeMillis() / 1000L);
        int finalTime = curentTime + (time * 60);
        eBuilder.addField("Event Starting in: ", "<t:" + Integer.toString(finalTime) + ":R>\n<t:" + Integer.toString(finalTime)  +">" , true);
    }

    public void embedEventType(EmbedBuilder eBuilder) {
        String type = ev.getOption("type").getAsString();
        eBuilder.addField("Event Type:",type , false);
    }

    public void embedDivider(EmbedBuilder eBuilder) {
        eBuilder.setDescription("**-------------------------------------------------------**");
    }

    public void embedHost(EmbedBuilder eBuilder) {
        String authorId = ev.getUser().getId();
        eBuilder.addField("Host: ", "<@!" + authorId + ">", false);
    }

    public void embedTitle(EmbedBuilder eBuilder) {
        String author = ev.getUser().getName();
        String type = ev.getOption("type").getAsString();
        eBuilder.setTitle(author + "'s " + type + " Event");
    }
}
