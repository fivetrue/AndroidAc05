package com.fivetrue.gimpo.ac05.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fivetrue.fivetrueandroid.google.FirebaseStorageManager;
import com.fivetrue.fivetrueandroid.ui.BaseActivity;
import com.fivetrue.fivetrueandroid.ui.diaglog.LoadingDialog;
import com.fivetrue.fivetrueandroid.ui.fragment.BaseDialogFragment;
import com.fivetrue.fivetrueandroid.utils.CustomWebViewClient;
import com.fivetrue.gimpo.ac05.Constants;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.firebase.database.MessageBoxDatabase;
import com.fivetrue.gimpo.ac05.firebase.database.ScrapContentDatabase;
import com.fivetrue.gimpo.ac05.firebase.model.ChatMessage;
import com.fivetrue.gimpo.ac05.firebase.model.NotifyMessage;
import com.fivetrue.gimpo.ac05.firebase.model.ScrapContent;
import com.fivetrue.gimpo.ac05.firebase.model.User;
import com.fivetrue.gimpo.ac05.preferences.ConfigPreferenceManager;
import com.fivetrue.gimpo.ac05.ui.adapter.ChatListAdapter;
import com.fivetrue.gimpo.ac05.ui.fragment.UserInfoDialogFragment;
import com.fivetrue.gimpo.ac05.utils.DeeplinkUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kwonojin on 16. 6. 10..
 */
public class ScrapContentActivity extends BaseActivity implements CustomWebViewClient.JSInterface{

    private static final String TAG = "ScrapContentActivity";

    private static final int REQUEST_CODE_CHOOSE_IMAGE = 0x30;
    private static final String COMMENTS_STORAGE_PATH = "/images/comments/";

    private SlidingUpPanelLayout mSlidingUpPanelLayout;

    private WebView mWebView = null;
    private CustomWebViewClient mCustomWebViewClient = null;

    private TextView mScrapTitle;
    private TextView mScrapCommentCountText;
    private ImageView mScrapNewComment;

    private ImageButton mImageAdd;
    private ImageButton mCommentSend;
    private EditText mCommentInput;

    private RecyclerView mScrapCommentList;
    private ChatListAdapter mAdapter;

    private ScrapContent mScrapContent;
    private ConfigPreferenceManager mConfigPref;

    private ScrapContentDatabase mScrapContentDatabase;
    private FirebaseStorageManager mFirebaseStorageManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrap_content);
        initData();
        initView();

        mScrapContentDatabase.getCommentReference(mScrapContent.key).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot != null && dataSnapshot.getValue() != null){
                    ChatMessage message = dataSnapshot.getValue(ChatMessage.class);
                    mAdapter.getData().add(message);
                    mAdapter.notifyItemInserted(mAdapter.getCount());
                    mScrapCommentCountText.setText(String.valueOf(mAdapter.getCount()));
                    if(message.updateTime + (60000 * 60 * 6) > System.currentTimeMillis()){
                        mScrapNewComment.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

    protected void initData(){
        mScrapContent = getIntent().getParcelableExtra(ScrapContent.class.getName());
        mFirebaseStorageManager = new FirebaseStorageManager();
        mScrapContentDatabase = new ScrapContentDatabase();
        mConfigPref = new ConfigPreferenceManager(this);
        mAdapter = new ChatListAdapter(new ArrayList<ChatMessage>(), mConfigPref.getUserInfo().uid);
    }

    private void initView(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mWebView = (WebView) findViewById(R.id.webview);
        mSlidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.layout_sliding_up_panel);

        mScrapTitle = (TextView) findViewById(R.id.tv_scrap_content_comment_title);
        mScrapCommentCountText = (TextView) findViewById(R.id.tv_scrap_content_comment_count);
        mScrapNewComment = (ImageView) findViewById(R.id.iv_scrap_content_comment_new);

        mImageAdd = (ImageButton) findViewById(R.id.iv_scrip_comment_add);
        mCommentInput = (EditText) findViewById(R.id.et_scrap_comment_input);
        mCommentSend = (ImageButton) findViewById(R.id.btn_scrap_comment_send);

        mScrapCommentList = (RecyclerView) findViewById(R.id.rv_scrap_comment_list);
        mScrapCommentList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mScrapCommentList.setAdapter(mAdapter);

        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        mCustomWebViewClient  = new CustomWebViewClient(this, mWebView, this);


        mWebView.loadUrl(mScrapContent.pageUrl);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(mScrapContent.getTitle());
        mScrapTitle.setText(mScrapContent.getTitle());
        mScrapTitle.setAlpha(0);

        mSlidingUpPanelLayout.addPanelSlideListener(new PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                mScrapTitle.setAlpha(slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, PanelState previousState, PanelState newState) {
                if(newState == PanelState.EXPANDED){
                    if(mScrapCommentList != null && mAdapter != null){
                        mScrapCommentList.scrollToPosition(mAdapter.getCount());
                    }
                }

            }
        });

        mSlidingUpPanelLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlidingUpPanelLayout.setPanelState(PanelState.COLLAPSED);
            }
        });

        mImageAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseStorageManager.chooseDeviceImage(ScrapContentActivity.this, REQUEST_CODE_CHOOSE_IMAGE);
            }
        });

        mCommentSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputText = mCommentInput.getText().toString().trim();
                if(!TextUtils.isEmpty(inputText)){
                    mCommentInput.setText("");
                    final ChatMessage message = new ChatMessage(null, inputText, null, mConfigPref.getUserInfo());
                    sendComment(message);
                }
            }
        });
    }

    private void sendComment(ChatMessage chatMessage){
        if(chatMessage != null){
            final LoadingDialog dialog = new LoadingDialog(ScrapContentActivity.this);
            dialog.show();
            mScrapContentDatabase.getCommentReference(mScrapContent.key).push().setValue(chatMessage.getValues()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    dialog.dismiss();

                    if(mAdapter != null){
                        ArrayList<User> targetUsers = new ArrayList<User>();
                        for(ChatMessage chatMessage : mAdapter.getData()){
                            if(!targetUsers.contains(chatMessage.user)){
                                targetUsers.add(chatMessage.user);
                            }
                        }

                        for(User user : targetUsers){
                            String message = getString(R.string.added_new_comment_to_scrap, mScrapContent.title);
                            String deeplink = DeeplinkUtil.makeLink(DeeplinkUtil.Host.Scrap, "key=" + mScrapContent.key);
                            String title = getString(R.string.new_alarm);
                            NotifyMessage notify = new NotifyMessage(title, message
                                    , deeplink, mScrapContent.imageUrl, mConfigPref.getUserInfo());
                            new MessageBoxDatabase(user.uid).getNotifyReference().push().setValue(notify.getValues());
                        }
                    }
                    Log.d(TAG, "onSuccess() called with: aVoid = [" + aVoid + "]");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        mCustomWebViewClient.onActivityResult(requestCode, resultCode, intent);
        if(requestCode == REQUEST_CODE_CHOOSE_IMAGE){
            if(resultCode == RESULT_OK){
                FirebaseStorageManager.cropChooseDeviceImage(this, intent.getData());
            }
        }else if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                CropImage.ActivityResult result = CropImage.getActivityResult(intent);
                Uri croppedUri =  result.getUri();
                if(croppedUri != null){
                    try {
                        final Bitmap bm = BitmapFactory.decodeStream(getContentResolver().openInputStream(croppedUri));
                        if(bm != null && !bm.isRecycled()){
                            final LoadingDialog dialog = new LoadingDialog(this);
                            dialog.show();
                            String fileName = mConfigPref.getUserInfo().uid + "_" + System.currentTimeMillis() + ".png";
                            mFirebaseStorageManager.uploadBitmapImage(COMMENTS_STORAGE_PATH, fileName, bm, new FirebaseStorageManager.OnUploadResultListener(){

                                @Override
                                public void onUploadSuccess(Uri url, String path) {
                                    Log.d(TAG, "onUploadSuccess: url = " + url);
                                    Log.d(TAG, "onUploadSuccess: path = " + path);
                                    bm.recycle();
                                    dialog.dismiss();
                                    ChatMessage message = new ChatMessage(null, getString(R.string.image_infomation), url.toString(), mConfigPref.getUserInfo());
                                    sendComment(message);
                                }

                                @Override
                                public void onUploadFailed(Exception e) {
                                    Toast.makeText(ScrapContentActivity.this, R.string.error_image_upload_failed, Toast.LENGTH_SHORT).show();
                                    bm.recycle();
                                    dialog.dismiss();
                                }
                            });

                        }
                    } catch (FileNotFoundException e) {
                        Log.w(TAG, "onActivityResult: image decoded error", e);
                    }
                }
            }
        }
    }

    @Override
    protected int getOptionsMenuResource() {
        return R.menu.menu_captured_page;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(!mConfigPref.getUserInfo().uid.equals(mScrapContent.user.uid)){
            menu.removeItem(R.id.action_delete);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home :
                finish();
                return true;
            case R.id.action_delete :{
                if(mScrapContent != null){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Base_Theme_AppCompat_Light_Dialog_Alert);
                    builder.setTitle(R.string.delete)
                            .setMessage(R.string.delete_captured_data)
                            .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, int which) {
                            final LoadingDialog loading = new LoadingDialog(ScrapContentActivity.this);
                            loading.show();
                            mFirebaseStorageManager.deleteFileFromStorage(mScrapContent.pagePath)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mScrapContentDatabase.getReference().child(mScrapContent.key).setValue(null);
                                            loading.dismiss();
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "onFailure: deleting captured data ", e);
                                    loading.dismiss();
                                }
                            });
                            dialog.dismiss();
                        }
                    }).show();
                }
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (mSlidingUpPanelLayout != null &&
                (mSlidingUpPanelLayout.getPanelState() == PanelState.EXPANDED
                        || mSlidingUpPanelLayout.getPanelState() == PanelState.ANCHORED)) {
            mSlidingUpPanelLayout.setPanelState(PanelState.COLLAPSED);
            return;
        }else if(mWebView.canGoBack()){
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
        return new JSInterface();
    }

    @Override
    public void onLoadedPage(WebView webView, String url) {
//        webView.loadUrl("javascript:window."+ Constants.JS_INTERFACE_NAME + ".onLoadHtml(document.getElementsByTagName('html')[0].innerHTML);");
    }

    @Override
    public void onStartPage(WebView webView, String url) {
    }

    public class JSInterface{

        @JavascriptInterface
        public void onClickInfo(){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mScrapContent != null){
                        Bundle b = new Bundle();
                        b.putParcelable(User.class.getName(), mScrapContent.getUser());
                        UserInfoDialogFragment dialog = new UserInfoDialogFragment();
                        dialog.show(getCurrentFragmentManager(), null
                                , getString(android.R.string.ok), null, b, new BaseDialogFragment.OnClickDialogFragmentListener() {
                                    @Override
                                    public void onClickOKButton(BaseDialogFragment f, Object data) {
                                        f.dismiss();
                                    }

                                    @Override
                                    public void onClickCancelButton(BaseDialogFragment f, Object data) {
                                        f.dismiss();
                                    }
                                });
                    }

                }
            });
        }

        @JavascriptInterface
        public void onClickImage(final String imageUrl){

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(ScrapContentActivity.this, ImageDetailActivity.class);
                    intent.putExtra("url", imageUrl);
                    startActivity(intent);
                }
            });
        }
    }
}
