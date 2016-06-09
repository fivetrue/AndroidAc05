package com.fivetrue.gimpo.ac05.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.vo.user.UserInfo;

/**
 * Created by kwonojin on 16. 6. 9..
 */
public class UserInfoInputFragment extends BaseFragment {

    private static final String TAG = "UserInfoInputFragment";

    public interface OnUserInfoChangedListener{
        void onChanged(UserInfo info);
    }

    private TextView mTvWellcome = null;

    private UserInfo mUserInfo = null;

    private OnUserInfoChangedListener mOnUserInfoChangedListener = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserInfo = getArguments().getParcelable(UserInfo.class.getName());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return initView(inflater);
    }

    private View initView(LayoutInflater inflater){
        View view = inflater.inflate(R.layout.fragment_userinfo_input, null);
        mTvWellcome = (TextView) view.findViewById(R.id.tv_fragment_userinfo_input_wellcome);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTvWellcome.setText(String.format(getString(R.string.userinfo_input_wellcome), mUserInfo.getNickname()));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(getActivity() != null && getActivity() instanceof OnUserInfoChangedListener){
            mOnUserInfoChangedListener = (OnUserInfoChangedListener) getActivity();
        }
    }
}
