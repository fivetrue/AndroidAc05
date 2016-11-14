package com.fivetrue.gimpo.ac05.utils;


import android.os.AsyncTask;
import android.util.Log;

import com.fivetrue.gimpo.ac05.firebase.model.AppConfig;
import com.fivetrue.gimpo.ac05.firebase.model.User;
import com.fivetrue.gimpo.ac05.service.GcmMessage;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kwonojin on 2016. 11. 9..
 */


public class NotificationSender {

    private static final String TAG = "NotificationSender";

    public static String GCM_SEND_SERVER_URL = "https://gcm-http.googleapis.com/gcm/send";

    public static void sendNotificationToUsers(final GcmMessage message, final AppConfig config){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("/user").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot != null){
                    ArrayList<String> ids = new ArrayList<String>();
                    for(DataSnapshot s : dataSnapshot.getChildren()){
                        String gcmId = s.child("gcmId").getValue(String.class);
                        ids.add(gcmId);
                    }
                    sendNotification(message, config.gcmKey, ids);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void sendNotification(final GcmMessage data, final String key, final List<String> ids){

        new AsyncTask<Void, Void, String>(){

            @Override
            protected String doInBackground(Void... params) {
                PushMessage message = new PushMessage();
                message.registration_ids = ids;
                PushMessage.Data d = new PushMessage.Data();
                d.data = data;
                message.data = d;
                String push = new Gson().toJson(message);
                Map<String, String> header = new HashMap<>();
                header.put("Authorization", "key=" + key);
                header.put("Content-Type", "application/json");
                String response = requestApi(GCM_SEND_SERVER_URL, "POST", false, header, push);
                return response;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.d(TAG, "onPostExecute() called with: s = [" + s + "]");
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private static String requestApi(String api, String method, boolean userCaches, Map<String, String> header, String data){
        String response = "";
        try {
            boolean hasoutbody = method.equalsIgnoreCase("POST");
            final URL url = new URL(api);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);


            if(header != null){
                for(String key : header.keySet()){
                    conn.addRequestProperty(key, header.get(key));
                }
            }

            conn.setUseCaches(userCaches);
            conn.setDoInput(true);
            conn.setDoOutput(hasoutbody);
            conn.connect();

            if(hasoutbody) if (data != null && data.length() > 0) {
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(data);
                writer.flush();
                writer.close();
                os.close();
            }

            int responseCode =conn.getResponseCode();
//            if (responseCode == HttpsURLConnection.HTTP_OK) {
            String line;
            BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line=br.readLine()) != null) {
                response+=line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }


    public static class PushMessage {

        public List<String> registration_ids;
        public Data data;
        public static final class Data{
            private Object data = null;
        }
        @Override
        public String toString() {
            return "PushMessage [registration_ids=" + registration_ids + ", data=" + data + "]";
        }
    }
}
