package com.github.javezki.sqlconfig;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.github.javezki.faction.factionuser.FactionUser;
import com.github.javezki.faction.factionuser.Loadout;
import com.google.gson.Gson;

public class SQLConfig {

    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";

    private static final String DB_URL = "jdbc:mysql://localhost:3306/factiondb";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "{Nathan}123";

    private static Connection conn = null;

    public static void connectToDB() {
        try {
            Class.forName(DB_DRIVER);

            conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);

            if (conn != null) {
                System.out.println("Success to connecting DB!");
            } else {
                System.out.println("Failure to connect to DB!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean tableExists(String tableName) {
        if (conn == null)
            return false;
        try {
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet resultSet = meta.getTables(null, null, tableName, new String[] { "TABLE" });
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean databaseExists(String databaseName) {
        if (conn == null)
            return false;

        try {
            DatabaseMetaData meta = conn.getMetaData();

            ResultSet set = meta.getCatalogs();
            while (set.next()) {
                if (set.getString("TABLE_CAT").equals(databaseName))
                    return true;
            }
            return false;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    public static void createUserInfoTable() {
        try {
            Statement statement = conn.createStatement();
            String sql = "CREATE TABLE userinfo ( " +
                    " id INTEGER, " +
                    " Ranks VARCHAR(50),  " +
                    " Balance DOUBLE(50, 2), " +
                    " loadout1 LONGBLOB )," +
                    " loadout2 LONGBLOB, " +
                    " loadout3 LONGBLOB);";
            statement.execute(sql);
            System.out.println("Info Table create");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createID(long ID) {

        if (isAlreadyInTable(ID)) return;
        try {
            String sql = "INSERT INTO userinfo (user_id, Balance, Ranks) " +
                    " VALUES (?, ?, ?) ";
            String initInv = "INSERT INTO userinventory (user_id) " +
            " VALUES (?) ";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setLong(1, ID);
            statement.setInt(2, 50);
            statement.setLong(3, FactionUser.defaultRankID);
            statement.execute();
            statement = conn.prepareStatement(initInv);
            statement.setLong(1, ID);
            statement.execute();
            System.out.println("Successfully created ID!: " + Long.toString(ID));
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void setBalance(long ID, double value) {

        String query = "UPDATE " + "userinfo" + 
        " SET Balance = ? " +
        " WHERE user_id = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setLong(2, ID);
            statement.setDouble(1, value);
            statement.execute();
            System.out.println("Successfully set user balance");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void setRank(long ID, long value) {
        String query = "UPDATE " + "userinfo" + 
        " SET Ranks = ? " +
        " WHERE user_id = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setLong(2, ID);
            statement.setLong(1, value);
            statement.execute();
            System.out.println("Successfully set user balance");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean addLoadout(long ID, Loadout loadout, int loadoutNumber) {
        if (loadoutNumber > 3 || loadoutNumber < 0) return false;
        if (isMaxLoadout(ID)) return false;
        String query = "INSERT INTO " + "userinventory" + 
        " (user_id, number, primaryweapon, secondaryweapon, melee, loadout) VALUES (?, ?, ?, ?, ?, true)";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setLong(1, ID);
            statement.setInt(2, loadoutNumber);
            statement.setString(3, loadout.getPrimary().name());
            statement.setString(4, loadout.getSecondary().name());
            statement.setString(5, loadout.getMelee().name());
            statement.execute();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    }

    public static boolean addInventory(long ID, Loadout loadout, int inventoryNumber) {
        if (inventoryNumber > 10 || inventoryNumber < 0) return false;
        String query = "INSERT INTO " + "userinventory" + 
        " (user_id, number, primaryweapon, secondaryweapon, melee, loadout) VALUES (?, ?, ?, ?, ?, true)";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setLong(1, ID);
            statement.setInt(2, inventoryNumber);
            statement.setString(3, loadout.getPrimary().name());
            statement.setString(4, loadout.getSecondary().name());
            statement.setString(5, loadout.getMelee().name());
            statement.execute();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    }

    public static double getUserBalance(long ID) {
        double balance = 0;
        String query = "SELECT Balance FROM " + "userinfo" + " WHERE user_id = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setLong(1, ID);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                balance = resultSet.getDouble("Balance");
                System.out.println("The balance of the player: " + balance);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return balance;
    }

    public static long getUserRank(long ID) {
        long rank = 0;
        String query = "SELECT Ranks FROM " + "userinfo" + " WHERE user_id = ?";
        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setLong(1, ID);
            ResultSet set = statement.executeQuery();
            set.next();
            rank = set.getLong("Ranks");
            System.out.println("The rank of the player: " + Long.toString(rank));
        }  catch(SQLException e) {
            e.printStackTrace();
        }
        return rank;
    }

    public static boolean isMaxLoadout(long ID) {
        boolean max = false;
        String query = "SELECT COUNT(*) from userinventory WHERE user_id = ? AND loadout IS TRUE";
        try (PreparedStatement statment = conn.prepareStatement(query)) {
            statment.setLong(1,ID);
            try (ResultSet set = statment.executeQuery()) {
                set.last();
                if (set.getRow() >= 3) max = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return max;
    }

    public static boolean isMaxInventory(long ID) {
        boolean max = false;
        String query = "SELECT COUNT(*) from userinventory WHERE user_id = ? AND inventory IS TRUE";
        try (PreparedStatement statment = conn.prepareStatement(query)) {
            statment.setLong(1,ID);
            try (ResultSet set = statment.executeQuery()) {
                set.last();
                if (set.getRow() >= 5) max = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return max;
    }

    public static boolean isAlreadyInTable(Object obj) {
        boolean exists = false;
        String query = "SELECT * FROM " + "userinfo" + " WHERE user_id = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setObject(1, obj);
            try (ResultSet resultSet = statement.executeQuery()) {
                exists = resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (exists) System.out.println("ID already exists!");
        return exists;
    }

}
