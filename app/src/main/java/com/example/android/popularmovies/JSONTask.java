package com.example.android.popularmovies;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Tryndamere on 26/01/2017.
 */

public class JSONTask {
    private String txtJson=null;


    //method to retrieve the JSON data from the url
    public String JSONTask (URL url){
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try{
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream stream = connection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(stream));

            StringBuffer buffer = new StringBuffer();
            String line = "";

            //with this loop the buffer gets the whole lines of the JSON data in a string format
            while ((line = reader.readLine()) != null) {
                buffer.append(line+"\n");
            }
            txtJson=buffer.toString();


        }catch (Exception e){
            e.printStackTrace();


        }finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        
        return txtJson;
    }
}
