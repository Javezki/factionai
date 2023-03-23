package com.github.javezki.faction.factionuser;

import org.jetbrains.annotations.Nullable;

import com.github.javezki.economy.Melee;
import com.github.javezki.economy.Primary;
import com.github.javezki.economy.Secondary;

public class Loadout {
    
    private Primary primary;
    private Secondary secondary;
    private Melee melee;


    public Loadout(Primary primary, Secondary secondary, Melee melee) {
        this.primary = primary;
        this.secondary = secondary;
        this.melee = melee;
    }

    public Primary getPrimary() {
        return primary;
    }

    public Secondary getSecondary() {
        return secondary;
    }

    public Melee getMelee() {
        return melee;
    }
    @Nullable
    public void setPrimary(Primary primary) {
        this.primary = primary;
    }

    @Nullable
    public void setSecondary(Secondary secondary) {
        this.secondary = secondary;
    }

    @Nullable
    public void setMelee(Melee melee) {
        this.melee = melee;
    }
}
