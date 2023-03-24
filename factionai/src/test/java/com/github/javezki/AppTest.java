package com.github.javezki;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.github.javezki.economy.Melee;
import com.github.javezki.economy.Primary;
import com.github.javezki.economy.Secondary;
import com.github.javezki.faction.factionuser.FactionUser;
import com.github.javezki.faction.factionuser.Loadout;
import com.github.javezki.sqlconfig.SQLConfig;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */

     private long id = 4050;

    @Before 
    public void initDBConn() {
        SQLConfig.connectToDB();
        SQLConfig.createID(id);
    }

    @Test
    public void shouldAnswerWithTrue() {
        // Loadout loadout = new Loadout(Primary.AK_74N, Secondary.B75, Melee.DOWNRANGE);
        assertTrue("Does return true?", SQLConfig.isAlreadyInTable(4050));
        // SQLConfig.setBalance(id, 0);
        // SQLConfig.setRank(id, 1234);
        // if (!(SQLConfig.addLoadout(id, loadout, 1))) System.out.println("Loadout is maxed");
        // assertEquals(1234, SQLConfig.getUserRank(id));
        // assertEquals(0, SQLConfig.getUserBalance(id), 0);
    }

    @Test
    public void loadoutTests() {
        Loadout loadout = new Loadout(Primary.AK_74N, Secondary.B75, Melee.DOWNRANGE);
        SQLConfig.setLoadout(id, loadout, 1);
        assertEquals(Primary.AK_74N, SQLConfig.getLoadout(id, 1).getPrimary());
        assertEquals(Secondary.B75, SQLConfig.getLoadout(id, 1).getSecondary());
        assertEquals(Melee.DOWNRANGE, SQLConfig.getLoadout(id, 1).getMelee());
    }

    @Test
    public void inventoryTests() { 
        Loadout loadout = new Loadout(Primary.AK_74N, null, Melee.DOWNRANGE);
        SQLConfig.setInventory(id, loadout, 1);
        loadout = SQLConfig.getInventory(id, 1);
        assertEquals(Primary.AK_74N, loadout.getPrimary());
        assertEquals(Secondary.NONE, loadout.getSecondary());
        assertEquals(Melee.DOWNRANGE, loadout.getMelee());
    }


}

