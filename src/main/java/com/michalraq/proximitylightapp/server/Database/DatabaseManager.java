package com.michalraq.proximitylightapp.server.Database;

import com.michalraq.proximitylightapp.server.Exceptions.LackOfDatabaseData;
import com.michalraq.proximitylightapp.server.MessageContent;
import com.michalraq.proximitylightapp.server.Util.FileReader;
import lombok.Getter;


import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    public void insertIntoCmtStatusTIME_IN(MessageContent message) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String date = dtf.format(now);

        String sql = "INSERT DEV.CMT_STATUS VALUES \n" +
                "(NEXT VALUE FOR DEV.SEQ_ID_STATUS,CAST('" + date + "'AS smalldatetime),null,'"+message.getPlace()+"')";

        try (Statement stm = connection.createStatement()) {

            stm.execute(sql);
        } catch (SQLException e) {
            System.err.println("Blad insertIntoCmtStatusTIME_IN");
            e.printStackTrace();
        }
    }

    public void updateStatusOfLight(MessageContent message){
        String sql2 = "update DEV.CMT_PLACES\n" +
                "set POWER_ON_OFF = 1\n" +
                "where ID_STATUS= (select current_value from sys.sequences where name = 'SEQ_ID_STATUS')";
    }

       public void insertIntoCmtStatusTIME_OUT(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String date = dtf.format(now);
        StringBuilder build = new StringBuilder("'");
        build.append(date).append("'");
        date = build.toString();
        boolean flag=false;

        //sprawdzenie czy istnieje rekord z aktualna wartoscia sekwencji, jezeli nie to nic nie rob
           try (Statement stm = connection.createStatement()) {
           String SQL = "select CMT_STATUS.ID_STATUS from CMT_STATUS where ID_STATUS = (select current_value from sys.sequences where name = 'SEQ_ID_STATUS');";
           ResultSet rs = stm.executeQuery(SQL);
           rs.next();
           flag=rs.getBoolean("ID_STATUS");
           if(!rs.wasNull())
               flag=true;
           } catch (SQLException e) {
               System.err.println("Blad podczas sprawdzenia czy istnieje rekord");
               e.printStackTrace();

           }

        if(flag) {
            try {
                PreparedStatement ps = connection.prepareStatement(
                        "update DEV.CMT_STATUS\n" +
                                "set TIME_OUT = CAST("+date+" as smalldatetime)\n" +
                                "where ID_STATUS= (select current_value from sys.sequences where name = 'SEQ_ID_STATUS')");

                //ps.setString(1, "'"+date+"'");

                ps.executeUpdate();
                ps.close();

            } catch (SQLException e) {
                System.err.println("Blad insertIntoCmtStatusTIME_OUT");
            }
        }else
        {
            System.err.println("Dupa rekord pusty");
        }
   }



}