package com.stocklord.stocklord;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by Aishwarya on 10/31/2015.
 */
public class GetData
{
        Context context;

        GetData(Context ctxt) {
            context = ctxt;
        }

        public boolean haveNetworkConnection() {
            boolean haveConnectedWifi = false;
            boolean haveConnectedMobile = false;


            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
            NetworkInfo[] netInfo = cm.getAllNetworkInfo();
            for (NetworkInfo ni : netInfo) {
                if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                    if (ni.isConnected())
                        haveConnectedWifi = true;
                if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                    if (ni.isConnected())
                        haveConnectedMobile = true;
            }
            return haveConnectedWifi || haveConnectedMobile;
        }

         public InputStream connect(String urlstr)
        {
                try {

                    URL url = new URL(urlstr);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000 /* milliseconds */);
                    conn.setConnectTimeout(15000 /* milliseconds */);
                    conn.connect();


                    int code = conn.getResponseCode();
                    //Check if received response without error code 200
                    if (code == 200) {
                        InputStream in = conn.getInputStream();
                        return in;


                    }
                    return null;
                }
                catch (Exception e)
                {
                    return null;
                }
        }


        public String post(String urlstr, String param)
        {

                    try {
                        URL url = new URL(urlstr);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setDoOutput(true);
                        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                        connection.setRequestMethod("POST");
                        OutputStreamWriter request = new OutputStreamWriter(connection.getOutputStream());
                        if(param!=null) {
                            request.write(param);
                        }
                        request.flush();
                        request.close();
                        String line = "";
                        InputStreamReader isr = new InputStreamReader(connection.getInputStream());
                        BufferedReader reader = new BufferedReader(isr);
                        StringBuilder sb = new StringBuilder();
                        while ((line = reader.readLine()) != null) {
                            sb.append(line + "\n");
                        }
                        // Response from server after login process will be stored in response variable.
                        String response = sb.toString();
                        // You can perform UI operations here
                        isr.close();
                        reader.close();
                        String res = response.split("<")[0];
                        return res;
                    }
                    catch (Exception e) {
                        return null;
                    }

        }
    public String conv(Map<String,Object> m)
    {
        String parameters;

        StringBuilder postData = new StringBuilder();
        try {
            for (Map.Entry<String, Object> param : m.entrySet()) {
                if (postData.length() != 0) postData.append('&');

                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));

            }
            parameters = postData.toString();
            return parameters;
        }
        catch(Exception e)
        {
            return null;
        }
    }

}


