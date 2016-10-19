package com.fivetrue.gimpo.ac05.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.fivetrue.fivetrueandroid.google.GoogleStorageManager;
import com.fivetrue.fivetrueandroid.image.ImageLoadManager;
import com.fivetrue.fivetrueandroid.ui.BaseActivity;
import com.fivetrue.fivetrueandroid.ui.adapter.BaseRecyclerAdapter;
import com.fivetrue.fivetrueandroid.utils.SimpleViewUtils;
import com.fivetrue.gimpo.ac05.Constants;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.chatting.GalleryMessage;
import com.fivetrue.gimpo.ac05.chatting.GalleryMessageDatabase;
import com.fivetrue.gimpo.ac05.preferences.ConfigPreferenceManager;
import com.fivetrue.gimpo.ac05.ui.adapter.GalleryListAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.theartofdev.edmodo.cropper.CropImage;

/**
 * Created by kwonojin on 2016. 10. 11..
 */

public class GalleryActivity extends BaseActivity {

    private static final String TAG = "GalleryActivity";

    private static final int REQUEST_IMAGE_PICKER = 0x22;
    private static final int REQUEST_IMAGE_UPLOAD = 0x33;


    private ImageView mGalleryMain;
    private RecyclerView mGalleryList;

    private FloatingActionButton mUploadButton;

    private GalleryListAdapter mAdapter;
    private GalleryMessageDatabase mGalleryMessageDatabase;

    private ConfigPreferenceManager mConfigPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        initData();
        initView();
    }

    private void initData(){
        mGalleryMessageDatabase = new GalleryMessageDatabase(this);
        mAdapter = new GalleryListAdapter(mGalleryMessageDatabase.getGalleryMessage(), getCurrentFragmentManager());
        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<GalleryMessage, GalleryListAdapter.GalleryItemView>() {
            @Override
            public void onClickItem(GalleryListAdapter.GalleryItemView holder, GalleryMessage data) {
                Intent intent = new Intent(GalleryActivity.this, ImageDetailActivity.class);
                intent.putExtra("url", data.getImageUrl());
                startActivity(intent);
            }
        });

        mConfigPref = new ConfigPreferenceManager(this);
    }

    private void initView(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.camera_photo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mGalleryMain = (ImageView) findViewById(R.id.iv_gallery_main);
        mGalleryList = (RecyclerView) findViewById(R.id.rv_gallery_list);
        mUploadButton = (FloatingActionButton) findViewById(R.id.fab_gallery_upload);

        if(mAdapter.getCount() > 0){
            ImageLoadManager.getInstance().loadImageUrl(mConfigPref.getAppConfig().getDefaultImageUrl(), new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if(response != null && response.getBitmap() != null && !response.getBitmap().isRecycled()){
                        mGalleryMain.setImageBitmap(response.getBitmap());
                        SimpleViewUtils.showView(mGalleryMain, View.VISIBLE);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.w(TAG, "onErrorResponse: ", error);
                }
            });
        }else{

        }
        mGalleryList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mGalleryList.setAdapter(mAdapter);

        mUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleStorageManager.chooseDeviceImage(GalleryActivity.this, REQUEST_IMAGE_PICKER);
            }
        });

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Log.d(TAG, "onMove() called with: recyclerView = [" + recyclerView + "], viewHolder = [" + viewHolder + "], target = [" + target + "]");
                return false;
            }

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                super.onSelectedChanged(viewHolder, actionState);
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //Remove swiped item from list and notify the RecyclerView
                Log.d(TAG, "onSwiped() called with: viewHolder = [" + viewHolder + "], swipeDir = [" + swipeDir + "]");
                final int position = viewHolder.getAdapterPosition();
                final GalleryMessage message = mAdapter.getItem(position);
                if(message.getUserId().equals(mConfigPref.getUserInfo().getUid())){
                    mAdapter.getData().remove(message);
                    mAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                        Snackbar.make(mGalleryList, R.string.delete_chat_message, Snackbar.LENGTH_LONG)
                                .setAction(android.R.string.cancel, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mAdapter.getData().add(position, message);
                                        mAdapter.notifyItemInserted(position);
                                    }
                                }).setCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                                super.onDismissed(snackbar, event);
                                if(event != Snackbar.Callback.DISMISS_EVENT_ACTION){
                                    FirebaseDatabase.getInstance()
                                            .getReference(Constants.FIREBASE_DB_ROOT_GALLERY)
                                            .child(message.key).setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "onSuccess() called with: aVoid = [" + aVoid + "]");
                                            int count = mGalleryMessageDatabase.removeGalleryMessage(Constants.PERSON_MESSAGE_NOTIFICATION_ID, message);
                                            Log.d(TAG, "onSuccess() called with: remove = [" + count + "]");

                                        }
                                    });
                                }
                            }
                        }).show();
                    }else{
                        FirebaseDatabase.getInstance()
                                .getReference(Constants.FIREBASE_DB_ROOT_GALLERY)
                                .child(message.key).setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "onSuccess() called with: aVoid = [" + aVoid + "]");
                                int count = mGalleryMessageDatabase.removeGalleryMessage(Constants.PERSON_MESSAGE_NOTIFICATION_ID, message);
                                Log.d(TAG, "onSuccess() called with: remove = [" + count + "]");
                            }
                        });
                    }
                }else{
                    Toast.makeText(GalleryActivity.this, R.string.can_not_delete_chat_message, Toast.LENGTH_SHORT).show();
                    mAdapter.notifyDataSetChanged();
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mGalleryList);
    }

    private void cropImage(Uri uri) {
        GoogleStorageManager.cropChooseDeviceImage(this, uri, 1, 1, 1080, 1080);
    }

    private void onCroppedImage(Intent intent) {
        Log.d(TAG, "onMediaPicker() called with: " + "intent = [" + intent + "]");
        if (intent != null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(intent);
            try {
                Uri uri = result.getUri();
                Intent i  = new Intent(this, ImageUploadActivity.class);
                i.putExtra("uri", uri);
                startActivityForResult(i, REQUEST_IMAGE_UPLOAD);
            } catch (Exception e) {
                Log.w(TAG, "onCroppedImage: ", e);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICKER) {
            if (resultCode == RESULT_OK) {
                cropImage(data.getData());
            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                onCroppedImage(data);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.d(TAG, "onActivityResult() crop error : " + error);
            }
        }else if(requestCode == REQUEST_IMAGE_UPLOAD){
            if(resultCode == RESULT_OK){
                GalleryMessage msg = data.getParcelableExtra(GalleryMessage.class.getName());
                if(msg != null && mAdapter != null){
                    mAdapter.getData().add(0, msg);
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    }
}
