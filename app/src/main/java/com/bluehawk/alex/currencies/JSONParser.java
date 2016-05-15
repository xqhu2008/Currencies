package com.bluehawk.alex.currencies;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;


/**
 * Created by Alex on 2016/5/14.
 */
public class JSONParser {

    static InputStream sInputStream = null;
    static JSONObject sReturnJsonObject = null;
    static String sRawJsonString = "";

    public JSONParser() {}

    public JSONObject getJSONFromUrl(String uri) {
        // attempt to get response from server
        try {
            URL url = new URL(uri);
            URLConnection uconn = url.openConnection();
            sInputStream = uconn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(sInputStream, "iso-8859-1"), 8);
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }

            sInputStream.close();
            sRawJsonString = stringBuilder.toString();
        } catch (Exception e) {
            Log.e("Error " + e.toString(), this.getClass().getSimpleName());
        }

        try {
            sReturnJsonObject = new JSONObject(sRawJsonString);
        } catch (JSONException e) {
            Log.e("Parser", "Error " + e.toString());
        }

        return sReturnJsonObject;
    }
}
