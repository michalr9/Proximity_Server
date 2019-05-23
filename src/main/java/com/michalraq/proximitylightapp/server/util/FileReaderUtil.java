package com.michalraq.proximitylightapp.server.util;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;

public class FileReaderUtil {

    public static String getProperty(String key){

        Properties properties = new Properties();

        try (InputStream input = FileReaderUtil.class.getClassLoader().getResourceAsStream("esp.properties")) {
            properties.load(input);
            } catch (IOException e) {
                e.printStackTrace();
            }

        return properties.getProperty(key);

    }

    public static ArrayList<String> getFileResources(String fileName){

        File file;
        ArrayList<String> list = new ArrayList<String>();

        ClassLoader classLoader = FileReaderUtil.class.getClassLoader();

            URL resource = classLoader.getResource(fileName);
            if (resource == null) {
                throw new IllegalArgumentException("file is not found!");
            } else {
                file = new File(resource.getFile());
            }


        try (FileReader reader = new FileReader(file);
             BufferedReader br = new BufferedReader(reader)) {

            String line;
            while ((line = br.readLine()) != null) {
                list.add(line.trim());
            }
        } catch (IOException e) {
            System.out.println("Reading failed!");
        }
    return list;
    }

    public static ArrayList<String> readDbFile(String filename){
        File file = new File(filename);
        Scanner in ;
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
