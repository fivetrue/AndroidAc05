package com.fivetrue.gimpo.ac05.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.widget.FTActionBar;


/**
 * Created by ojin.kwon on 2016-02-03.
 */
public class BaseActivity extends FragmentActivity {

    private FTActionBar mActionbar = null;
    private View mActionbarShadow = null;
    private ViewGroup mBaseContainer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initView(){
        mActionbar = (FTActionBar) findViewById(R.id.layout_actionbar);
        mActionbarShadow = findViewById(R.id.view_actionbar_shadow);

        mActionbar.getSearchButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickActionBarSearch(v);
            }
        });

        mActionbar.getDrawerButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickActionBarHome(v, mActionbar.isHomeAsUp());
            }
        });

        if(!isBlendingActionBar()){
            RelativeLayout.LayoutParams params  = (RelativeLayout.LayoutParams) mBaseContainer.getLayoutParams();
            if(params != null){
                params.addRule(RelativeLayout.BELOW, mActionbar.getId());
            }
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        View childView = LayoutInflater.from(this).inflate(layoutResID, null);
        View parentView = LayoutInflater.from(this).inflate(R.layout.activity_base, null);
        super.setContentView(mergeView(childView, parentView));
        initView();
    }

    protected View mergeView(View childView, View parentView){
        setBaseContentView((ViewGroup) parentView.findViewById(R.id.layout_base_container));
        getBaseLayoutContainer().addView(childView);
        return parentView;
    }

    protected void setBaseContentView(ViewGroup view){
        mBaseContainer = view;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected FTActionBar getFtActionBar(){
        return mActionbar;
    }

    protected ViewGroup getBaseLayoutContainer(){
        return mBaseContainer;
    }

    protected void onClickActionBarSearch(View view){

    }

    protected void onClickActionBarHome(View view, boolean isHomeAsUp){
        if(isHomeAsUp){
            onBackPressed();
        }
    }

    public void setActionbarVisible(boolean isVisible){
        mActionbarShadow.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        mActionbar.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
//        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void finish() {
        super.finish();
//        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    protected boolean isBlendingActionBar(){
        return false;
    }
}
