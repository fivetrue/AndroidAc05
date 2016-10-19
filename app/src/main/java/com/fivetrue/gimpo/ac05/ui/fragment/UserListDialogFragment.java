package com.fivetrue.gimpo.ac05.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fivetrue.fivetrueandroid.ui.adapter.BaseRecyclerAdapter;
import com.fivetrue.fivetrueandroid.ui.fragment.BaseDialogFragment;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.ui.adapter.UserListAdapter;
import com.fivetrue.gimpo.ac05.vo.user.FirebaseUserInfo;

import java.util.List;

/**
 * Created by kwonojin on 2016. 10. 18..
 */

public class UserListDialogFragment extends BaseDialogFragment {

    private static final String TAG = "UserListDialogFragment";

    private RecyclerView mRecyclerView;
    private List<FirebaseUserInfo> mUserList;
    private UserListAdapter mUserListAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserList = getArguments().getParcelableArrayList(FirebaseUserInfo.class.getName());
        mUserListAdapter = new UserListAdapter(mUserList);
        mUserListAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<FirebaseUserInfo, UserListAdapter.UserItemView>() {
            @Override
            public void onClickItem(UserListAdapter.UserItemView holder, FirebaseUserInfo data) {
                Bundle b = new Bundle();
                b.putParcelable(FirebaseUserInfo.class.getName(), data);
                UserInfoDialogFragment dialog = new UserInfoDialogFragment();
                dialog.show(getFragmentManager(), null
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
        });
    }

    @Override
    protected View onCreateChildView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.fragment_user_list, null);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_fragment_user_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView.setAdapter(mUserListAdapter);
    }
}
