package com.fivetrue.gimpo.ac05.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.preferences.ConfigPreferenceManager;
import com.fivetrue.gimpo.ac05.ui.CafeActivity;
import com.fivetrue.gimpo.ac05.ui.MainActivity;
import com.fivetrue.gimpo.ac05.ui.NoticeListActivity;
import com.fivetrue.gimpo.ac05.ui.SettingActivity;
import com.fivetrue.gimpo.ac05.ui.adapter.BaseListAdapter;
import com.fivetrue.gimpo.ac05.ui.adapter.LeftMenuListAdapter;
import com.fivetrue.gimpo.ac05.view.CircleImageView;
import com.fivetrue.gimpo.ac05.vo.LeftMenu;
import com.fivetrue.gimpo.ac05.vo.user.UserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fivetrue on 2016-02-15.
 */
public class DrawerLeftMenuFragment extends  BaseListFragment<LeftMenu> {

    public interface OnMenuClickItemListener{
        void onClickMenu(LeftMenu menu);
    }

    public static final String TAG = DrawerLeftMenuFragment.class.getSimpleName();

    private TextView mName = null;
    private TextView mEmail = null;
    private TextView mDistrict = null;
    private CircleImageView mProfileImage = null;

    private ArrayList<LeftMenu> mLeftMenu = null;
    private OnMenuClickItemListener mOnMenuClickItemListener = null;

    private ConfigPreferenceManager mConfigPref = null;

    public DrawerLeftMenuFragment(){

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(getActivity() != null && getActivity() instanceof OnMenuClickItemListener){
            mOnMenuClickItemListener = (OnMenuClickItemListener) getActivity();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    @Override
    protected BaseListAdapter makeAdapter(List<LeftMenu> datas) {
        return new LeftMenuListAdapter(getActivity(), datas);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        setListData(mLeftMenu);
        return view;
    }

    private void initData(){
        mLeftMenu = new ArrayList<>();
        mLeftMenu.add(new LeftMenu(getString(R.string.main)
                , R.drawable.ic_home_20dp
                , MainActivity.class));
        mLeftMenu.add(new LeftMenu(getString(R.string.notice)
                , R.drawable.ic_notification_20dp
                , NoticeListActivity.class));
        mLeftMenu.add(new LeftMenu(getString(R.string.cafe)
                , R.drawable.ic_cafe_20dp
                , CafeActivity.class));
        mLeftMenu.add(new LeftMenu(getString(R.string.setting)
                , R.drawable.ic_setting_20dp
                , SettingActivity.class));

        mConfigPref = new ConfigPreferenceManager(getActivity());
    }

    @Override
    protected void initListView(final ListView listVIew) {
        super.initListView(listVIew);
        getListView().setVerticalScrollBarEnabled(false);
        listVIew.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        listVIew.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (getAdapter() != null) {
                    LeftMenu menu = (LeftMenu) getAdapter().getItem(i - listVIew.getHeaderViewsCount());
                    onMenuClickItemListener.onClickMenu(menu);
                }
            }
        });

        View headerView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_left_menu_header, null);
        initListHeader(headerView);
        listVIew.addHeaderView(headerView, null, false);
    }

    protected void initListHeader(View view){
        UserInfo userInfo = mConfigPref.getUserInfo();
        if(view != null){

            mName = (TextView) view.findViewById(R.id.tv_left_menu_header_name);
            mEmail = (TextView) view.findViewById(R.id.tv_left_menu_header_email);
            mProfileImage = (CircleImageView) view.findViewById(R.id.iv_left_menu_header);
            mDistrict = (TextView) view.findViewById(R.id.tv_left_menu_header_district);

            mName.setText(userInfo.getName() + " / " + userInfo.getId());
            mEmail.setText(userInfo.getEmail());
            mProfileImage.setImageUrl(userInfo.getProfileImage());
            mDistrict.setText(userInfo.getApartDong());
            mDistrict.setVisibility(TextUtils.isEmpty(userInfo.getApartDong()) ? View.GONE : View.VISIBLE);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }


    private OnMenuClickItemListener onMenuClickItemListener = new OnMenuClickItemListener() {
        @Override
        public void onClickMenu(LeftMenu menu) {
            if(mOnMenuClickItemListener != null){
                mOnMenuClickItemListener.onClickMenu(menu);
            }
        }
    };
}
