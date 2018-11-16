package com.michalraq.proximitylightapp.server;
import java.io.*;
import java.net.*;
public class Server {

    int 	port = 12345;
    String 	host = "127.0.0.1";

    ServerSocket serverSocket;
    Socket connection = null;
    DataOutputStream out;
    DataInputStream in;
    String message;

    Server(){}

    // metoda obslugujaca klientow =====================================
    void run(){
        try{
            // tworzymy nowe gniazdo -----------------------------------
            serverSocket = new ServerSocket( port);
            // akceptujemy polaczenie ----------------------------------
            connection = serverSocket.accept();
            // wyswietlamy informacje o polaczeniu ---------------------
            System.out.println("Address: " + connection.getInetAddress() + " Port: " + connection.getPort() );

            out = new DataOutputStream(connection.getOutputStream());
            sendMessage("Połączono !");
            byte[] data = new byte[256];

            boolean flaga ;
            do {
                //transmisja przychodzaca
                in = new DataInputStream(connection.getInputStream());
                if(in.read(data)>0) {
                    System.out.println("Odebrano " + new String(data));
                    flaga=true;
                }else
                    flaga=false;

            }while( flaga);

        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
        finally{
            try{
                in.close();
                out.close();
                serverSocket.close();
                System.out.println("Zamykam połączenie");
            }
            catch(IOException ioException){
                ioException.printStackTrace();
            }
        }
    }

    // metoda odpowiadajaca za wysylanie wiadomosci do klientow ========
    void sendMessage(String msg) {
        try{
            byte[] data = msg.getBytes();
            out.write	( data );
            out.flush();
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
    }

}
