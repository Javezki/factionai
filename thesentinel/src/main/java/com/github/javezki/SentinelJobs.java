package com.github.javezki;

import java.util.List;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class SentinelJobs {

    /**
     * 
     * @param psCode
     * @param embedID
     */
    public void onFutureStart(String embedID) {
        SentinelEvent event = SentinelEvent.getEvent(embedID);
        sendDM(event);
        sendToLog(event);
        sendToChannel(event);

        event.setStarted(true);
    }


    public void onLate(String eventID) {
        SentinelEvent event = SentinelEvent.getEvent(eventID);
        sendToLog(event);
        event.deleteEvent();
    }
 
    private void sendToChannel(SentinelEvent event) {
        EmbedBuilder builder = new EmbedBuilder();

        builder.setTitle("An Event has Started!");
        builder.addField("The Event: ", event.getEventLink(), false);
        builder.setFooter("Event ID: " + event.getEventID());

        event.getChannel().sendMessageEmbeds(builder.build()).queue();
    }

    private void sendToLog(SentinelEvent event) {
        EmbedBuilder builder = new EmbedBuilder();

        builder.setTitle("Log of: " + event.getEventLink());

        if (!event.isStarted())
            builder.addField("Attendees: ", appendMessage(event.getAttendingUsersList()), false);
        if (event.isStarted())
            builder.addField("Late Arrivals: ", appendMessage(event.getLateList()), false);
        TextChannel channel = Sentinel.jda.getTextChannelById(Config.getValue(SentinelEvent.LOGID_KEY_VALUE));
        channel.sendMessageEmbeds(builder.build()).queue();
    }

    /**
     * 
     * @param userList User list that wants to be appended
     * @return string that has all users appended on a new
     */
    private String appendMessage(List<User> userList) {
        String userListString = "";
        for (User user : userList)
            userListString = userListString + "\n<@" + user.getId() + ">";
        return userListString;
    }

    /**
     * @deprecated
     * @param psCode  The Private server code
     * @param embedID the event ID
     * @param event   The actual event
     * 
     */
    @SuppressWarnings("unused")
    private void sendDM(String psCode, String embedID, SentinelEvent event) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Hello! The Event Has Started!");
        builder.addField("Code: ", psCode, false);
        builder.addField("Spawn Location: ", event.getSpawnLocation(), false);
        builder.addField("Squad Colour: ", event.getSquadColour(), false);
        builder.setFooter("The Event ID: " + embedID);
        for (User user : event.getAttendingUsersList()) {
            user.openPrivateChannel().queue(channel -> {
                channel.sendMessageEmbeds(builder.build()).queue(
                        message -> {
                            message.delete().queueAfter(20, TimeUnit.MINUTES);
                        });
            });
        }
    }

    /***
     * 
     * @param event The instance of sentinel events that is starting.
     */

    private void sendDM(SentinelEvent event) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Hello! The Event Has Started!");
        builder.addField("Code: ", event.getpsCode(), false);
        builder.addField("Voice Channel: ", event.getVoiceChannel(), false);
        builder.addField("Spawn Location: ", event.getSpawnLocation(), false);
        builder.addField("Squad Colour: ", event.getSquadColour(), false);
        builder.setFooter("The Event ID: " + event.getEventID());
        for (User user : event.getAttendingUsersList()) {
            user.openPrivateChannel().queue(channel -> {
                channel.sendMessageEmbeds(builder.build()).queue(
                        message -> {
                            message.delete().queueAfter(20, TimeUnit.MINUTES);
                        });
            });
        }
    }
}
