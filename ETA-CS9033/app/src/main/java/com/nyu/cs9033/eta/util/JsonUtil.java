package com.nyu.cs9033.eta.util;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class JsonUtil {
    private URL url;

    /**
     *  This method will provide connection between client and server.
     *  We send Json format data to server and get the response from server.
     */
    public String connectServer(String newUrl,JSONObject object){
        String result="";
        try {
            url = new URL(newUrl);
            HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(10000);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.addRequestProperty("Content-length", "application/json");
            urlConnection.addRequestProperty("X-Request-With", "XMLHttpRequest");
            urlConnection.connect();

            OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
            writer.write(object.toString());
            writer.flush();
            writer.close();

            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String s="";
            StringBuilder stringBuilder = new StringBuilder("");
            while((s = bufferedReader.readLine())!=null){
                stringBuilder.append(s);
            }
            result = stringBuilder.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
