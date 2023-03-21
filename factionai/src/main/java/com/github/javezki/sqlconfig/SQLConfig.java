package com.github.javezki.sqlconfig;


import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;



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
        if (conn == null) return false;
        try {
            DatabaseMetaData meta =  conn.getMetaData();
            ResultSet resultSet = meta.getTables(null, null, tableName, new String[] {"TABLE"});
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean databaseExists(String databaseName) {
        if (conn == null) return false;

        try {
            DatabaseMetaData meta = conn.getMetaData();

            ResultSet set = meta.getCatalogs();
            while (set.next()) {
                if(set.getString("TABLE_CAT").equals(databaseName)) return true;
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
            String sql = "CREATE TABLE userinfo (id INT, rank VARCHAR(255), balance DOUBLE(255, 2), )";
            statement.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
