package com.github.javezki;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

public class BotConfig {

    private BotConfig(){

    }

    /**
     * 
     * @param event Takes a parameter of "role" and sets the ID as has access
     */

    public static void setEventAccess(Role role) {
        Config.setProperty(FactionEvent.ROLEID_KEY_VALUE, role.getId());
    }

    /**
     * 
     * @param event Sets the channel used as the main channel for logging
     */
    public static void setLogChannel(MessageChannel channel) {
        Config.setProperty(FactionEvent.LOGID_KEY_VALUE, channel.getId());
    }
    
    /**
     * 
     * @param event Sets the channel where command was used as the only channel that can be used for events
     */
    public static void setEventChannel(MessageChannel channel) {
        Config.setProperty(FactionEvent.CHANNELID_KEY_VALUE, channel.getId());
    }

    public static void setRolePing(Role role) {
        Config.setProperty(FactionEvent.PINGID_KEY_VALUE, role.getId());
    }
}
