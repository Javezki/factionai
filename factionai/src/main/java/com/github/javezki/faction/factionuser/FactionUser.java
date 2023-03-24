package com.github.javezki.faction.factionuser;

import java.util.ArrayList;
import java.util.List;

import com.github.javezki.faction.Faction;
import com.github.javezki.sqlconfig.SQLConfig;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

public class FactionUser {

    private double balance;
    private long userID;
    private long rankID;
    private Loadout loadout1;
    private Loadout loadout2;
    private Loadout loadout3;
    public static long defaultRankID = 1088178490643320872L;
    public final static int INVENTORY_SIZE = 5;
    public final static int LOADOUT_SIZE = 3;

    FactionUser (User user) {
        userID = user.getIdLong();
        balance = SQLConfig.getUserBalance(userID);
        rankID = SQLConfig.getUserRank(userID);
        loadout1 = null;
        loadout2 = null;
        loadout3 = null;
    }

    public FactionUser (long ID) {
        this.userID = ID;
        balance = SQLConfig.getUserBalance(ID);
        rankID = SQLConfig.getUserRank(ID);
    }


    FactionUser (int ID, double balance, Loadout loadout1, Loadout loadout2, Loadout loadout3) {
        this.userID = ID;
        this.balance = balance;
        this.loadout1 = loadout1;
        this.loadout2 = loadout2;
        this.loadout3 = loadout3;
    }

    public long getUserID() {
        return userID;
    }

    public double getBalance() {
        return balance;
    }

    public void addBalance(double toAdd) {
        SQLConfig.setBalance(userID, balance += toAdd);
    }

    public void subtractBalance(double toSubtract) {
        SQLConfig.setBalance(userID, balance -= toSubtract);
    }

    public void onDeath() {
        SQLConfig.setBalance(userID, balance *= 0.85);
        
    }

    public void setLoadout1(Loadout loadout) {
        loadout1 = loadout;
    }
    
    public void setLoadout2(Loadout loadout) {
        loadout2 = loadout;
    }
    
    public void setLoadout3(Loadout loadout) {
        loadout3 = loadout;
    }

    public List<Loadout> getLoadouts() {
        List<Loadout> loadouts = new ArrayList<>();
        loadouts.add(loadout1);
        loadouts.add(loadout2);
        loadouts.add(loadout3);
        return loadouts;
    }

    public Role getRank() {
        return Faction.jda.getGuildById(Faction.guildID).getRoleById(rankID);
    }

    public User getUser() {
        return Faction.jda.getUserById(userID);
    }



}

