package com.github.javezki;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.jobrunr.scheduling.BackgroundJob;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class SentinelEvent {

    private SlashCommandInteractionEvent ev;
    private List<OptionMapping> nonBlankOptions = new ArrayList<>();
    private Config config;
    private String embedID;
    private int timeToEvent;
    private String psCode;

    private List<User> userList = new ArrayList<>();
    private List<User> attendingUsersList = new ArrayList<>();


    public SentinelEvent(SlashCommandInteractionEvent ev) {
        this.ev = ev;
        config = new Config();
        this.timeToEvent = ev.getOption("time").getAsInt();
        this.psCode = ev.getOption("code").getAsString();
        getAllNonNullOptions();
        
        initEvent();

    }

    private void getAllNonNullOptions() {
        for (OptionMapping option : ev.getOptions()) {
            if (option.getAsString() != null && !option.getAsString().isBlank()) {
                nonBlankOptions.add(option);
            }
        }
        nonBlankOptions.remove(0);
        nonBlankOptions.remove(0);
        nonBlankOptions.remove(0);
    }

    private boolean isChannel() {
        if (!(ev.getChannel().getId().equals(config.getKey(EventCommand.CHANNELID_KEY_VALUE))))
            return false;
        return true;
    }

    /**
     * 
     */
    public void initEvent() {
        if (!isChannel()) {
            failedInit();
            return;
        }

        EmbedBuilder eBuilder = new EmbedBuilder();

        SentinelEmbeds embeds = new SentinelEmbeds(ev, nonBlankOptions);
        
        embeds.embedTitle(eBuilder);
        embeds.embedDivider(eBuilder);
        embeds.embedHost(eBuilder);
        embeds.embedEventType(eBuilder);
        embeds.embedTime(eBuilder);
        embeds.embedOptionals(eBuilder);
                
        Message eventMessage = ev.getChannel().sendMessageEmbeds(eBuilder.build()).complete();
        eventMessage.addReaction(Emoji.fromUnicode("U+2705")).queue();
        embedID = eventMessage.getId();
        ev.reply("Event Successfully Created!").setEphemeral(true).queue();

        BackgroundJob.schedule(Instant.now().plusSeconds(timeToEvent*60), () -> {
            new SentinelMessage().onEventStart(psCode, embedID);
        });
    }
        // for (User user : attendingUsersList) {
        //     user.openPrivateChannel().queue(channel -> {
        //         channel.sendMessageEmbeds(builder.build()).queue(m -> {
        //             m.delete().queueAfter(20, TimeUnit.MINUTES);
        //         });
        //     });
        // }
    // }

    private void failedInit() { 
    }

    public String getEmbedID() {
        return embedID;
    }

    public int getTimeToEvent() {
        return timeToEvent;
    }

    public String getpsCode() {
        return psCode;
    }
    
    public List<User> getAttendingUsersList() {
        return attendingUsersList;
    }

    public List<User> getUsers() {
        return userList;
    }

    public void addUser(User user) {
        attendingUsersList.add(user);
        for (User userInList : userList) if(userInList.equals(user)) return;
        userList.add(user);
    }

    public void removeUser(User user) {
        attendingUsersList.remove(user);
    }

}