package com.github.javezki;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.github.javezki.sqlconfig.SQLConfig;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */

    @Test
    public void shouldAnswerWithTrue() {
        SQLConfig.connectToDB();
        assertTrue("does DB exist?", SQLConfig.databaseExists("factiondb"));
    }
}
