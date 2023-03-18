package com.github.javezki;

import java.util.List;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class SentinelEmbeds {

    private List<OptionMapping> nonBlankOptions;

    public SentinelEmbeds(List<OptionMapping> nonBlankOptions) {
        this.nonBlankOptions = nonBlankOptions;
    }
    
    public void embedOptionals(EmbedBuilder eBuilder) {
        for (OptionMapping opt : nonBlankOptions) {
            String temp = opt.getName();
            temp = temp.substring(0,1 ).toUpperCase() + temp.substring(1);
            eBuilder.addField(temp, opt.getAsString(), false);
        }
    }

    public void embedTime(EmbedBuilder eBuilder, int time) {
        int curentTime =(int)( System.currentTimeMillis() / 1000L);
        int finalTime = curentTime + (time * 60);
        eBuilder.addField("Event Starting in: ", "<t:" + Integer.toString(finalTime) + ":R>\n<t:" + Integer.toString(finalTime)  +">" , true);
    }

    /**
     * 
     * @param eBuilder The embed builder that is currently being built
     * @param type The type of event will be
     */
    public void embedEventType(EmbedBuilder eBuilder, String type, String author) {
        eBuilder.addField("Event Type:",type , false);
        eBuilder.setTitle(author + "'s " + type + " Event");
    }

    public void embedDivider(EmbedBuilder eBuilder) {
        eBuilder.setDescription("**-------------------------------------------------------**");
    }

    public void embedHost(EmbedBuilder eBuilder, String authorID) {
        eBuilder.addField("Host: ", authorID,  false);
    }

    public void embedId(EmbedBuilder eBuilder, String eventID) {
        eBuilder.setFooter("Event ID: " + eventID);
    }

}
