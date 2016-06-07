package com.fivetrue.gimpo.ac05.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.ui.fragment.BaseFragment;

/**
 * Created by kwonojin on 16. 3. 15..
 */
public class BaseActivity extends FragmentActivity {

    private FrameLayout mLayoutMainContainer = null;

    private static final String TAG = BaseActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
    public void setContentView(int layoutResID) {
//        super.setContentView(layoutResID);
        View activityView = LayoutInflater.from(this).inflate(layoutResID, null);
        setContentView(activityView);
    }

    @Override
    public void setContentView(View view) {
        View mergeView = initView(view);
        super.setContentView(mergeView);
    }

    private View initView(View childView) {
        View rootView = LayoutInflater.from(this).inflate(R.layout.activity_base, null);
        mLayoutMainContainer = (FrameLayout) rootView.findViewById(R.id.layout_main_container);
        mLayoutMainContainer.addView(childView);
        return rootView;
    }

    protected int getFragmentAchorViewId() {
        return mLayoutMainContainer.getId();
    }

    /**
     * BaseFragment를 추가한다. 이미 추가된 Fragment일 경우 FragmentManager에서 TAG값을 이용해 Frament를 가져와
     * 다시 Replace 시킨다
     * @param fClss BaseFragment 를 상속 받은 Fragment의 class
     * @param bundle 전달한 Fragment Argument
     */
    public void addFragment(Class<? extends BaseFragment> fClss, Bundle bundle,
                            int enterTransition, int exitTransition) {
        BaseFragment f = null;
        try {
            f = fClss.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        if(f != null){
            FragmentTransaction ft = getCurrentFragmentManager().beginTransaction();
            ft.setCustomAnimations(enterTransition, exitTransition, enterTransition, exitTransition)
                    .replace(getFragmentAchorViewId(), f, f.getFragmentTag());
            if(bundle != null){
                f.setArguments(bundle);
            }
            ft.setBreadCrumbTitle(f.getFragmentTitle())
                    .addToBackStack(f.getBackstackName())
                    .commitAllowingStateLoss();
        }
    }

    public void addFragment(Class<? extends BaseFragment> fClss, Bundle bundle){
        addFragment(fClss, bundle, R.anim.enter_smooth, R.anim.exit_smooth);
    }

    public AlertDialog createAlertDialog(String msg,DialogInterface.OnClickListener okListener,DialogInterface.OnClickListener cancelListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg);
        builder.setPositiveButton(getResources().getText(R.string.ok), okListener);
        builder.setNegativeButton(getResources().getText(R.string.cancel),cancelListener);
        return builder.create();
    }


    @Override
    public void onBackPressed() {
        if(popFragment(getCurrentFragmentManager())){
            return;
        }
        super.onBackPressed();
    }

    protected boolean popFragment(FragmentManager fm){
        return fm.popBackStackImmediate();
    }

    protected FragmentManager getCurrentFragmentManager(){
        return getSupportFragmentManager();
    }
}
