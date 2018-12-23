package com.michalraq.proximitylightapp.server;
import com.michalraq.proximitylightapp.server.Database.DatabaseManager;
import com.michalraq.proximitylightapp.server.Exceptions.LackOfDatabaseData;
import com.michalraq.proximitylightapp.server.Util.StringOperations;
import org.apache.http.client.methods.HttpPost;
import org.json.simple.JSONObject;

import java.io.*;
import java.net.*;
import java.sql.SQLException;

public class Server {

    int 	port = 12345;

    private ServerSocket serverSocket;
    private Socket socket = null;
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;
    private String message;
    private DatabaseManager database;
    Server(){
        try {
            database = new DatabaseManager();
        } catch (LackOfDatabaseData lackOfDatabaseData) {
            System.err.println("Connection with database will be imposible to do!");
            lackOfDatabaseData.printStackTrace();
        }
    }

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

            if(socket!=null || !socket.isClosed()){
                database.connectDatabase();
            }

            MessageContent messageContent = new MessageContent();

            while((message = bufferedReader.readLine())!=null) {

                decodeMessage(messageContent);

                if(messageContent.getSignal()==1) {
                    database.insertIntoCmtStatusTIME_IN(messageContent);
                    sendRequestToESP(messageContent);
                    database.updateStatusOfLight(messageContent); //TODO zmienic na ustawianie w bazie jezeli odpowiedz z requesta bedzie poprawna
                }

                if(messageContent.getSignal()==0) {
                    database.insertIntoCmtStatusTIME_OUT(messageContent);
                    sendRequestToESP(messageContent);
                    database.updateStatusOfLight(messageContent);

                }


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

                if(!database.getConnection().isClosed())
                database.disconnectDatabase();
                System.out.println("Zamykam połączenie");
            }
            catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    void decodeMessage(MessageContent messageContent){

        int size = message.length();
        int signal = Integer.parseInt(message.substring(0,1));
        String place = message.substring(1,size);
        messageContent.setSignal(signal);
        messageContent.setPlace(place);
    }

    void sendMessage(String msg) {
            printWriter.println( msg );
            printWriter.flush();
    }

    void sendRequestToESP(MessageContent messageContent){
        int signal = messageContent.getSignal();
        String place = messageContent.getPlace();
        String url="http://192.168.0.19/switch";
        String username="cHJveGltaXR5QWRtaW4=";
        String password="cHJveDIwMThA";
        JSONObject user=new JSONObject();
        user.put("signal", signal);
        user.put("place", place);
        String jsonData=user.toString();
        HttpURLClient httpPostReq=new HttpURLClient();
        HttpPost httpPost=httpPostReq.createConnectivity(url , username, password);
        httpPostReq.executeReq( jsonData, httpPost);
    }
}
