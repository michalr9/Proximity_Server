package com.michalraq.proximitylightapp.server.service;
import com.michalraq.proximitylightapp.server.data.DatabaseManager;
import com.michalraq.proximitylightapp.server.exceptions.LackOfDatabaseData;
import com.michalraq.proximitylightapp.server.data.MessageContent;
import org.apache.http.client.methods.HttpPost;
import org.json.simple.JSONObject;

import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class Server extends Thread{


    private Socket client = null;
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;
    private String message;
    private DatabaseManager database;
    private ArrayList<Integer> codeTab ;
    private ArrayList<String> acceptedClients ;
    private static Boolean isRejected=false;
    public Server(Socket socket){
        try {
            client=socket;
            database = new DatabaseManager();
        } catch (LackOfDatabaseData lackOfDatabaseData) {
            System.err.println("Connection with database will be imposible to do!");
            lackOfDatabaseData.printStackTrace();
        }

       codeTab = new ArrayList<>(Arrays.asList(200,201,202,203,204,205,206));
        acceptedClients = new ArrayList<>(Arrays.asList("/192.168.0.10","/192.168.0.26","/192.168.0.157","/192.168.0.31"));
        //192.168.0.157 huawei 192.168.0.31 lg
    }

    public void run(){
        try{

            // wyswietlamy informacje o polaczeniu ---------------------
            System.out.println("Address: " + client.getInetAddress() + " Port: " + client.getPort() );

            if(acceptedClients.contains(client.getInetAddress().toString())) {
                isRejected=false;

                bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                printWriter = new PrintWriter(client.getOutputStream());
                sendMessage("Połączono !");

                if(database.getConnection() == null)
                    database.connectDatabase();


                int success;
                MessageContent messageContent = new MessageContent();

                while ((message = bufferedReader.readLine()) != null) {

                    decodeMessage(messageContent);

                    if (messageContent.getSignal() == 1) {
                        success = sendRequestToESP(messageContent);

                        if (codeTab.contains(success)) {
                            database.insertIntoCmtStatusTIME_IN(messageContent);
                            database.updateStatusOfLight(messageContent);
                        } else {
                            sendMessage("Światło nie zostało włączone!");
                        }
                    }

                    if (messageContent.getSignal() == 0) {
                        success = sendRequestToESP(messageContent);

                        if (codeTab.contains(success)) {
                            database.insertIntoCmtStatusTIME_OUT(messageContent);
                            database.updateStatusOfLight(messageContent);
                        } else {
                            sendMessage("Światło nie zostało wyłączone!");

                        }

                    }
                    System.out.println(message);
                }

            }else
            {
                printWriter = new PrintWriter(client.getOutputStream());
                sendMessage("Połączenie niedozwolone !");
                isRejected=true;
            }
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
        finally{
            try{
            disconnect();
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

    /**
     * Wysłanie żądania do serwera
     * @param messageContent
     * @return int - kod odpowiedzi
     */
    private int sendRequestToESP(MessageContent messageContent){
        int signal = messageContent.getSignal();
        String place = messageContent.getPlace();
        String url="http://192.168.0.248/switch";
        String username="cHJveGltaXR5QWRtaW4=";
        String password="cHJveDIwMThA";
        JSONObject user=new JSONObject();
        user.put("signal", signal);
        user.put("place", place);
        String jsonData=user.toString();
        HttpURLClient httpPostReq=new HttpURLClient();
        HttpPost httpPost=httpPostReq.createConnectivity(url , username, password);
        httpPostReq.executeReq( jsonData, httpPost);
        return httpPostReq.getReturnCode();
    }

    private void deactivateLights(){
        MessageContent messageContent1 = new MessageContent();
        MessageContent messageContent2 = new MessageContent();
        MessageContent messageContent3 = new MessageContent();

        messageContent1.setSignal(0);
        messageContent2.setSignal(0);
        messageContent3.setSignal(0);

        messageContent1.setPlace("biuro");
        messageContent2.setPlace("salon");
        messageContent3.setPlace("kuchnia");

         try {

            sendRequestToESP(messageContent1);

             Thread.sleep(1000);
             sendRequestToESP(messageContent2);

             Thread.sleep(1000);
             sendRequestToESP(messageContent3);

         } catch (InterruptedException e) {
             e.printStackTrace();
         }

    }

    private void disconnect() throws IOException, SQLException {
        if(!isRejected)
        deactivateLights();

        if(bufferedReader!=null)
        bufferedReader.close();
        if(printWriter!=null)
        printWriter.close();

        client.close();

        if(!isRejected) {
            if (client != null || !client.isClosed()) {
                if (database.getConnection() != null)
                    if (!database.getConnection().isClosed())
                        database.disconnectDatabase();
            }
        }
        System.out.println("Zamykam połączenie");
    }
}
