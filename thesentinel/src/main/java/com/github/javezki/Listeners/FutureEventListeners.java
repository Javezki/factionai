package com.github.javezki.Listeners;

import com.github.javezki.Sentinel;
import com.github.javezki.SentinelEvent;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class FutureEventListeners extends ListenerAdapter {
    private FutureEventListeners() {}

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent ev) {
        SentinelEvent sentinelEvent = SentinelEvent.getEvent(ev.getMessageId());
        User user = Sentinel.jda.retrieveUserById(ev.getUserId()).complete();
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
        SentinelEvent sentinelEvent = SentinelEvent.getEvent(ev.getMessageId());
        if (sentinelEvent == null)
            return;
        if (ev.getUser().isBot())
            return;
        if (!(ev.getEmoji().asUnicode().equals(Emoji.fromUnicode("U+2705"))))
            return;
        if (sentinelEvent.isStarted())
            return;
        User user = Sentinel.jda.retrieveUserById(ev.getUserId()).complete();
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
}
