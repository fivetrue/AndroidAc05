package com.fivetrue.gimpo.ac05.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.fivetrue.fivetrueandroid.net.BaseApiRequest;
import com.fivetrue.fivetrueandroid.net.BaseApiResponse;
import com.fivetrue.fivetrueandroid.net.NetworkManager;
import com.fivetrue.fivetrueandroid.ui.BaseActivity;
import com.fivetrue.fivetrueandroid.ui.adapter.BaseRecyclerAdapter;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.net.request.AdminUpdateDataRequest;
import com.fivetrue.gimpo.ac05.preferences.ConfigPreferenceManager;
import com.fivetrue.gimpo.ac05.ui.adapter.AdminListAdapter;

import java.util.ArrayList;

/**
 * Created by kwonojin on 16. 6. 10..
 */
public class AdminActivity extends BaseActivity {

    private static final String TAG = "AdminActivity";

    private RecyclerView mRecyclerView;

    private ArrayList<BaseApiRequest> requestArrayList;

    private ConfigPreferenceManager mConfig;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        initData();
        initView();
    }

    protected void initData(){
        mConfig = new ConfigPreferenceManager(this);
        requestArrayList = new ArrayList<>();
        AdminUpdateDataRequest updateDataRequest = new AdminUpdateDataRequest(this, onResponseListener);
        updateDataRequest.setTimeoutMills(1000 * 1000 * 5);
        updateDataRequest.putParam("email", mConfig.getUserInfo().email);
        requestArrayList.add(updateDataRequest);
    }

    private void initView(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.admin);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_admin_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        AdminListAdapter adapter = new AdminListAdapter(requestArrayList);
        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<BaseApiRequest, AdminListAdapter.AdminViewHolder>() {
            @Override
            public void onClickItem(AdminListAdapter.AdminViewHolder holder, final BaseApiRequest data) {
                if(data != null){
                    Toast.makeText(AdminActivity.this, data.getUrl(), Toast.LENGTH_SHORT).show();
                    NetworkManager.getInstance().request(data);
                }
            }
        });
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home :
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected boolean transitionModeWhenFinish() {
        return true;
    }

    private BaseApiResponse.OnResponseListener<String> onResponseListener = new BaseApiResponse.OnResponseListener<String>() {
        @Override
        public void onResponse(BaseApiResponse<String> response) {
            if(response != null){
                Log.d(TAG, "onResponse() called with: response = [" + response + "]");
                Toast.makeText(AdminActivity.this, response.getData(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onError(VolleyError error) {
            Toast.makeText(AdminActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
        }
    };
}
