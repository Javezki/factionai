package com.github.javezki;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CurrentEventListener extends ListenerAdapter {

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent ev) {
        SentinelEvent event = SentinelEvent.getEvent(ev.getMessageId());
        User user = Sentinel.jda.retrieveUserById(ev.getUserId()).complete();
        if (event == null)
            return;
        if (!(ev.getEmoji().asUnicode().equals(Emoji.fromUnicode("U+2705"))))
            return;
        if (user.isBot())
            return;
        if (!event.isStarted())
            return;
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Event has already started!");
        builder.addField("Code: ", event.getpsCode(), false);
        builder.addField("Voice Channel: ", event.getVoiceChannel(), false);
        builder.addField("Spawn Location: ", event.getSpawnLocation(), false);
        builder.addField("Squad Colour: ", event.getSquadColour(), false);
        builder.setFooter("The Event ID: " + event.getEventID());
        user.openPrivateChannel()
            .flatMap(channel -> channel.sendMessageEmbeds(builder.build()))
            .onSuccess(success -> {
                event.addLateUser(user);
                System.out.println("User, " + user.getAsTag() + "added to event: " + event.getEventID());
            })
            .onErrorFlatMap(
                    (error) -> ev.getChannel().sendMessage("OPEN DMS NERD " + user.getAsMention()))
            .queue();

        System.out.println("User, " + user.getName() + " attended late to: " + event.getEventID());
    }
}
