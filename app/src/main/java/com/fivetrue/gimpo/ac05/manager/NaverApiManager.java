package com.fivetrue.gimpo.ac05.manager;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Pair;

import com.fivetrue.gimpo.ac05.preferences.ConfigPreferenceManager;
import com.fivetrue.gimpo.ac05.utils.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by kwonojin on 16. 6. 2..
 */
public class NaverApiManager {

    private static final String TAG = "NaverApiManager";

    private static final String API_USER_PROFILE = "https://openapi.naver.com/v1/nid/getUserProfile.xml";

    public interface OnErrorCodeListener{
        void onError(NaverApiManager apiManager, String errorCode, String errorDescription);
    }

    public interface OnOAuthLoginListener extends OnErrorCodeListener{
        void onSuccess(NaverApiManager apiManager, String accessToken, String refreshToken,
                       long expiresAt, String tokenType, String state);
    }

    public interface OnRequestResponseListener{
        void onResponse(String response);
    }

    private static NaverApiManager sInstance = null;

    private Context mContext = null;

    private String mClientId = null;
    private String mClientSecret = null;
    private String mApplicationName = null;

    private ConfigPreferenceManager mConfigPref = null;

    private OnRequestResponseListener mOnRequestResponseListener = null;

    public static NaverApiManager getInstance(){
        return sInstance;
    }

    public static void init(Context context, String clientId, String clientSecret,
                            String applicationName){
        sInstance = new NaverApiManager(context, clientId, clientSecret, applicationName);
    }

    private NaverApiManager(Context context, String clientId, String clientSecret,
                            String applicationName){

        if(clientId == null || clientSecret == null || applicationName == null){
            throw new IllegalArgumentException("Naver parameters must be not null");
        }

        this.mContext = context;
        mClientId = clientId;
        mClientSecret = clientSecret;
        mApplicationName = applicationName;

        mConfigPref = new ConfigPreferenceManager(mContext);
    }

    public void logout(){
        mConfigPref.setToken(null);
        mConfigPref.setUserInfo(null);
    }

    public void reqeustUserProfile(OnRequestResponseListener ll){
        mOnRequestResponseListener = ll;
        new RequestAPITask().execute(API_USER_PROFILE);
    }

    private OnRequestResponseListener onRequestResponseListener = new OnRequestResponseListener() {
        @Override
        public void onResponse(String response) {
            Log.d(TAG, "onResponse : " + response);
            if(mOnRequestResponseListener != null){
                mOnRequestResponseListener.onResponse(response);
                mOnRequestResponseListener = null;
            }
        }
    };

    private class RequestAPITask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {
            String result = null;
            if(params != null && params.length > 0){
                Log.i(TAG, "RequestApiTask start ");
                String api = params[0];
                String at = mConfigPref.getToken().getAccess_token();
                result = requestNaverApi(mContext, at, api);
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            onRequestResponseListener.onResponse(s);
        }
    }

    private String requestNaverApi(Context context, String accessToken, String url) {
        String authHeader = "bearer " + accessToken;
        Pair<String,String>[] header = new Pair[1];
        header[0] = new Pair("Authorization", authHeader);
        String response = requestApi(url, "POST", true, header, "");


        return response;
    }

    public static String requestApi(String api, String method, boolean userCaches, Pair<String, String>[] headers, Pair<String, String>...parameters){
        return requestApi(api, method, userCaches, headers, getPostDataString(parameters));
    }

    public static String requestApi(String api, String method, boolean userCaches, Pair<String, String>[] headers, String data){
        String response = "";
        try {
            boolean hasoutbody = method.equalsIgnoreCase("POST");
            final URL url = new URL(api);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);

            if(headers != null){
                for(Pair<String, String> header : headers){
                    conn.addRequestProperty(header.first, header.second);
                }
            }

            conn.setUseCaches(userCaches);
            conn.setDoInput(true);
            conn.setDoOutput(hasoutbody);
            conn.connect();

            if(hasoutbody){
                if(data != null && data.length() > 0){
                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    writer.write(data);
                    writer.flush();
                    writer.close();
                    os.close();
                }
            }

            int responseCode =conn.getResponseCode();
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

    public static String getPostDataString(Pair<String, String>[] pairs){
        String data = "";
        if(pairs != null && pairs.length > 0){
            for(Pair<String, String> p : pairs){
                data += p.first + "=" + p.second + "&";
            }
        }
        return data;

    }
}
