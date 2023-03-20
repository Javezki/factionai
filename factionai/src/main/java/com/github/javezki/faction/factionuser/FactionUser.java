package com.github.javezki.faction.factionuser;

import net.dv8tion.jda.api.entities.User;

public abstract class FactionUser {

    private User jdaUser;

    FactionUser(User user) {
        jdaUser = user;
    }

    public User getUser() {
        return jdaUser;
    }

    public abstract void serializeToSQL();
}

