package com.fivetrue.gimpo.ac05.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.fivetrue.gimpo.ac05.ApplicationEX;
import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.analytics.Event;
import com.fivetrue.gimpo.ac05.analytics.GoogleAnalytics;
import com.fivetrue.gimpo.ac05.ui.fragment.BaseFragment;
import com.fivetrue.gimpo.ac05.widget.FTActionBar;


/**
 * Created by ojin.kwon on 2016-02-03.
 */
public class BaseActivity extends FragmentActivity {

    private static final String TAG = "BaseActivity";

    private FTActionBar mActionbar = null;
    private View mActionbarShadow = null;
    private ViewGroup mBaseContainer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkAppConfig();
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

        mActionbar.showDrawerIcon(isVisibleDrawerIcon());

        if(!isBlendingActionBar()){
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mBaseContainer.getLayoutParams();
            if(params != null){
                params.addRule(RelativeLayout.BELOW, mActionbar.getId());
            }
        }

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

    protected void checkAppConfig(){
        if(((ApplicationEX)getApplicationContext()).getAppConfig() == null){
            GoogleAnalytics.getInstance().sendLogEventProperties(Event.AppConfigNull.addParams("checkAppConfig", "null"));
            Intent i = getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage( getBaseContext().getPackageName() );
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
//            System.exit(0);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkAppConfig();
    }

    @Override
    protected void onStop() {
        super.onStop();
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


    protected void onClickActionBarMore(View view){

    }

    protected void onClickActionBarTextButton(View view){

    }

    protected boolean isVisibleDrawerIcon(){
        return true;
    }

    public FragmentManager getCurrentFragmentManager(){
        return getSupportFragmentManager();
    }

    protected int getFragmentAnchorLayoutID(){
        return R.id.layout_base_container;
    }

    protected boolean popFragment(FragmentManager fm){
        boolean b = fm.popBackStackImmediate();
        if(doChangeActionbarTitleByFragment()){
            if(fm.getBackStackEntryCount() > 0){
                getFtActionBar().setTitle(fm.getBackStackEntryAt(fm.getBackStackEntryCount() - 1).getBreadCrumbTitleRes());
            }else{
                getFtActionBar().setTitle(getDefaultActionbarTitle());
            }
        }
        return b;
    }

    public BaseFragment addFragment(Class< ? extends BaseFragment> cls, Bundle arguments, int anchorLayout, boolean addBackstack){
        return addFragment(cls, arguments, anchorLayout, 0, 0, addBackstack);
    }

    public BaseFragment addFragment(Class< ? extends BaseFragment> cls, Bundle arguments, boolean addBackstack){
        return addFragment(cls, arguments, getFragmentAnchorLayoutID(), 0, 0, addBackstack);
    }

    public BaseFragment addFragment(Class< ? extends BaseFragment> cls, Bundle arguments, int anchorLayout, int enterAnim, int exitAnim, boolean addBackstack){
        BaseFragment f = null;
        try {
            f = cls.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if(f != null){
            if(arguments != null){
                f.setArguments(arguments);
            }
            FragmentTransaction ft = getCurrentFragmentManager().beginTransaction();
            ft.setCustomAnimations(enterAnim, exitAnim, enterAnim, exitAnim)
                    .replace(anchorLayout, f, f.getFragmentTag());
            if(addBackstack){
                ft.addToBackStack(f.getFragmentTag());
                ft.setBreadCrumbTitle(f.getFragmentNameResource());
            }
            ft.commitAllowingStateLoss();

            if(doChangeActionbarTitleByFragment() && addBackstack){
                getFtActionBar().setTitle(f.getFragmentNameResource());
            }
        }
        return f;
    }

    @Override
    public void onBackPressed() {
        if(popFragment(getCurrentFragmentManager())){
            return;
        }
        super.onBackPressed();
    }

    protected String getDefaultActionbarTitle(){
        return getString(R.string.app_name);
    }

    protected boolean doChangeActionbarTitleByFragment(){
        return false;
    }
}
