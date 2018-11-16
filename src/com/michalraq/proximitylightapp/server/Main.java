package com.michalraq.proximitylightapp.server;

public class Main {

    public static void main(String[] args) {
        Server server = new Server();

        while(true){
            server.run();
        }
    }
}
