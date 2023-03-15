package com.github.javezki;

import java.time.Instant;
import java.util.HashMap;

import org.jobrunr.jobs.JobId;
import org.jobrunr.scheduling.BackgroundJob;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SentinelEventListener extends ListenerAdapter {

    public final static String CHANNELID_KEY_VALUE = "eventChannelID";
    public final static String LOGID_KEY_VALUE = "logChannelID";
    public final static String ROLEID_KEY_VALUE = "eventAccessID";
    private static HashMap<String, SentinelEvent> sentinelEvents = new HashMap<>();
    public static HashMap<SentinelEvent, JobId> eventJobs = new HashMap<>();

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String eventID = "";
        switch (event.getName()) {
            case "createevent":
                if (!permissionCheck(event))
                    return;
                SentinelEvent hostEvent = new SentinelEvent(event);
                String messageID = hostEvent.getEmbedID();
                sentinelEvents.put(messageID, hostEvent);
                break;
            case "seteventchannel":
                if (!permissionCheck(event))
                    return;
                setEventChannel(event);
                break;
            case "setlogchannel":
                if (!permissionCheck(event))
                    return;
                setLogChannel(event);
                break;
            case "seteventaccess":
                if (!(event.getMember().hasPermission(Permission.ADMINISTRATOR))) {
                    event.reply("You do not have permission to use this command!").queue();
                    return;
                }
                setEventAccess(event);
                break;
            case "cancelevent":
                if (!(permissionCheck(event)))
                    return;
                eventID = event.getOption("eventid").getAsString();
                if (!isValidEvent(eventID, event)) {
                    event.reply("This is not a valid event ID!").queue();
                    System.out.println("Invalid ID: " + eventID);
                    return;
                }
                ;
                cancelEvent(getMessageFromSentinel(eventID, event));
                event.reply("Event has been successfully deleted!").setEphemeral(true).queue();
                break;
            case "delayevent":
                if (!(permissionCheck(event)))
                    return;
                eventID = event.getOption("eventid").getAsString();
                if (!isValidEvent(eventID, event)) {
                    event.reply("This is not a valid event ID!").queue();
                    System.out.println("Invalid ID: " + eventID);
                    return;
                }
                delayEvent(event);
            default:
                break;
        }
    }

    private void delayEvent(SlashCommandInteractionEvent event) {

        Message message = getMessageFromSentinel(event.getOption("eventid").getAsString(), event);
        notfiyAttendee(message, "An event was delayed!");

        SentinelEvent sEvent = sentinelEvents.get(message.getId());
        EmbedBuilder builder = sEvent.getEventEmbed();

        Instant timeToStart = sEvent.getTimeToStart();

        int delayTime = event.getOption("delaytime").getAsInt();
        timeToStart = timeToStart.plusSeconds(delayTime*60);
        String timeToStartStr = Long.toString(timeToStart.getEpochSecond());
        
        builder.addField("New Time:", "<t:" + timeToStartStr + ":R>\n<t:" + timeToStartStr  +">", false);
        builder.setFooter("(Delayed)");
        message.editMessageEmbeds(builder.build()).queue();
        BackgroundJob.delete(eventJobs.get(sEvent));
        eventJobs.remove(sEvent);
        JobId id = BackgroundJob.schedule(Instant.now().plusSeconds(event.getOption("delaytime").getAsInt()*60), () ->{
            new SentinelMessage().onEventStart(sEvent.getpsCode(), sEvent.getEmbedID());
        });

        eventJobs.put(sEvent, id);

        event.reply("Event successfully delayed!").setEphemeral(true).queue();
    }

    private void notfiyAttendee(Message message, String title) {
        EmbedBuilder notifyPlayerMessage = new EmbedBuilder();
        SentinelEvent event = sentinelEvents.get(message.getId());
        notifyPlayerMessage.setTitle(title);
        notifyPlayerMessage.addField("Event:", message.getJumpUrl(), false);
        notifyPlayerMessage.setFooter(message.getId());
        for (User user : event.getAttendingUsersList()) {
            user.openPrivateChannel().queue(channel -> {
                channel.sendMessageEmbeds(notifyPlayerMessage.build()).queue();
            });
        }
    }

    /**
     * 
     * @param eventID
     * @param ev
     * @return
     */
    private boolean isValidEvent(String eventID, SlashCommandInteractionEvent ev) {
        if (eventID == null)
            return false;
        if (getMessageFromSentinel(eventID, ev) == null)
            return false;
        if (sentinelEvents.get(eventID) == null)
            return false;
        return true;
    }

    /**
     * @apiNote Cancels an event. It will send a message to all participants in said
     *          event
     * @param message The message that was generated from events
     */

    private void cancelEvent(Message message) {
        notfiyAttendee(message, "An Event was Cancelled!");
        SentinelEvent event = sentinelEvents.get(message.getId());
        BackgroundJob.delete(eventJobs.get(event));
        sentinelEvents.remove(message.getId());
        EmbedBuilder editedMessage = new EmbedBuilder();
        editedMessage.setFooter("(Cancelled)");
        message.editMessageEmbeds(editedMessage.build()).queue();
        message.clearReactions();
        System.out.println(message.getId() + " has been deleted!");
    }

    /**
     * 
     * @param eventID The event ID of the bot. It is actual message ID but in string
     *                form
     * @param ev
     * @return
     */

    public Message getMessageFromSentinel(String eventID, SlashCommandInteractionEvent ev) {
        return ev.getGuild()
                .getTextChannelById(Config.getValue(CHANNELID_KEY_VALUE))
                .retrieveMessageById(eventID)
                .complete();
    }

    private void setEventAccess(SlashCommandInteractionEvent event) {
        Config.setProperty(ROLEID_KEY_VALUE, event.getOption("role").getAsRole().getId());
        event.reply("Role Successfully set!").setEphemeral(true).queue();
    }

    private void setLogChannel(SlashCommandInteractionEvent event) {
        Config.setProperty(LOGID_KEY_VALUE, event.getChannel().getId());
        event.reply("Channel Successfully Set!").setEphemeral(true).queue();
    }

    /**
     * @apiNote Sets the channel of where the command is being used as the only
     *          channel for events
     * @param event The Slash Interaction Command Event
     */

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent ev) {
        SentinelEvent sentinelEvent = sentinelEvents.get(ev.getMessageId());
        if (sentinelEvent == null)
            return;
        if (!(ev.getEmoji().asUnicode().equals(Emoji.fromUnicode("U+2705"))))
            return;
        if (ev.getUser().isBot())
            return;
        sentinelEvent.addUser(ev.getUser());
        ev.getUser().openPrivateChannel().queue(channel -> {
            channel.sendMessage("You have signed up for the event!").queue();
        });

    }

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent ev) {
        SentinelEvent sentinelEvent = sentinelEvents.get(ev.getMessageId());
        if (sentinelEvent == null)
            return;
        if (!(ev.getEmoji().asUnicode().equals(Emoji.fromUnicode("U+2705"))))
            return;
        User user = Sentinel.jda.retrieveUserById(ev.getUserId()).complete();
        sentinelEvent.removeUser(user);
        user.openPrivateChannel().queue(channel -> {
            channel.sendMessage("You have left the event!").queue();
        });
    }

    private void setEventChannel(SlashCommandInteractionEvent event) {
        Config.setProperty(CHANNELID_KEY_VALUE, event.getChannel().getId());
        event.reply("Channel Successfully Set!").queue();
    }

    public void removeEvent(String eventID) {
        sentinelEvents.remove(eventID);
    }

    public static HashMap<String, SentinelEvent> getEvents() {
        return sentinelEvents;
    }

    private boolean permissionCheck(SlashCommandInteractionEvent ev) {
        if (Config.getValue(SentinelEventListener.ROLEID_KEY_VALUE) == null
                || Config.getValue(SentinelEventListener.ROLEID_KEY_VALUE).equals("")) {
            ev.reply("Set the role in using /seteventaccess!").queue();
            return false;
        }
        Role role = Sentinel.jda.getRoleById(Config.getValue(SentinelEventListener.ROLEID_KEY_VALUE));
        if (role == null) {
            ev.reply("Set the role in using /seteventaccess!").queue();
            return false;
        }
        if (!(ev.getMember().getRoles().contains(role))) {
            ev.reply("You do not have access to this command!").queue();
            return false;
        }

        return true;
    }

    public static void addJob(SentinelEvent event, JobId id) {
        eventJobs.put(event, id);
    }

    public static void removeJob(SentinelEvent event) {
        eventJobs.remove(event);
    }
}
