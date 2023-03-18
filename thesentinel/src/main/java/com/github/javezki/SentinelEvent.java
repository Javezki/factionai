package com.github.javezki;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jobrunr.jobs.JobId;
import org.jobrunr.scheduling.BackgroundJob;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class SentinelEvent {

    private List<OptionMapping> nonBlankOptions = new ArrayList<>();
    private String eventID;
    private int timeToEvent;
    private String psCode;
    private EmbedBuilder eventEmbed;
    private Instant timeToStart;
    private String squadColour;
    private String spawnLocation;
    private String voiceChannel;
    private Message eventMessage;
    private String type;
    private User host;

    public final static String CHANNELID_KEY_VALUE = "eventChannelID";
    public final static String LOGID_KEY_VALUE = "logChannelID";
    public final static String ROLEID_KEY_VALUE = "eventAccessID";

    private List<User> userList = new ArrayList<>();
    private List<User> attendingUsersList = new ArrayList<>();
    private static HashMap<String, SentinelEvent> futureEvents = new HashMap<>();
    private static HashMap<String, SentinelEvent> currentEvents = new HashMap<>();
    public static HashMap<SentinelEvent, JobId> eventJobs = new HashMap<>();


    public SentinelEvent(SlashCommandInteractionEvent ev) {
 
        this.timeToEvent = ev.getOption("time").getAsInt();
        this.psCode = ev.getOption("code").getAsString();
        this.squadColour = ev.getOption("squad-colour").getAsString();
        this.spawnLocation = ev.getOption("spawn-location").getAsString();
        this.voiceChannel = ev.getOption("voice-channel").getAsString();
        this.host = ev.getUser();
        this.type = ev.getOption("type").getAsString();
        getAllNonNullOptions(ev.getOptions());
        
        //Sends the embed to specified channel

        sendEmbedOnCreation(ev.getChannel().asTextChannel());

        createJob();

        ev.reply("Event Successfully Created!").setEphemeral(true).queue();

        futureEvents.put(eventID, this);

        System.out.println("Successfully created a new event! Event ID: " + eventID);

    }

    public void sendEmbedOnCreation(TextChannel channel) {
        if (!isChannel(channel)) return;

        EmbedBuilder eBuilder = new EmbedBuilder();

        //Creates embeds and its properties

        SentinelEmbeds embeds = new SentinelEmbeds(nonBlankOptions);
        
        embeds.embedEventType(eBuilder, type, host.getName());
        embeds.embedDivider(eBuilder);
        embeds.embedHost(eBuilder, host.getAsMention());
        embeds.embedTime(eBuilder, timeToEvent);
        embeds.embedOptionals(eBuilder);

        //Sends the message embed then sets the global message to that

        eventMessage = channel.sendMessageEmbeds(eBuilder.build()).complete();

        //Gets the event ID

        eventID = eventMessage.getId();

        //Sets the event ID onto the main message

        embeds.embedId(eBuilder, eventID);

        //Edits the message and adds the event ID

        eventMessage = eventMessage.editMessageEmbeds(eBuilder.build()).complete();
        eventMessage.addReaction(Emoji.fromUnicode("U+2705")).queue();

        eventEmbed = eBuilder;

    }


    private void createJob() {
        timeToStart = Instant.now().plusSeconds(timeToEvent*60);

        JobId id = BackgroundJob.schedule(timeToStart, () -> {
            new SentinelMessage().onEventStart(psCode, eventID);
        });
        addJob(this, id);
    }

    /**
     * @apiNote This is done to get the entire list of options for the embed generator
     * @param ev The event with all the events in it.
     */

    private void getAllNonNullOptions(List<OptionMapping> map) {
        for (OptionMapping option : map) {
            if (option.getAsString() != null && !option.getAsString().isBlank()) {
                nonBlankOptions.add(option);
            }
        }
        nonBlankOptions.remove(0);
        nonBlankOptions.remove(0);
        nonBlankOptions.remove(0);
        nonBlankOptions.remove(0);
        nonBlankOptions.remove(0);
        nonBlankOptions.remove(0);
    }

    private boolean isChannel(TextChannel channel) {
        if (!(channel.getId().equals(Config.getValue(CHANNELID_KEY_VALUE))))
            return false;
        return true;
    }

    public void delayEvent(int delayTime) {

        notfiyAttendee("An event was delayed!");

        EmbedBuilder builder = getEventEmbed();

        timeToStart = timeToStart.plusSeconds(delayTime * 60);
        String timeToStartStr = Long.toString(timeToStart.getEpochSecond());

        builder.addField("New Time:", "<t:" + timeToStartStr + ":R>\n<t:" + timeToStartStr + ">", false);
        builder.setFooter("(Delayed)");
        eventMessage.editMessageEmbeds(builder.build()).queue();

        //Deletes background job and removes the job from main list 

        BackgroundJob.delete(eventJobs.get(this));
        eventJobs.remove(this);

        //Starts new job with delay
        JobId id = BackgroundJob.schedule(Instant.now().plusSeconds(delayTime * 60),
                () -> {
                    new SentinelMessage().onEventStart(psCode, eventID);
                });

        //Re-adds job into job list

        eventJobs.put(this, id);

    }
    
    /**
     * @apiNote Cancels an event. It will send a message to all participants in said
     *          event
     * @param message The message that was generated from events
     */

     public void cancelEvent() {

        //Notifies event attendees

        notfiyAttendee("An Event was Cancelled!");

        //Deletes the jobrunr, removes it from the job and removes this instance

        BackgroundJob.delete(eventJobs.get(this));
        eventJobs.remove(this);
        futureEvents.remove(eventID);

        //Sets current event message as cancelled

        EmbedBuilder editedMessage = new EmbedBuilder();
        editedMessage.setFooter("EventID: " + eventID + "\n(Cancelled)" );
        eventMessage.editMessageEmbeds(editedMessage.build()).queue();
        eventMessage.clearReactions();
        System.out.println(eventID + " has been deleted!");
    }

    /**
     * 
     * @param title The reason for sending the message
     */

    private void notfiyAttendee(String title) {
        EmbedBuilder notifyPlayerMessage = new EmbedBuilder();
        notifyPlayerMessage.setTitle(title);
        notifyPlayerMessage.addField("Event:", eventMessage.getJumpUrl(), false);
        notifyPlayerMessage.setFooter("EventID: " + eventMessage.getId());

        //Iterates through atendee list to send a message to every person 

        for (User user : attendingUsersList) {
            user.openPrivateChannel().queue(
                    channel -> {
                        channel.sendMessageEmbeds(notifyPlayerMessage.build()).queue();
                    });
        }
    }

    public String getEventID() {
        return eventID;
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

    public EmbedBuilder getEventEmbed() {
        return eventEmbed;
    }

    public Instant getTimeToStart() {
        return timeToStart;
    }

    public String getSquadColour() {
        return squadColour;
    }

    public String getSpawnLocation() {
        return spawnLocation;
    }

    public String getVoiceChannel() {
        return voiceChannel;
    }

    /**
     * 
     * @return Returns a hashmap with all current events that are currently on going. The Key is the 
     * event ID (the message ID)
     */
    public static HashMap<String, SentinelEvent> getAllEvents() {
        return futureEvents;
    }

    /**
     * 
     * @return A hash map that contains the corresponding job that is working with the sentinel event
     * Used to cancel and delay events.
     */
    public static HashMap<SentinelEvent, JobId> getAllJobs() {
        return eventJobs;
    }

    /**
     * 
     * @return Returns the embed message associated with the event.
     */

    public Message getEventMessage() {
        return eventMessage;
    }

    /**
     * 
     * @param eventID The eventID corresponding to the event
     * @return The event initalized with that eventID
     */

    public static SentinelEvent getEvent(String eventID) {
        return futureEvents.get(eventID);
    }

    /**
     * 
     * @param event An instance of Sentinel Event
     * @param id The JobID that JobRunr generates
     */
    public static void addJob(SentinelEvent event, JobId id) {
        eventJobs.put(event, id);
    }

    /**
     * 
     * @param id The jobID that JobRunr generates
     */
    public void addJob(JobId id){
        eventJobs.put(this, id);
    }

    /**
     * 
     * @param event An instantiated sentinel event
     */

    public static void removeJob(SentinelEvent event) {
        eventJobs.remove(event);
    }

    /**
     * 
     * @return The discord URL associated with the event
     */
    public String getEventLink() {
        return eventMessage.getJumpUrl();
    }

    public static HashMap<String, SentinelEvent> getCurrentEvents() {
        return currentEvents;
    }
}
