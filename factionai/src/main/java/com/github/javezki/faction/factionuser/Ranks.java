package com.github.javezki.faction.factionuser;

public enum Ranks {

    SOVEREIGN("Sovereign", "The highest rank of angels, they are the supreme authority and source of all existence."),
    SERAPH("Seraph", "The second highest rank of angels, they are the closest to the Sovereign and radiate pure light and love."),
    CHERUB("Cherub", "The third highest rank of angels, they are the guardians of the Sovereign's throne and wisdom and have multiple wings and faces."),
    THRONE("Throne", "The fourth highest rank of angels, they are the bearers of the Sovereign's justice and power and have wheels covered with eyes."),
    DOMINION("Dominion", "The fifth highest rank of angels, they are the regulators of the angelic duties and domains and have scepters or orbs."),
    VIRTUE("Virtue", "The sixth highest rank of angels, they are the performers of the Sovereign's miracles and blessings on humans and nature."),
    POWER("Power", "The seventh highest rank of angels, they are the protectors of the cosmic order and balance from evil and chaos."),
    PRINCIPALITY("Principality", "The eighth highest rank of angels, they are the overseers of nations and cities and have crowns or diadems."),
    ARCHANGEL("Archangel", "The ninth highest rank of angels, they are the leaders of the angelic army and have trumpets or banners."),
    ANGEL("Angel", "The tenth highest rank of angels, they are the messengers of the Sovereign's will and grace to humans and other beings."),
    GUARDIAN("Guardian", "The eleventh highest rank of angels, they are assigned to protect individual humans from harm and temptation."),
    WATCHER("Watcher", "The twelfth highest rank of angels, they are tasked to observe and record history and events in different realms."),
    FALLEN("Fallen", "The thirteenth highest rank of angels, they are those who challenged or opposed the Sovereign’s authority or plan and were banished from heaven."),
    NEPHILIM("Nephilim", "The fourteenth highest rank of angels, they are offspring between fallen angels and humans who possess great power but also great flaws."),
    GRIGORI("Grigori", "The fifteenth highest rank of angels, they are watchers who became fascinated with the mysteries of creation and explored them without permission or restraint."),
    OUTCAST("Outcast", "The lowest rank of angels");

    private String normal;
    private String description;
    
    Ranks(String normal, String description) {
        this.normal = normal;
        this.description = description;
    }

    @Override
    public String toString() {
        return normal;
    }

    public String getDescription() {
        return description;
    }

    
}
