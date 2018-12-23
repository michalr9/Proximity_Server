package com.michalraq.proximitylightapp.server.Util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StringOperations {

    public static String addSingleQuotes(String word){
        StringBuilder build = new StringBuilder("'");
        build.append(word).append("'");
        word=build.toString();
        return word;
    }

    public static String getCurrentDateYMDHmS(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    public static String addDoubleQuotes(String word){
        StringBuilder build = new StringBuilder('"');
        build.append(word).append('"');
        word=build.toString();
        return word;
    }

}
