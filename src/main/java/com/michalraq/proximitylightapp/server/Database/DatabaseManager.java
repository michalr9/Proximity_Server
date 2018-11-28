package com.michalraq.proximitylightapp.server.Database;

import com.michalraq.proximitylightapp.server.Exceptions.LackOfDatabaseData;
import com.michalraq.proximitylightapp.server.Util.FileReader;
import lombok.Getter;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

public class DatabaseManager {

    @Getter private String url ;
    @Getter private Connection connection;

   public DatabaseManager() throws LackOfDatabaseData {
       ArrayList<String> list = FileReader.readDbFile("database.txt");
       String hostName;
       String dbName;
       String user;
       String password;
       if (!list.isEmpty()) {
           hostName = list.get(0);
           dbName = list.get(1);
           user = list.get(2);
           password = list.get(3);
           url = String.format("jdbc:sqlserver://%s:1433;database=%s;user=%s;password=%s;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;", hostName, dbName, user, password);


       } else {
           hostName = "";
           dbName = "";
           user = "";
           password = "";
           throw new LackOfDatabaseData();
       }
   }

    public Boolean connectDatabase(){
        try {
                connection = DriverManager.getConnection(url);
                String schema = connection.getSchema();
                System.out.println("Successful connection - Schema: " + schema);
                System.out.println("Connection with database established");
                return true;
        }
        catch (Exception e) {
            System.err.println("Error occured during connect to database!");
            return false;
        }
    }

    public Boolean disconnectDatabase(){
        try {
            if(connection!=null)
            connection.close();
            System.out.println("Connection with database closed");
            return true;
        } catch (SQLException e) {
            System.err.println("Error occured during disconnect to database!");
        return false;
        }
    }


}
