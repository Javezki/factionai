package com.github.javezki;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.github.javezki.faction.factionuser.FactionUser;
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

    @Test
    public void shouldAnswerWithTrue() {
        SQLConfig.connectToDB();
        SQLConfig.createID(id);
        assertTrue("Does return true?", SQLConfig.isAlreadyInTable(4050));
        SQLConfig.setBalance(id, 0);
        SQLConfig.setRank(id, 1234);
        assertEquals(1234, SQLConfig.getUserRank(id));
        assertEquals(0, SQLConfig.getUserBalance(id), 0);
    }


}

