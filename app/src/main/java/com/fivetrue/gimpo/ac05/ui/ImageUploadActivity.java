package com.fivetrue.gimpo.ac05.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.fivetrue.fivetrueandroid.google.FirebaseStorageManager;
import com.fivetrue.fivetrueandroid.ui.BaseActivity;
import com.fivetrue.fivetrueandroid.ui.diaglog.LoadingDialog;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.chatting.GalleryMessage;
import com.fivetrue.gimpo.ac05.firebase.database.GalleryDatabase;
import com.fivetrue.gimpo.ac05.preferences.ConfigPreferenceManager;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileNotFoundException;

/**
 * Created by kwonojin on 2016. 10. 11..
 */

public class ImageUploadActivity extends BaseActivity {

    private static final String TAG = "ImageUploadActivity";

    private static final String IMAGE_UPLOAD_PATH = "/images/ac05/gallery/";
    private ImageView mImageMain;
    private TextInputEditText mInputMessage;
    private Bitmap mPendingBitmap;

    private ConfigPreferenceManager mConfigPref;
    private FirebaseStorageManager mFirebaseStorageManager;

    private LoadingDialog mLoadingDialog;

    private InputMethodManager mImm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);
        initData();
        initView();
    }

    private void initData(){
        Uri uri = getIntent().getParcelableExtra("uri");
        try {
            mPendingBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            Log.w(TAG, "initData: ", e);
            finish();
        }
        mConfigPref = new ConfigPreferenceManager(this);
        mFirebaseStorageManager = new FirebaseStorageManager();
        mImm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
    }

    private void initView(){

        mLoadingDialog = new LoadingDialog(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.upload_image);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mImageMain = (ImageView) findViewById(R.id.iv_image_upload);
        mInputMessage = (TextInputEditText) findViewById(R.id.et_image_upload_message);
        mImageMain.setImageBitmap(mPendingBitmap);

    }

    @Override
    protected int getOptionsMenuResource() {
        return R.menu.menu_upload;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_upload :
                mImm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                if(mPendingBitmap != null && !mPendingBitmap.isRecycled()){
                    String message = mInputMessage.getText().toString();
                    if(TextUtils.isEmpty(message)){
                        mInputMessage.setError(getString(R.string.error_input_message)
                                , getResources().getDrawable(R.drawable.ic_notification_20dp));
                        return true;
                    }
                    mLoadingDialog.show();
                    String name = mConfigPref.getUserInfo().getDisplayName() + System.currentTimeMillis();
                    mFirebaseStorageManager.uploadBitmapImage(IMAGE_UPLOAD_PATH, name, mPendingBitmap, new FirebaseStorageManager.OnUploadResultListener() {
                        @Override
                        public void onUploadSuccess(Uri url, String path) {
                            GalleryMessage msg = new GalleryMessage(null, url.toString()
                                    , mInputMessage.getText().toString().trim(), path
                                    , mConfigPref.getUserInfo());
                            new GalleryDatabase().pushData(msg);
                            mLoadingDialog.dismiss();
                            Intent intent = new Intent();
                            intent.putExtra(GalleryMessage.class.getName(), msg);
                            setResult(RESULT_OK, intent);
                            finish();
                        }

                        @Override
                        public void onUploadFailed(Exception e) {
                            Log.w(TAG, "onUploadFailed: ", e);
                            mLoadingDialog.dismiss();
                            Toast.makeText(ImageUploadActivity.this, R.string.error_image_upload_failed, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
