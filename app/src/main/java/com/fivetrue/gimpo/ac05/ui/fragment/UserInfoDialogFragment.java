package com.fivetrue.gimpo.ac05.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.fivetrue.gimpo.ac05.firebase.database.UserMessageBoxDatabase;
import com.fivetrue.gimpo.ac05.firebase.model.BadUserReportMessage;
import com.fivetrue.gimpo.ac05.chatting.ChatMessage;
import com.fivetrue.gimpo.ac05.preferences.ConfigPreferenceManager;
import com.fivetrue.gimpo.ac05.firebase.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by kwonojin on 2016. 10. 18..
 */

public class UserInfoDialogFragment extends BaseDialogFragment implements BaseDialogFragment.OnClickDialogFragmentListener {

    private static final String TAG = "UserInfoDialogFragment";

    private NetworkImageView mUserImage;
    private TextView mUserName;

    private User mUserInfo;

    private UserMessageBoxDatabase mPeronsalMessageDatabase;

    private ConfigPreferenceManager mConfigPref;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserInfo = getArguments().getParcelable(User.class.getName());
        mConfigPref = new ConfigPreferenceManager(getActivity());
        mPeronsalMessageDatabase = new UserMessageBoxDatabase(mUserInfo.uid);
    }

    @Override
    protected View onCreateChildView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.fragment_user_info, null);
        mUserImage = (NetworkImageView) view.findViewById(R.id.iv_fragment_user_info_image);
        mUserName = (TextView) view.findViewById(R.id.tv_fragment_user_info_name);
        if(mUserInfo.uid.equals(mConfigPref.getUserInfo().uid)){
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
                                            , mConfigPref.getUserInfo());
                                    mPeronsalMessageDatabase.pushData(message);
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
                                    BadUserReportMessage reportMessage = new BadUserReportMessage(data, mUserInfo);
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
        mUserImage.setImageUrl(mUserInfo.profileImage, ImageLoadManager.getImageLoader());
        mUserName.setText(mUserInfo.getDisplayName());
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
