package com.fivetrue.gimpo.ac05.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.manager.NaverApiManager;
import com.fivetrue.gimpo.ac05.parser.NaverUserInfoParser;
import com.fivetrue.gimpo.ac05.utils.Log;
import com.fivetrue.gimpo.ac05.vo.user.UserInfo;

/**
 * Created by kwonojin on 16. 6. 1..
 */
public class NaverLoginActivity extends BaseActivity{

    private static final String TAG = "OAuthSampleActivity";


    /** UI 요소들 */
    private Button mLoginButton = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {
        mLoginButton = (Button) findViewById(R.id.btn_login);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NaverApiManager.getInstance().startOauthLoginActivity(NaverLoginActivity.this, new NaverApiManager.OnOAuthLoginListener() {
                    @Override
                    public void onSuccess(NaverApiManager apiManager, String accessToken, String refreshToken, long expiresAt, String tokenType, String state) {


                    }

                    @Override
                    public void onError(NaverApiManager apiManager, String errorCode, String errorDescription) {

                    }
                });
            }
        });


        findViewById(R.id.btn_loginout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NaverApiManager.getInstance().logout();

            }
        });

        findViewById(R.id.btn_delete_token).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NaverApiManager.getInstance().requestDeleteAccessToken(new NaverApiManager.OnErrorCodeListener() {
                    @Override
                    public void onError(NaverApiManager apiManager, String errorCode, String errorDescription) {

                    }
                });
            }
        });

        findViewById(R.id.btn_refresh_token).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NaverApiManager.getInstance().requestRefreshAccessToken(new NaverApiManager.OnOAuthLoginListener() {
                    @Override
                    public void onSuccess(NaverApiManager apiManager, String accessToken, String refreshToken, long expiresAt, String tokenType, String state) {

                    }

                    @Override
                    public void onError(NaverApiManager apiManager, String errorCode, String errorDescription) {

                    }
                });
            }
        });

        findViewById(R.id.btn_request_profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NaverApiManager.getInstance().reqeustUserProfile(new NaverApiManager.OnRequestResponseListener() {
                    @Override
                    public void onResponse(String response) {

//                        try{
//                            XmlPullParser parser =
//
//                            while(parser.getEventType() != XmlPullParser.END_DOCUMENT) {
//                                if(parser.getEventType() == XmlPullParser.START_TAG) {
//                                    if(parser.getName().equals("content")) {
//                                        items.add(parser.getAttributeValue(0));
//                                    }
//                                }
//                                parser.next();
//                            }
//                        }catch(Throwable t) {
//                        }
                        Log.i(TAG, response);
                        UserInfo info = NaverUserInfoParser.parse(response);
                        if(info != null){
                            Log.i(TAG, info.toString());
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
