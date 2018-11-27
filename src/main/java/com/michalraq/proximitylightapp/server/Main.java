package com.michalraq.proximitylightapp.server;

import com.michalraq.proximitylightapp.server.Database.DatabaseManager;
import com.michalraq.proximitylightapp.server.Exceptions.LackOfDatabaseData;

public class Main {

    public static void main(String[] args) {
      //  Server server = new Server();
        try {
            DatabaseManager database = new DatabaseManager();
            System.out.println("Url uzupelniono" + database.getUrl());
database.connectToDatabase();
database.disconnectDatabase();
        }catch (LackOfDatabaseData e){
            System.out.println("Wyjatek");
        }


//        while(true){
//            server.run();
//        }
    }
}
