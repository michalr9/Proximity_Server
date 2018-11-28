package com.michalraq.proximitylightapp.server;

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
