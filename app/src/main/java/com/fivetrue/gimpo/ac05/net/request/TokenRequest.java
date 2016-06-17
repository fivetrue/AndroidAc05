package com.fivetrue.gimpo.ac05.net.request;

import android.content.Context;

import com.fivetrue.gimpo.ac05.net.BaseApiRequest;
import com.fivetrue.gimpo.ac05.net.BaseApiResponse;
import com.fivetrue.gimpo.ac05.vo.config.AppConfig;
import com.fivetrue.gimpo.ac05.vo.config.AuthLoginResult;
import com.fivetrue.gimpo.ac05.vo.config.Token;

/**
 * Created by kwonojin on 16. 6. 13..
 */
public class TokenRequest extends BaseApiRequest{

    private static final String API = "https://nid.naver.com/oauth2.0/token";

    public TokenRequest(Context context, BaseApiResponse response) {
        super(context, API, response);
    }

    public void requestAccessToken(AppConfig config
            , AuthLoginResult authLoginResult){
        getParams().put("grant_type", "authorization_code");
        getParams().put("client_id", config.getNaverClientId());
        getParams().put("client_secret", config.getNaverClientSecret());
        getParams().put("code", authLoginResult.getCode());
        getParams().put("state", authLoginResult.getState());
    }

    public void requestRefreshToken(AppConfig config, Token token){
        getParams().put("grant_type", "refresh_token");
        getParams().put("client_id", config.getNaverClientId());
        getParams().put("client_secret", config.getNaverClientSecret());
        getParams().put("refresh_token", token.getRefresh_token());
    }

    public void requestDeleteToken(AppConfig config, Token token){
        getParams().put("grant_type", "delete");
        getParams().put("client_id", config.getNaverClientId());
        getParams().put("client_secret", config.getNaverClientSecret());
        getParams().put("access_token", token.getAccess_token());
        getParams().put("service_provider", "NAVER");
    }


}
