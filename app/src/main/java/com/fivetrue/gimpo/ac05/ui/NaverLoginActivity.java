package com.fivetrue.gimpo.ac05.ui;


import android.content.Intent;
import android.os.Bundle;

import com.android.volley.VolleyError;
import com.fivetrue.gimpo.ac05.ApplicationEX;
import com.fivetrue.gimpo.ac05.Constants;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.net.BaseApiResponse;
import com.fivetrue.gimpo.ac05.net.NetworkManager;
import com.fivetrue.gimpo.ac05.net.request.TokenRequest;
import com.fivetrue.gimpo.ac05.net.response.TokenApiResponse;
import com.fivetrue.gimpo.ac05.preferences.ConfigPreferenceManager;
import com.fivetrue.gimpo.ac05.ui.fragment.BaseFragment;
import com.fivetrue.gimpo.ac05.ui.fragment.WebViewFragment;
import com.fivetrue.gimpo.ac05.utils.Log;
import com.fivetrue.gimpo.ac05.vo.config.AppConfig;
import com.fivetrue.gimpo.ac05.vo.config.AuthLoginResult;
import com.fivetrue.gimpo.ac05.vo.config.Token;
import com.google.gson.Gson;

import java.util.UUID;

public class NaverLoginActivity extends BaseActivity implements WebViewFragment.OnShouldOverrideUrlLoadingListener{

    private static final String TAG = "NaverLoginActivity";

    private static final String API = Constants.API_SERVER_HOST + "/api/naver/login?state=";

    private ConfigPreferenceManager mConfigPref = null;
    private UUID mUid = null;

    private TokenRequest mAccessTokenRequest = null;
    private TokenRequest mRefreshTokenRequest = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cafe);
        initData();
        initView();
    }

    private void initData(){
        mConfigPref = new ConfigPreferenceManager(this);
        mUid = UUID.randomUUID();
        mAccessTokenRequest = new TokenRequest(this, accessTokenApiResponse);
        mRefreshTokenRequest = new TokenRequest(this, refreshTokenResponse);
    }

    private void initView(){
        setActionbarVisible(false);
        checkCachedTokenForLogin();
    }

    private void checkCachedTokenForLogin(){
        Token token = mConfigPref.getToken();
        if(token == null){
            Log.i(TAG, "checkCachedTokenForLogin: showLoginWebView");
            showLoginWebView();
        }else{
            boolean isExpried = token.isExpired();
            AppConfig config = mConfigPref.getAppConfig();
            Log.i(TAG, "checkCachedTokenForLogin: isExpried = " + isExpried);
            if(isExpried){
                if(token.getRefresh_token() != null){
                    mRefreshTokenRequest.requestRefreshToken(config, token);
                    NetworkManager.getInstance().request(mRefreshTokenRequest);
                }else{
                    showLoginWebView();
                }
            }else{
                successLogin(token);
            }
        }
    }

    private void showLoginWebView(){
        Bundle b = new Bundle();
        b.putString("url", API + mUid.toString());
        b.putBoolean("hide", true);
        addFragment(WebViewFragment.class, b, false);
    }

    @Override
    public boolean onOverride(String url) {
        return false;
    }

    @Override
    public void onCallback(String response) {
        Log.i(TAG, "onCallback: " + response);
        if(response != null){
            AuthLoginResult result = new Gson().fromJson(response, AuthLoginResult.class);
            if(result != null){
                AppConfig config = mConfigPref.getAppConfig();
                if(config != null){
                    mAccessTokenRequest.requestAccessToken(config, result);
                    NetworkManager.getInstance().request(mAccessTokenRequest);
                }
            }
        }
    }

    private TokenApiResponse accessTokenApiResponse = new TokenApiResponse(new BaseApiResponse.OnResponseListener<Token>() {
        private int mRetry = 0;
        @Override
        public void onResponse(BaseApiResponse<Token> response) {
            Log.i(TAG, "onResponse: " + response);
            if(response != null && response.getData() != null){
                Token token = response.getData();
                token.setUpdateTime(System.currentTimeMillis());
                mConfigPref.setToken(token);
                successLogin(response.getData());
            }else{
                if(mRetry < BaseApiResponse.RETRY_COUNT){
                    NetworkManager.getInstance().request(mAccessTokenRequest);
                    mRetry ++;
                }else{
                    failedLogin();
                }
            }

        }

        @Override
        public void onError(VolleyError error) {
            Log.e(TAG, error.toString());
            if(mRetry < BaseApiResponse.RETRY_COUNT){
                NetworkManager.getInstance().request(mAccessTokenRequest);
                mRetry ++;
            }else{
                failedLogin();
            }
        }
    });

    private TokenApiResponse refreshTokenResponse = new TokenApiResponse(new BaseApiResponse.OnResponseListener<Token>() {
        private int mRetry = 0;
        @Override
        public void onResponse(BaseApiResponse<Token> response) {
            Log.i(TAG, "onResponse: " + response);
            if(response != null && response.getData() != null){
                Token token = response.getData();
                token.setUpdateTime(System.currentTimeMillis());
                mConfigPref.setToken(token);
                successLogin(response.getData());
            }else{
                if(mRetry < BaseApiResponse.RETRY_COUNT){
                    NetworkManager.getInstance().request(mRefreshTokenRequest);
                    mRetry ++;
                }else{
                    failedLogin();
                }
            }

        }

        @Override
        public void onError(VolleyError error) {
            Log.e(TAG, error.toString());
            if(mRetry < BaseApiResponse.RETRY_COUNT){
                NetworkManager.getInstance().request(mRefreshTokenRequest);
                mRetry ++;
            }else{
                failedLogin();
            }
        }
    });

    private void successLogin(Token token){
        if(token != null){
            Log.i(TAG, "successLogin: " + token.toString());
            Intent intent = new Intent();
            intent.putExtra(Token.class.getName(), token);
            setResult(RESULT_OK, intent);
        }else{
            setResult(-1);
        }
        finish();
    }

    private void failedLogin(){
        Log.i(TAG, "failedLogin: ");
        setResult(-1);
        finish();
    }

}
