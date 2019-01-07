package com.michalraq.proximitylightapp.server;

import lombok.Getter;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class HttpURLClient {

   @Getter
   public int returnCode;

  public  HttpPost createConnectivity(String url, String username, String password)
    {
        HttpPost post = new HttpPost(url);

        String auth=new StringBuffer(username).append(":").append(password).toString();
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("UTF-8")));
        String authHeader = "Basic " + new String(encodedAuth);
        post.setHeader("AUTHORIZATION", authHeader);

        post.setHeader("Content-Type", "application/json");
        post.setHeader("Accept", "application/json");
        post.setHeader("X-Stream" , "true");
        return post;
    }

    public void executeReq(String jsonData, HttpPost httpPost)
    {
        try{
            executeHttpRequest(jsonData, httpPost);
        }catch(HttpHostConnectException e){
            System.err.println(" ESP nieosiągalne: "+e);
        }
        catch (UnsupportedEncodingException e){
            System.err.println("error while encoding api url : "+e);
        }
        catch (IOException e){
            System.err.println("ioException occured while sending http request : "+e);
        }
        catch(Exception e){
            System.err.println("exception occured while sending http request : "+e);
        }
        finally{
            httpPost.releaseConnection();
        }
    }

   private void executeHttpRequest(String jsonData,  HttpPost httpPost)  throws  IOException
    {
        HttpResponse response;
        httpPost.setEntity(new StringEntity(jsonData));
        HttpClient client = HttpClientBuilder.create().build();
        response = client.execute(httpPost);
        System.out.println("Post parameters : " + jsonData );
        System.out.println("Response Code : " +response.getStatusLine().getStatusCode());
        returnCode =response.getStatusLine().getStatusCode();
//        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
//        while ((line = reader.readLine()) != null){ result.append(line); }
//        System.out.println(result.toString());
    }
}
