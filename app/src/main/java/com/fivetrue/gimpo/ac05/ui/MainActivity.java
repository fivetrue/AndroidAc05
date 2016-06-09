package com.fivetrue.gimpo.ac05.ui;

import android.os.Bundle;
import android.text.TextUtils;

import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.ui.fragment.UserInfoInputFragment;
import com.fivetrue.gimpo.ac05.vo.user.UserInfo;

/**
 * Created by kwonojin on 16. 6. 7..
 */
public class MainActivity extends DrawerActivity implements UserInfoInputFragment.OnUserInfoChangedListener{

    private static final String TAG = "MainActivity";

    private UserInfo mUserInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();

        checkUserInfo();
    }

    private void initData(){
        mUserInfo = getIntent().getParcelableExtra(UserInfo.class.getName());
    }

    private void checkUserInfo(){
        if(mUserInfo != null){
            if(TextUtils.isEmpty(mUserInfo.getApartDong())){
                Bundle arg = new Bundle();
                arg.putParcelable(UserInfo.class.getName(), mUserInfo);
                addFragment(UserInfoInputFragment.class, arg,
                        getBaseLayoutContainer().getId(), android.R.anim.fade_in, android.R.anim.fade_out,true);
            }
        }
    }

    @Override
    public void onChanged(UserInfo info) {

    }
}
