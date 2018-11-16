package com.michalraq.proximitylightapp.server;
import java.io.*;
import java.net.*;
public class Server {

    int 	port = 12345;
    String 	host = "127.0.0.1";

    ServerSocket serverSocket;
    Socket socket = null;
    PrintWriter printWriter;
    BufferedReader bufferedReader;
    String message;

    Server(){}

    // metoda obslugujaca klientow =====================================
    void run(){
        try{
            // tworzymy nowe gniazdo -----------------------------------
            serverSocket = new ServerSocket( port);
            // akceptujemy polaczenie ----------------------------------
            socket = serverSocket.accept();
            // wyswietlamy informacje o polaczeniu ---------------------
            System.out.println("Address: " + socket.getInetAddress() + " Port: " + socket.getPort() );

            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            printWriter = new PrintWriter(socket.getOutputStream());

            sendMessage("Połączono !");

            while((message = bufferedReader.readLine())!=null) {
                //transmisja przychodzaca
                    System.out.println("Odebrano " + message);
            }

        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
        finally{
            try{
                bufferedReader.close();
                printWriter.close();
                socket.close();
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
            printWriter.println( msg );
            printWriter.flush();
    }

}
