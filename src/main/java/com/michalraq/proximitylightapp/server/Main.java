package com.michalraq.proximitylightapp.server;


import com.michalraq.proximitylightapp.server.service.Server;
import com.michalraq.proximitylightapp.server.util.FileReaderUtil;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

public class Main {
private final static String keystoreName = "mrstore.jks";
private final static String password = "password";
private final static int portNumber =12345;

    public static void main(String[] args) {
        URL res = Main.class.getClassLoader().getResource(keystoreName);
        File file;
        SSLServerSocketFactory sslServerSocketFactory;
        SSLServerSocket sslServerSocket=null;
        try {
            file = Paths.get(res.toURI()).toFile();
        String absolutePath = file.getAbsolutePath();

        System.setProperty("javax.net.ssl.keyStore",absolutePath);
        System.setProperty("javax.net.ssl.keyStorePassword", password);

             sslServerSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
             sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(portNumber);

        while(true){
            new Server(sslServerSocket.accept()).start();
        }

        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
            if(sslServerSocket!=null) {
                try {
                    sslServerSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}
