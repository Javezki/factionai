package com.github.javezki.listeners;

import com.github.javezki.BotConfig;
import com.github.javezki.Config;
import com.github.javezki.Faction;
import com.github.javezki.FactionEvent;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandListeners extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent ev) {
        String eventID = "";
        FactionEvent event = null;
        switch (ev.getName()) {
            case "createevent":
                if (!permissionCheck(ev))
                    return;
                new FactionEvent(ev);
                break;
            case "seteventchannel": {
                if (!permissionCheck(ev))
                    return;
                BotConfig.setEventChannel(ev.getChannel());
                ev.reply("This channel has been configured as the event channel!");
                break;
            }
            case "setlogchannel":
                if (!permissionCheck(ev))
                    return;
                BotConfig.setLogChannel(ev.getChannel());
                ev.reply("This channel has been configured as the log channel!");
                break;
            case "seteventaccess":
                if (!(ev.getMember().hasPermission(Permission.ADMINISTRATOR))) {
                    ev.reply("You do not have permission to use this command!").setEphemeral(true).queue();
                    return;
                }
                BotConfig.setEventAccess(ev.getOption("role").getAsRole());
                break;
            case "cancelevent":
                if (!(permissionCheck(ev)))
                    return;
                eventID = ev.getOption("eventid").getAsString();
                if (!isValidEvent(eventID)) {
                    ev.reply("This is not a valid event ID!").queue();
                    System.out.println("Invalid ID: " + eventID);
                    return;
                };
                event = FactionEvent.getEvent(eventID);
                event.cancelEvent();
                ev.reply("Event has been successfully deleted!").setEphemeral(true).queue();
                System.out.println("Event successfully deleted by: " + ev.getUser().getAsMention() + "\nID: " + event.getEventID());
                break;
            case "delayevent":
                if (!(permissionCheck(ev)))
                    return;
                eventID = ev.getOption("eventid").getAsString();
                if (!isValidEvent(eventID)) {
                    ev.reply("This is not a valid event ID!").queue();
                    System.out.println("Invalid ID: " + eventID);
                    return;
                }
                event = FactionEvent.getEvent(eventID);
                int delayTime = ev.getOption("delaytime").getAsInt();
                event.delayEvent(delayTime);
                ev.reply("Event successfully delayed!").setEphemeral(true).queue();
                System.out.println("Event successfully delayed by: " + ev.getUser().getAsTag() + "\nID: " + event.getEventID());
            case "setroleping": 
                if (!(ev.getMember().hasPermission(Permission.ADMINISTRATOR))) return;
                BotConfig.setRolePing(ev.getOption("role").getAsRole());
                ev.reply("Ping role has been successfully set!").setEphemeral(true).queue();
                System.out.println("Ping role has been set to: " + ev.getOption("role").getAsRole().getId());
            default:
                break;
        }
    }

    /**
     * 
     * @param eventID
     * @param ev
     * @return
     */
    private boolean isValidEvent(String eventID) {
        if (eventID == null)
            return false;
        FactionEvent event = FactionEvent.getEvent(eventID);
        if (event == null)
            return false;
        return true;
    }
    
    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent ev) {
        FactionEvent sentinelEvent = FactionEvent.getEvent(ev.getMessageId());
        User user = Faction.jda.retrieveUserById(ev.getUserId()).complete();
        if (sentinelEvent == null)
            return;
        if (!(ev.getEmoji().asUnicode().equals(Emoji.fromUnicode("U+2705"))))
            return;
        if (user.isBot())
            return;
        if (sentinelEvent.isStarted())
            return;

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Joined an event!");
        builder.addField("Event:", sentinelEvent.getEventLink(), false);
        user.openPrivateChannel()
                .flatMap(channel -> channel.sendMessageEmbeds(builder.build()))
                .onSuccess(success -> {
                    sentinelEvent.addUser(user);
                    System.out.println("User, " + user.getAsTag() + " added to event: " + sentinelEvent.getEventID());
                })
                .onErrorFlatMap(
                        (error) -> ev.getChannel().sendMessage("OPEN DMS NERD " + user.getAsMention()))
                .queue();

    }

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent ev) {
        FactionEvent sentinelEvent = FactionEvent.getEvent(ev.getMessageId());
        if (sentinelEvent == null)
            return;
        if (ev.getUser().isBot())
            return;
        if (!(ev.getEmoji().asUnicode().equals(Emoji.fromUnicode("U+2705"))))
            return;
        if (sentinelEvent.isStarted())
            return;
        User user = Faction.jda.retrieveUserById(ev.getUserId()).complete();
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Withdrawn from event");
        builder.addField("Event:", sentinelEvent.getEventLink(), false);
        user.openPrivateChannel()
                .flatMap(channel -> channel.sendMessageEmbeds(builder.build()))
                .onSuccess((success) -> {
                    sentinelEvent.removeUser(user);
                    System.out.println("User, " + user.getAsTag() + " removed from event: " + sentinelEvent.getEventID());
                })
                .onErrorFlatMap(
                        (error) -> ev.getChannel().sendMessage("OPEN DMS NERD " + user.getAsMention()))
                .queue();

    }


    private boolean permissionCheck(SlashCommandInteractionEvent ev) {
        if (Config.getValue(FactionEvent.ROLEID_KEY_VALUE) == null
                || Config.getValue(FactionEvent.ROLEID_KEY_VALUE).equals("")) {
            ev.reply("Set the role in using /seteventaccess!").setEphemeral(true).queue();
            return false;
        }
        Role role = Faction.jda.getRoleById(Config.getValue(FactionEvent.ROLEID_KEY_VALUE));
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
}
