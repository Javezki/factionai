package com.github.javezki;

import java.util.List;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class SentinelMessage {
    
    public void onEventStart(String psCode, String embedID) {
        SentinelEvent event = EventCommand.getEvents().get(embedID);
        sendDM(psCode, embedID, event);
        sendToLog(event);
    }

    private void sendToLog(SentinelEvent event) {
        String logID = Config.getValue(EventCommand.LOGID_KEY_VALUE);
        String eventChannelID = Config.getValue(EventCommand.CHANNELID_KEY_VALUE);
        TextChannel channel = Sentinel.jda.getTextChannelById(logID);
        EmbedBuilder builder = new EmbedBuilder();
        String eventLink = Sentinel.jda
        .getTextChannelById(eventChannelID)
        .retrieveMessageById(event.getEmbedID())
        .complete()
        .getJumpUrl();

        builder.setTitle("Log of: " + eventLink);
        builder.addField("All people who reacted: ", appendMessage(event.getUsers()), false);
        builder.addField("Attendees: ", appendMessage(event.getAttendingUsersList()), false);

        channel.sendMessageEmbeds(builder.build()).queue();
    }  

    private String appendMessage(List<User> userList) {
        String userListString = "";
        for (User user : userList) userListString = userListString + "\n<@" + user.getId() + ">";
        return userListString;
    }

    private void sendDM(String psCode,String embedID, SentinelEvent event) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Hello! The Event Has Started!");
        builder.addField("Code: ", psCode, false);
        builder.setFooter("The Event ID: " + embedID);
        for (User user : event.getAttendingUsersList()) {
            user.openPrivateChannel().queue(channel -> {
                channel.sendMessageEmbeds(builder.build()).queue(
                    message -> {
                        message.delete().queueAfter(20, TimeUnit.MINUTES);
                    }
                );
            });
        }
    }
}
