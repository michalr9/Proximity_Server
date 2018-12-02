package com.michalraq.proximitylightapp.server;

import com.michalraq.proximitylightapp.server.Database.DatabaseManager;
import com.michalraq.proximitylightapp.server.Exceptions.LackOfDatabaseData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {

    public static void main(String[] args) {
        Server server = new Server();

        while(true){
            server.run();
        }

    }
}
