package com.michalraq.proximitylightapp.server.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class FileReader {


    public static ArrayList<String> readDbFile(String filename){
        File file = new File(filename);
        Scanner in = null;
        ArrayList<String> list = new ArrayList<String>();
        try {
            in = new Scanner(file);

        while(in.hasNextLine()) {
            list.add(in.nextLine().trim());
        }

        } catch (FileNotFoundException e) {
            System.out.println("File NOT FOUND !!!");
            return null;
        }
        return list;
    }
}
