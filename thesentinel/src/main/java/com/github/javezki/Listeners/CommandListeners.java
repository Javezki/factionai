package com.github.javezki.Listeners;

import com.github.javezki.BotConfig;
import com.github.javezki.Config;
import com.github.javezki.Sentinel;
import com.github.javezki.SentinelEvent;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandListeners extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent ev) {
        String eventID = "";
        SentinelEvent event = null;
        switch (ev.getName()) {
            case "createevent":
                if (!permissionCheck(ev))
                    return;
                new SentinelEvent(ev);
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
                event = SentinelEvent.getEvent(eventID);
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
                event = SentinelEvent.getEvent(eventID);
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
        SentinelEvent event = SentinelEvent.getEvent(eventID);
        if (event == null)
            return false;
        return true;
    }
    
    private boolean permissionCheck(SlashCommandInteractionEvent ev) {
        if (Config.getValue(SentinelEvent.ROLEID_KEY_VALUE) == null
                || Config.getValue(SentinelEvent.ROLEID_KEY_VALUE).equals("")) {
            ev.reply("Set the role in using /seteventaccess!").setEphemeral(true).queue();
            return false;
        }
        Role role = Sentinel.jda.getRoleById(Config.getValue(SentinelEvent.ROLEID_KEY_VALUE));
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
