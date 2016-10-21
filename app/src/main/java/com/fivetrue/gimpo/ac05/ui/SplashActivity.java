package com.fivetrue.gimpo.ac05.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fivetrue.fivetrueandroid.google.GoogleLoginUtil;
import com.fivetrue.fivetrueandroid.ui.BaseActivity;
import com.fivetrue.fivetrueandroid.utils.AppUtils;
import com.fivetrue.fivetrueandroid.utils.SimpleViewUtils;
import com.fivetrue.fivetrueandroid.view.CircleImageView;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.firebase.database.AppMessageDatabase;
import com.fivetrue.gimpo.ac05.firebase.model.AppMessage;
import com.fivetrue.gimpo.ac05.preferences.DefaultPreferenceManager;
import com.fivetrue.gimpo.ac05.service.FirebaseService;
import com.fivetrue.gimpo.ac05.firebase.database.AppConfigDatabase;
import com.fivetrue.gimpo.ac05.firebase.database.UserInfoDatabase;
import com.fivetrue.gimpo.ac05.firebase.model.AppConfig;
import com.fivetrue.gimpo.ac05.preferences.ConfigPreferenceManager;
import com.fivetrue.gimpo.ac05.service.notification.NotificationHelper;
import com.fivetrue.gimpo.ac05.firebase.model.User;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

/**
 * 1. Login check
 * 2. get Messages from server
 * 3. Login to Naver
 * 4. getNaver Userinfo
 * 5. register UserInfo to Server
 * 6. start application
 */
public class SplashActivity extends BaseActivity implements GoogleLoginUtil.OnAccountManagerListener {

    private static final String TAG = "SplashActivity";

    private TextView mMainMessage = null;
    private TextView mLoadingMessage = null;

    private ProgressBar mProgress = null;
    private LinearLayout mUserLayout = null;

    private CircleImageView mUserImage = null;
    private TextView mUserNickname = null;

    private Button mLoginGoogle = null;

    private ConfigPreferenceManager mConfigPref = null;
    private GoogleLoginUtil mGoogleLoginUtil = null;

    private Typeface mTyeFace = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initData();
        initView();
        checkLoginStatus(mGoogleLoginUtil.getUser());
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleLoginUtil.onStart();
    }


    @Override
    protected void onStop() {
        super.onStop();
        mGoogleLoginUtil.onStop();
    }

    private void initView() {

        mMainMessage = (TextView) findViewById(R.id.tv_splash_main);
        mLoadingMessage = (TextView) findViewById(R.id.tv_splash_loading);

        mUserLayout = (LinearLayout) findViewById(R.id.layout_splash_user_info);
        mUserNickname = (TextView) findViewById(R.id.tv_splash_user);
        mUserImage = (CircleImageView) findViewById(R.id.iv_splash_user);

        mProgress = (ProgressBar) findViewById(R.id.pb_splash);
        mProgress.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorAccent)
                , android.graphics.PorterDuff.Mode.MULTIPLY);

        mLoginGoogle = (Button) findViewById(R.id.btn_splash_login_google);

        mLoginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadingMessage.setVisibility(View.VISIBLE);
                mLoadingMessage.setText(R.string.config_user_info_verify);
                mGoogleLoginUtil.loginGoogleAccount();
                showProgress();
            }
        });

        new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                mTyeFace = Typeface.createFromAsset(getAssets(), "Typo_PapyrusM.ttf");
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                mMainMessage.setTypeface(mTyeFace);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void initData() {
        mConfigPref = new ConfigPreferenceManager(this);
        mGoogleLoginUtil = new GoogleLoginUtil(SplashActivity.this, getString(R.string.firebase_auth_client_id));
    }

    private void checkLoginStatus(final FirebaseUser firebaseUser) {
        if (firebaseUser != null) {
            getAppMessage();
        } else {
            mLoadingMessage.setVisibility(View.GONE);
            mProgress.setVisibility(View.GONE);
            mLoginGoogle.setVisibility(View.VISIBLE);
        }
    }


    private void getAppMessage(){
        Log.i(TAG, "check regarding application message");
        mLoadingMessage.setText(R.string.config_data_check);
        new AppMessageDatabase().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    AppMessage message = dataSnapshot.getValue(AppMessage.class);
                    mConfigPref.setAppMessage(message);
                    getApplicationConfig();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                getApplicationConfig();
            }
        });
    }

    private void getApplicationConfig() {
        Log.i(TAG, "getApplicationConfig : start");
        new AppConfigDatabase().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange() called with: dataSnapshot = [" + dataSnapshot + "]");
                mLoadingMessage.setText(R.string.config_check_app_version);
                AppConfig appConfig = dataSnapshot.getValue(AppConfig.class);
                mConfigPref.setAppConfig(appConfig);
                checkApplication();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                getApplicationConfig();
            }
        });
    }

    private void checkApplication(){
        final AppConfig appConfig = mConfigPref.getAppConfig();
        int recentlyVersion = appConfig.appVersionCode;
        int appVersion = AppUtils.getApplicationVersionCode(SplashActivity.this);
        if (recentlyVersion > appVersion && appConfig.forceUpdate) {
            new AlertDialog.Builder(SplashActivity.this, R.style.Base_Theme_AppCompat_Light_Dialog_Alert)
                    .setTitle(R.string.notice)
                    .setMessage(R.string.config_force_update)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            AppUtils.goAppStore(SplashActivity.this);
                        }
                    }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    AppUtils.goAppStore(SplashActivity.this);

                }
            }).create().show();
        }else{
            AppMessage appMessage = mConfigPref.getAppMessage();
            if(!DefaultPreferenceManager.getInstance(this).isReadWelcome()){
                View view = LayoutInflater.from(this).inflate(R.layout.layout_simple_text_view, null);
                Spanned s = Html.fromHtml(appMessage.welcome);
                TextView message = (TextView) view.findViewById(R.id.message);
                message.setText(s);
                message.setMovementMethod(LinkMovementMethod.getInstance());
                new AlertDialog.Builder(SplashActivity.this, R.style.Base_Theme_AppCompat_Light_Dialog_Alert)
                        .setTitle(R.string.notice)
                        .setCancelable(false)
                        .setView(view)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                DefaultPreferenceManager.getInstance(SplashActivity.this).setReadWelcome(true);
                                getGcmId(appConfig);
                            }
                        }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                }).show();
            }else{
                getGcmId(appConfig);
            }
        }
    }

    /**
     * 2. do register device after getting appconfig
     *
     * @param appConfig
     */
    private void getGcmId(final AppConfig appConfig) {
        mLoadingMessage.setText(R.string.config_data_register);
        if (appConfig != null) {
            new AsyncTask<Long, Void, String>() {
                @Override
                protected String doInBackground(Long... params) {
                    String regId = null;
                    if (params != null && params.length > 0) {
                        Long senderId = params[0];
                        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                        try {
                            regId = gcm.register(String.valueOf(senderId));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    return regId;
                }

                @Override
                protected void onPostExecute(String value) {
                    super.onPostExecute(value);
                    if (value != null) {
                        mConfigPref.setGcmDeviceId(value);
                    } else {
                        Log.e(TAG, "registerDevice Gcm register error");
                    }
                    if (getIntent().getAction().equals(NotificationHelper.ACTION_NOTIFICATION)) {
                        startMainActivity();
                    }else{
                        prepareUserInfo();
                    }
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, appConfig.senderId);
        }
    }

    private void prepareUserInfo(){
        mLoadingMessage.setVisibility(View.VISIBLE);
        showProgress();
        mLoadingMessage.setText(R.string.config_user_info_register);
        final UserInfoDatabase userDatabase = new UserInfoDatabase(mGoogleLoginUtil.getUser().getUid());
        userDatabase.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange() called with: dataSnapshot = [" + dataSnapshot + "]");
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    userDatabase.updateTime();
                    mConfigPref.setUserInfo(user);
                } else {
                    user = new User(mGoogleLoginUtil.getUser(), mConfigPref.getGcmDeviceId(), Build.MODEL, 0);
                    mConfigPref.setUserInfo(user);
                    dataSnapshot.getRef().setValue(user.getValues());
                }
                startApplication(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void finishError() {
        Toast.makeText(SplashActivity.this, R.string.config_data_can_not_read, Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 500L);
    }

    private void startApplication(final User userInfo) {
        Log.i(TAG, "startApplication: start");
        if (getIntent().getAction().equals(NotificationHelper.ACTION_NOTIFICATION)) {
            startMainActivity();
        }else{

        }

        SimpleViewUtils.hideView(mLoadingMessage, View.GONE);
        SimpleViewUtils.hideView(mProgress, View.GONE);
        if (userInfo != null) {
            mUserNickname.setText(userInfo.name);
            mUserImage.setImageUrl(userInfo.profileImage);

            SimpleViewUtils.showView(mUserLayout, View.VISIBLE, new SimpleViewUtils.SimpleAnimationStatusListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onEnd() {
                    mUserLayout.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startMainActivity();
                        }
                    }, 700L);
                }
            });
            mUserLayout.setVisibility(View.VISIBLE);
        }
    }

    private void showProgress() {
        SimpleViewUtils.hideView(mLoginGoogle, View.GONE, new SimpleViewUtils.SimpleAnimationStatusListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onEnd() {
                SimpleViewUtils.showView(mProgress, View.VISIBLE);
            }
        });
    }

    private void showLoginButton() {
        SimpleViewUtils.hideView(mProgress, View.GONE, new SimpleViewUtils.SimpleAnimationStatusListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onEnd() {
                SimpleViewUtils.showView(mLoginGoogle, View.VISIBLE);
            }
        });
    }

    private void startMainActivity() {
        Log.i(TAG, "startMainActivity: start");
        if(TextUtils.isEmpty(mConfigPref.getUserInfo().nickName)
                || mConfigPref.getUserInfo().district == 0){
            Intent intent = new Intent(this, UserInfoInputActivity.class);
            startActivityWithClipRevealAnimation(intent, mMainMessage);
        }else{
            Intent intent = null;
            if (getIntent() != null
                    && getIntent().getAction() != null
                    && getIntent().getAction().equals(NotificationHelper.ACTION_NOTIFICATION)) {
                intent = new Intent(this, MainActivity.class);
                intent.setData(getIntent().getData());
                intent.putExtra(NotificationHelper.KEY_NOTIFICATION_PARCELABLE, getIntent().getParcelableExtra(NotificationHelper.KEY_NOTIFICATION_PARCELABLE));

            } else {
                intent = new Intent(this, MainActivity.class);
            }
            startActivityWithClipRevealAnimation(intent, mMainMessage);
        }
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mGoogleLoginUtil.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onUserAddSuccess(final FirebaseUser firebaseUser) {
        getAppMessage();
    }

    @Override
    public void onUserAddError(Exception message) {
        showLoginButton();
    }

    @Override
    public void onUpdateUserInfo(FirebaseUser user) {

    }
}
