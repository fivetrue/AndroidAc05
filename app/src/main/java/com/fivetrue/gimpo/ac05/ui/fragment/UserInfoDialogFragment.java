package com.fivetrue.gimpo.ac05.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.fivetrue.fivetrueandroid.image.ImageLoadManager;
import com.fivetrue.fivetrueandroid.ui.fragment.BaseDialogFragment;
import com.fivetrue.gimpo.ac05.Constants;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.chatting.BadUserReportMessage;
import com.fivetrue.gimpo.ac05.chatting.ChatMessage;
import com.fivetrue.gimpo.ac05.chatting.GalleryMessage;
import com.fivetrue.gimpo.ac05.preferences.ConfigPreferenceManager;
import com.fivetrue.gimpo.ac05.ui.adapter.UserListAdapter;
import com.fivetrue.gimpo.ac05.vo.user.FirebaseUserInfo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

/**
 * Created by kwonojin on 2016. 10. 18..
 */

public class UserInfoDialogFragment extends BaseDialogFragment implements BaseDialogFragment.OnClickDialogFragmentListener {

    private static final String TAG = "UserInfoDialogFragment";

    private NetworkImageView mUserImage;
    private TextView mUserName;

    private FirebaseUserInfo mUserInfo;
    private ChatMessage mChatMessage;
    private GalleryMessage mGalleryMessage;

    private DatabaseReference mPersonDBReference;

    private ConfigPreferenceManager mConfigPref;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserInfo = getArguments().getParcelable(FirebaseUserInfo.class.getName());
        mChatMessage = getArguments().getParcelable(ChatMessage.class.getName());
        mGalleryMessage= getArguments().getParcelable(GalleryMessage.class.getName());
        mConfigPref = new ConfigPreferenceManager(getActivity());
        mPersonDBReference = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_DB_ROOT_PERSON).child(getUserId());
    }

    private String getUserImage(){
        if(mUserInfo != null){
            return mUserInfo.getPhotoUrl();
        }

        if(mChatMessage != null){
            return mChatMessage.getUserImage();
        }

        if(mGalleryMessage != null){
            return mGalleryMessage.getUserImage();
        }

        return null;
    }

    private String getUserName(){
        if(mUserInfo != null){
            return mUserInfo.getName();
        }

        if(mChatMessage != null){
            return mChatMessage.getName();
        }

        if(mGalleryMessage != null){
            return mGalleryMessage.getName();
        }
        return null;
    }

    private String getUserId(){
        if(mUserInfo != null){
            return mUserInfo.getUid();
        }

        if(mChatMessage != null){
            return mChatMessage.getUserId();
        }

        if(mGalleryMessage != null){
            return mGalleryMessage.getUserId();
        }

        return null;
    }

    private String getUserEmail(){
        if(mUserInfo != null){
            return mUserInfo.getEmail();
        }

        if(mChatMessage != null){
            return mChatMessage.getUser();
        }

        if(mGalleryMessage != null){
            return mGalleryMessage.getUser();
        }

        return null;
    }

    @Override
    protected View onCreateChildView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.fragment_user_info, null);
        mUserImage = (NetworkImageView) view.findViewById(R.id.iv_fragment_user_info_image);
        mUserName = (TextView) view.findViewById(R.id.tv_fragment_user_info_name);
        if(getUserId().equals(mConfigPref.getUserInfo().getUid())){
            view.findViewById(R.id.layout_fragment_user_info_buttons).setVisibility(View.GONE);
        }
        view.findViewById(R.id.btn_fragment_user_info_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputDialogFragment dialog = new InputDialogFragment();
                dialog.show(getFragmentManager(), getString(R.string.sending_message)
                        , getString(android.R.string.ok)
                        , getString(android.R.string.cancel), null
                        , new OnClickDialogFragmentListener<String>() {
                            @Override
                            public void onClickOKButton(BaseDialogFragment f, String data) {
                                Log.d(TAG, "onClickOKButton() called with: f = [" + f + "], data = [" + data + "]");
                                if(!TextUtils.isEmpty(data)){
                                    ChatMessage message = new ChatMessage(null, data, null
                                            , mConfigPref.getUserInfo().getEmail()
                                            , mConfigPref.getUserInfo().getUid()
                                            ,mConfigPref.getUserInfo().getPhotoUrl(), 0);
                                    mPersonDBReference.child(Constants.FIREBASE_DB_MESSAGE).push().setValue(message.getValues());
                                }
                                f.dismiss();
                            }

                            @Override
                            public void onClickCancelButton(BaseDialogFragment f, String data) {
                                f.dismiss();
                            }
                        });
            }
        });

        view.findViewById(R.id.btn_fragment_user_info_bad_user_report).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputDialogFragment dialog = new InputDialogFragment();
                dialog.show(getFragmentManager(), getString(R.string.report_bad_user)
                        , getString(android.R.string.ok)
                        , getString(android.R.string.cancel), null
                        , new OnClickDialogFragmentListener<String>() {
                            @Override
                            public void onClickOKButton(BaseDialogFragment f, String data) {
                                Log.d(TAG, "onClickOKButton() called with: f = [" + f + "], data = [" + data + "]");
                                if(!TextUtils.isEmpty(data)){
                                    BadUserReportMessage reportMessage = new BadUserReportMessage(getUserId()
                                    , getUserEmail(), getUserImage(), data);
                                    FirebaseDatabase.getInstance()
                                            .getReference(Constants.FIREBASE_DB_ROOT_BAD_USER).push()
                                            .setValue(reportMessage.getValues()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            if(getActivity() != null){
                                                Toast.makeText(getActivity(), R.string.report_bad_user_completed, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                                f.dismiss();
                            }

                            @Override
                            public void onClickCancelButton(BaseDialogFragment f, String data) {
                                f.dismiss();
                            }
                        });

            }
        });
        mUserImage.setImageUrl(getUserImage(), ImageLoadManager.getImageLoader());
        mUserName.setText(getUserName());
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onClickOKButton(BaseDialogFragment f, Object data) {

    }

    @Override
    public void onClickCancelButton(BaseDialogFragment f, Object data) {

    }
}
