package com.fivetrue.gimpo.ac05.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import com.fivetrue.fivetrueandroid.google.FirebaseStorageManager;
import com.fivetrue.fivetrueandroid.ui.BaseActivity;
import com.fivetrue.fivetrueandroid.ui.diaglog.LoadingDialog;
import com.fivetrue.fivetrueandroid.ui.fragment.BaseDialogFragment;
import com.fivetrue.fivetrueandroid.utils.CustomWebViewClient;
import com.fivetrue.gimpo.ac05.Constants;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.database.ScrapLocalDB;
import com.fivetrue.gimpo.ac05.firebase.database.ScrapContentDatabase;
import com.fivetrue.gimpo.ac05.firebase.database.UserInfoDatabase;
import com.fivetrue.gimpo.ac05.firebase.model.AppConfig;
import com.fivetrue.gimpo.ac05.firebase.model.ScrapContent;
import com.fivetrue.gimpo.ac05.firebase.model.User;
import com.fivetrue.gimpo.ac05.preferences.ConfigPreferenceManager;
import com.fivetrue.gimpo.ac05.ui.fragment.InputDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by kwonojin on 16. 6. 7..
 */
public class CafeActivity extends BaseActivity implements CustomWebViewClient.JSInterface{

    private static final String TAG = "CafeActivity";

    private WebView mWebView = null;

    private ConfigPreferenceManager mConfigPref = null;

    private View mCaptureContentView;
    private View mCloseCaptureContentView;

    private LoadingDialog mLoadingDialog;

    private CustomWebViewClient mCustomWebViewClient = null;
    private CafeJSInterface mCafeInterface;

    private FirebaseStorageManager mFirebaseStorageManager;
    private ScrapContentDatabase mScrapContentDatabase;

    private ScrapLocalDB mScrapLocalDB;

    private Pattern mPattern = Pattern.compile("src\\s*=\\s*([\"'])?([^ \"']*)");

    private String mHtmlTemplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cafe);
        initData();
        initView();
    }

    private void initData(){
        mConfigPref = new ConfigPreferenceManager(this);
        mScrapLocalDB = new ScrapLocalDB(this);

        mFirebaseStorageManager = new FirebaseStorageManager();
        mScrapContentDatabase = new ScrapContentDatabase();
        mCafeInterface = new CafeJSInterface();
        BufferedReader r = null;
        try {
            r = new BufferedReader(new InputStreamReader(getAssets().open("templete/cafe_templete.html")));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line).append('\n');
            }
            mHtmlTemplete = total.toString();
        } catch (IOException e) {
            Log.w(TAG, "initData: html templete open error", e);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        mCustomWebViewClient.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mCustomWebViewClient.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCustomWebViewClient.onDestroy();
        mWebView = null;
    }

    private void initView(){
        mLoadingDialog = new LoadingDialog(this);
        mWebView = (WebView) findViewById(R.id.webview_cafe);

        mCaptureContentView = findViewById(R.id.layout_cafe_capture);
        mCloseCaptureContentView = findViewById(R.id.iv_cafe_capture_close);

        mCustomWebViewClient = new CustomWebViewClient(this, mWebView, this);
        mWebView.loadUrl(mConfigPref.getAppConfig().clubUrl);

        mCaptureContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCafeInterface.canCapture()){
                    mScrapContentDatabase.isExistCapturedPage(mCustomWebViewClient.getCurrentUrl()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.getValue() == null){
                                Bundle b = new Bundle();
                                b.putString("text", mCafeInterface.pageTitleText);
                                final InputDialogFragment dialog = new InputDialogFragment();
                                dialog.show(getCurrentFragmentManager(), getString(R.string.scrap_data_name)
                                        , getString(android.R.string.ok)
                                        , getString(android.R.string.cancel),
                                        b, new BaseDialogFragment.OnClickDialogFragmentListener() {
                                            @Override
                                            public void onClickOKButton(final BaseDialogFragment f, final Object data) {
                                                mLoadingDialog.show();

                                                mFirebaseStorageManager.uploadHtmlText("web/scrap/"
                                                        , mConfigPref.getUserInfo().uid + System.currentTimeMillis()
                                                        , mCafeInterface.makeHtml(mConfigPref.getUserInfo(), mCustomWebViewClient.getCurrentUrl())
                                                        , new FirebaseStorageManager.OnUploadResultListener() {
                                                            @Override
                                                            public void onUploadSuccess(Uri url, String path) {
                                                                String imageUrl = null;
                                                                Matcher m = mPattern.matcher(mCafeInterface.pageContentHtml);
                                                                if (m.find()) {
                                                                    imageUrl = m.group(2);
                                                                }else{
                                                                    imageUrl = Constants.DEFAULT_DEFAULT_IMAGE_URL;
                                                                }

                                                                final ScrapContent page = new ScrapContent(null, (String) data
                                                                        , url.toString()
                                                                        , path
                                                                        , imageUrl
                                                                        , mCustomWebViewClient.getCurrentUrl()
                                                                        , mConfigPref.getUserInfo());
                                                                mScrapContentDatabase.pushData(page);
                                                                f.dismiss();
                                                                mCaptureContentView.setVisibility(View.INVISIBLE);
                                                                mLoadingDialog.dismiss();
                                                            }

                                                            @Override
                                                            public void onUploadFailed(Exception e) {
                                                                mLoadingDialog.dismiss();
                                                            }
                                                        });

                                            }

                                            @Override
                                            public void onClickCancelButton(BaseDialogFragment f, Object data) {
                                                dialog.dismiss();
                                            }
                                        });
                            }else{
                                Toast.makeText(CafeActivity.this, R.string.already_scrap_content, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

        mCloseCaptureContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCaptureContentView.setVisibility(View.INVISIBLE);
            }
        });
    }

    private synchronized void updateScrapContentView(){
        runOnUiThread(new Runnable(){

            @Override
            public void run() {
                if(mCafeInterface.canCapture()){
                    User user = mConfigPref.getUserInfo();
                    AppConfig appConfig = mConfigPref.getAppConfig();
                    String currentUrl = mWebView.getUrl();
                    if(!user.isAuthUser
                            && currentUrl.contains(String.valueOf(appConfig.clubId))){
                        user.isAuthUser = true;
                        mConfigPref.setUserInfo(user);
                        new UserInfoDatabase(mConfigPref.getUserInfo().uid).getReference().setValue(user.getValues());
                    }

                    if(user.isAuthUser){
                        if(!mScrapLocalDB.existsScrapContent(currentUrl)){
                            mCaptureContentView.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCustomWebViewClient.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if(mWebView.canGoBack()){
            mWebView.goBack();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public String getInterfaceName() {
        return Constants.JS_INTERFACE_NAME;
    }

    @Override
    public Object getInterface() {
        return mCafeInterface;
    }

    @Override
    public void onLoadedPage(WebView webView, String url) {
        webView.loadUrl("javascript:window."+ Constants.JS_INTERFACE_NAME + ".onLoadPostTitleText(document.getElementsByClassName('tit')[0].innerText);");
        webView.loadUrl("javascript:window."+ Constants.JS_INTERFACE_NAME + ".onLoadPostContentHtml(document.getElementById('postContent').innerHTML);");
    }

    @Override
    public void onStartPage(WebView webView, String url) {
        mCafeInterface.initData();
        mCaptureContentView.setVisibility(View.INVISIBLE);
    }

    public class CafeJSInterface{

        private String pageContentHtml;
        private String pageTitleText;

        @android.webkit.JavascriptInterface
        public void onLoadPostContentHtml(String postContentHtml){
            if(postContentHtml != null){
                postContentHtml = postContentHtml.replaceAll("\n", "");
                postContentHtml = postContentHtml.replaceAll("\t", "");
                postContentHtml = postContentHtml.trim();
                StringBuilder remakeBuilder = new StringBuilder();
                String[] contentBlock = postContentHtml.split("</p>");
                String imageClass = "<img class=\"";
                for(String b : contentBlock){
                    String str = b;
                    int startIndex = str.indexOf(imageClass);
                    int endIndex = str.indexOf("\"", startIndex + imageClass.length() + 1);
                    if(startIndex > 0 && endIndex > 0){
                        String sub = str.substring(startIndex, endIndex);
                        String replaceStart = str.substring(0, startIndex);
                        String replaceEnd = str.substring(endIndex);

                        String imageUrl = null;
                        Matcher m = mPattern.matcher(str);
                        if (m.find()) {
                            imageUrl = m.group(2);
                        }

                        str = replaceStart + "<img onClick=\"onClickImage('" + imageUrl +
                                "')\" class=\"" + "img-thumbnail" + replaceEnd;
                    }
                    remakeBuilder.append(str).append("</p>");
                }
                pageContentHtml = remakeBuilder.toString();
                updateScrapContentView();
            }
        }

        @android.webkit.JavascriptInterface
        public void onLoadPostTitleText(String title){
            pageTitleText = title;
            updateScrapContentView();
        }

        public boolean canCapture(){
            return !TextUtils.isEmpty(pageContentHtml) && !TextUtils.isEmpty(pageTitleText);
        }

        public void initData(){
            pageContentHtml = null;
            pageTitleText = null;
        }

        public String makeHtml(User user, String url){
            //Title, onClick JS Interface, Title, Link, User Image, User DisplayName,   Content
            return String.format(mHtmlTemplete ,pageTitleText, pageTitleText, url
                    , user.profileImage
                    , user.district + "동 " + user.getDisplayName()
                    , pageContentHtml);
        }
    }
}
