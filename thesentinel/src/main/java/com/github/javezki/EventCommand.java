package com.github.javezki;

import java.util.HashMap;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class EventCommand extends ListenerAdapter {

    public final static String CHANNELID_KEY_VALUE = "eventChannelID";
    public final static String LOGID_KEY_VALUE = "logChannelID";
    private static HashMap<String ,SentinelEvent> sentinelEvents = new HashMap<>();
    
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            case "createevent":
                SentinelEvent hostEvent = new SentinelEvent(event);
                String messageID = hostEvent.getEmbedID();
                sentinelEvents.put(messageID, hostEvent);
                break;
            case "seteventchannel":
                setEventChannel(event);
                break;
            case "setlogchannel": 
                setLogChannel(event);
            default:
                break;
        }
    }
    
    private void setLogChannel(SlashCommandInteractionEvent event) {
        Config config = new Config();
        config.setProperty(LOGID_KEY_VALUE, event.getChannel().getId());   
    }

    /**
     * @apiNote Sets the channel of where the command is being used as the only channel for events
     * @param event The Slash Interaction Command Event 
     */

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent ev) {
        SentinelEvent sentinelEvent = sentinelEvents.get(ev.getMessageId());
        if (sentinelEvent == null) return;
        if(!(ev.getEmoji().asUnicode().equals(Emoji.fromUnicode("U+2705")))) return;
        if(ev.getUser().isBot()) return;
        sentinelEvent.addUser(ev.getUser());
        ev.getUser().openPrivateChannel().queue(channel -> {
            channel.sendMessage("You have signed up for the event!").queue();
        });
        
    }

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent ev) {
        SentinelEvent sentinelEvent = sentinelEvents.get(ev.getMessageId());
        if (sentinelEvent == null) return;
        if (!(ev.getEmoji().asUnicode().equals(Emoji.fromUnicode("U+2705")))) return;
        User user = Sentinel.jda.retrieveUserById(ev.getUserId()).complete();
        sentinelEvent.removeUser(user);
        user.openPrivateChannel().queue(channel -> {
            channel.sendMessage("You have left the event!").queue();
        });
    }

    private void setEventChannel(SlashCommandInteractionEvent event) {
        Config config = new Config();
        config.setProperty(CHANNELID_KEY_VALUE, event.getChannel().getId());
        event.reply("Channel Successfully Set!").queue();
    }

    public void removeEvent(String eventID) {
        sentinelEvents.remove(eventID);
    }

    public static HashMap<String, SentinelEvent> getEvents() {
        return sentinelEvents;
    }
}
