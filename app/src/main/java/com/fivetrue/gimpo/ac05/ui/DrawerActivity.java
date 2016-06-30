package com.fivetrue.gimpo.ac05.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.analytics.GoogleAnalytics;
import com.fivetrue.gimpo.ac05.ui.fragment.BaseFragment;
import com.fivetrue.gimpo.ac05.ui.fragment.DrawerLeftMenuFragment;
import com.fivetrue.gimpo.ac05.utils.Log;
import com.fivetrue.gimpo.ac05.vo.LeftMenu;

public class DrawerActivity extends BaseActivity implements DrawerLeftMenuFragment.OnMenuClickItemListener{

    private static final String TAG = DrawerActivity.class.getSimpleName();

    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected View mergeView(View childView, View parentView) {
        View mergeView = super.mergeView(childView, parentView);
        View view = LayoutInflater.from(this).inflate(R.layout.activity_menu, null);
        initView(view);
        mDrawerLayout.addView(mergeView, 0);
        return view;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mDrawerLayout != null){
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.END);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mDrawerLayout != null){
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.END);
        }
    }

    public void toggleMenu() {/**/
        if (false == isOpenMenu()) {
            openMenu();
        } else {
            closeMenu();
        }
    }

    public boolean isOpenRightDrawer(){
        boolean b = false;
        if(mDrawerLayout != null){
            b = mDrawerLayout.isDrawerOpen(Gravity.END);
        }
        return b;
    }

    public void closeRightDrawer(){
        if (mDrawerLayout.isDrawerOpen(Gravity.END)) {
            mDrawerLayout.closeDrawer(Gravity.END);
        }
    }

    public boolean isOpenMenu() {
        boolean b = false;
        if(mDrawerLayout != null){
            b = mDrawerLayout.isDrawerOpen(Gravity.START);
        }
        return b;
    }

    public void openMenu() {
        Log.d(TAG, "openMenu");
        if (!mDrawerLayout.isDrawerOpen(Gravity.START)) {
            mDrawerLayout.openDrawer(Gravity.START);
        }
    }

    public void closeMenu() {
        Log.d(TAG, "closeMenu");
        if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
            mDrawerLayout.closeDrawer(Gravity.START);
        }
    }

    @Override
    public BaseFragment addFragment(Class<? extends BaseFragment> cls, Bundle arguments, int anchorLayout, int enterAnim, int exitAnim, boolean addBackstack) {
        BaseFragment f = super.addFragment(cls, arguments, anchorLayout, enterAnim, exitAnim, addBackstack);
        if(addBackstack){
            getFtActionBar().setHomeAsUp(true, Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
        }
        return f;
    }


    @Override
    protected boolean popFragment(FragmentManager fm) {
        boolean b = super.popFragment(fm);
        if(getCurrentFragmentManager().getBackStackEntryCount() <= 0){
            getFtActionBar().setHomeAsUp(false, Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
            getFtActionBar().setTitle(R.string.app_name);
        }
        return b;
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");
        if (closeDrawer()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
    }

    public boolean closeDrawer(){
        boolean b = false;
        if (isOpenMenu()) {
            b =  true;
            closeMenu();
        }

        if(isOpenRightDrawer()){
            b =  true;
            closeRightDrawer();
        }
        return b;
    }



    private void initView(View view) {

        mDrawerLayout = (DrawerLayout) view.findViewById(R.id.layout_drawer);
        mDrawerLayout.setFocusableInTouchMode(false);

        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.END);
        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View view, float offset) {
                if (getFtActionBar() != null && getFtActionBar().getDrawerButton() != null) {
                    getFtActionBar().getDrawerButton().setRotationOffset(offset);
                }
//                if(getBaseLayoutContainer() != null && mLeftDrawerFragment != null){
//                    getBaseLayoutContainer().setX(offset * mLeftDrawerFragment.getView().getWidth());
//                }
            }

            @Override
            public void onDrawerOpened(View view) {
                Log.d(TAG, "onDrawerOpened");
            }

            @Override
            public void onDrawerClosed(View view) {
                Log.d(TAG, "onDrawerClosed");
            }

            @Override
            public void onDrawerStateChanged(int i) {
                Log.d(TAG, "onDrawerStateChanged");
            }
        });
    }

    @Override
    protected void onClickActionBarHome(View view, boolean isHomeAsUp) {
        if(isHomeAsUp){
            super.onClickActionBarHome(view, isHomeAsUp);
        }else{
            toggleMenu();
        }
    }

    protected DrawerLayout getDrawerLayout(){
        return mDrawerLayout;
    }

    @Override
    public void onClickMenu(LeftMenu menu) {
        if(menu != null && DrawerActivity.this.getClass() != menu.getActivity() ){
            Intent i = new Intent(DrawerActivity.this, menu.getActivity());
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
        closeMenu();
    }
}