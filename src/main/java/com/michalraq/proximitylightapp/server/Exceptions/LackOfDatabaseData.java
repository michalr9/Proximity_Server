package com.michalraq.proximitylightapp.server.Exceptions;

public class LackOfDatabaseData extends Throwable{
    public LackOfDatabaseData() {
        System.err.println("Array with database data is probably empty!");
    }
}
