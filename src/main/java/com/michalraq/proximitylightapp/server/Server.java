package com.michalraq.proximitylightapp.server;
import java.io.*;
import java.net.*;
public class Server {

    int 	port = 12345;

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

            MessageContent messageContent = new MessageContent();

            while((message = bufferedReader.readLine())!=null) {

                messageContent.setMessage(message);
                decodeMessage(messageContent);


                    System.out.println( message);
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

    void decodeMessage(MessageContent messageContent){
        int size = message.length();
        int signal = Integer.parseInt(message.substring(0,1));
        String place = message.substring(1,size+1);
        messageContent.setSignal(signal);
        messageContent.setPlace(place);
    }

    void sendMessage(String msg) {
            printWriter.println( msg );
            printWriter.flush();
    }

}
