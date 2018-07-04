package com.example.cchiv.kanema.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class HTTPFetchRequest {

    public HTTPFetchRequest() {
        super();
    }

    public StringBuilder fetchFromURL(URL url) {

        try {
            URLConnection urlConnection = url.openConnection();
            urlConnection.addRequestProperty("method", "GET");
            urlConnection.setConnectTimeout(15000);
            urlConnection.setReadTimeout(15000);
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String line = bufferedReader.readLine();
            StringBuilder stringBuilder = new StringBuilder();
            while(line != null) {
                stringBuilder.append(line);
                line = bufferedReader.readLine();
            }

            return stringBuilder;
        } catch(IOException e) {
            Log.v("HTTPFetchRequest", e.toString());
        }

        return null;
    }
}
