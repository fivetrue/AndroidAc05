package com.fivetrue.gimpo.ac05.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fivetrue.gimpo.ac05.R;


/**
 * Created by kwonojin on 15. 10. 2..
 */
public class FTActionBar extends RelativeLayout {

    public static final int ACTIONBAR_MODE_EMPTY = -1;
    public static final int ACTIONBAR_MODE_DEFAULT = 0x00;
    public static final int ACTIONBAR_MODE_DEFAULT_ICON = 0x01;
    public static final int ACTIONBAR_MODE_DEFAULT_TITLE = 0x02;
    public static final int ACTIONBAR_MODE_NONE = 0x10;
    public static final int ACTIONBAR_MODE_SEARCH_INPUT = 0x20;
    public static final int ACTIONBAR_MODE_SEARCH_INPUT_FULL = 0x21;
    public static final int ACTIONBAR_MODE_PRODUCT_DETAIL = 0x30;

    private RelativeLayout mLayoutLeft = null;
    private DrawerButton mDrawerButton = null;
    private ImageView mLogoIcon = null;
    private TextView mTitle = null;

    private LinearLayout mLayoutRight = null;
    private ImageView mRightButton = null;
    private TextView mRightSticker = null;

    private ImageView mInputIcon = null;
    private ImageView mInputDelete = null;
    private ImageView mSearchInputIcon = null;
//    private ImageView mSearchButton = null;

    private View mBackround = null;

    private Animation mEmergeAnimation = null;

    private int mAlphaValue = 0x00;

    private int mMode = ACTIONBAR_MODE_EMPTY;


    public FTActionBar(Context context) {
        super(context);
        init();
    }

    public FTActionBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.actionbar_main, this, true);

        mLayoutLeft = (RelativeLayout) findViewById(R.id.layout_actionbar_left);
        mDrawerButton = (DrawerButton) findViewById(R.id.btn_actionbar_home);
        mLogoIcon = (ImageView) findViewById(R.id.iv_actionbar_logo);
        mTitle = (TextView) findViewById(R.id.tv_actionbar_title);

        mLayoutRight = (LinearLayout) findViewById(R.id.layout_actionbar_right);
        mRightButton = (ImageView) findViewById(R.id.iv_actionbar_right_button);
//        mRightSticker = (TextView) findViewById(R.id.tv_actionbar_right_sticker);
//
//        mLayoutInput = (LinearLayout) findViewById(R.id.layout_actionbar_search);
//        mInputIcon = (ImageView) findViewById(R.id.iv_actionbar_search_icon);
//        mInputDelete = (ImageView) findViewById(R.id.iv_actionbar_search_delete_icon);
//        mSearchInputIcon = (ImageView) findViewById(R.id.iv_actionbar_search_input_icon);
//        mInput = (EditText) findViewById(R.id.et_actionbar_search_input);
//        mSearchButton = (ImageView) findViewById(R.id.iv_actionbar_search_button);

        mBackround = findViewById(R.id.view_actionbar_background);

//        mEmergeAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.anim_emerge);
    }

    /**
     * 액션바의 DrawerButton을 Back으로 할지 Drawer버튼으로 변경할지 정한다.
     * @param isUp
     */
    public void setHomeAsUp(boolean isUp){
        setHomeAsUp(isUp, false);
    }

    public void setHomeAsUp(final boolean isUp, boolean smooth){
        mDrawerButton.setVisibility(VISIBLE);
        if(smooth){
            ValueAnimator animator = ValueAnimator.ofInt(0, 100);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if (animation != null && animation.getAnimatedValue() != null && animation.getAnimatedValue() instanceof Integer) {
                        int value = (Integer) animation.getAnimatedValue();
                        mDrawerButton.setRotationOffset((float)value / (float)100);
                    }
                }
            });
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mDrawerButton.setHomeAsUp(isUp);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            animator.setDuration(300L);
            animator.start();
        }else{
            mDrawerButton.setHomeAsUp(isUp);
        }

    }

    public boolean isHomeAsUp(){
        return mDrawerButton.isHomeAsUp();
    }

    public void showLogo(boolean isShow){
        mTitle.setVisibility(isShow ? GONE : VISIBLE);
        mLogoIcon.setVisibility(isShow ? VISIBLE : GONE);
    }

    public void showDrawerIcon(boolean isShow){
        mDrawerButton.setVisibility(isShow ? VISIBLE : GONE);
    }

    public void showRightButton(boolean isShow){
        mLayoutRight.setVisibility(isShow ? VISIBLE : GONE);
    }

    public ImageView getRightButton(){
        return mRightButton;
    }

    public DrawerButton getDrawerButton(){
        return mDrawerButton;
    }

    public ImageView getLogoIcon(){
        return mLogoIcon;
    }

    public void showDeleteInput(boolean isShow){
        mInputDelete.setVisibility(isShow ? VISIBLE : GONE);
    }

    /**
     * 액션바의 타이틀을 정한다. 타이틀 값이 없을 경우 액션바 로고가 나타난다.
     * @param title
     */
    public void setTitle(String title){
        if(!TextUtils.isEmpty(title)){
            mTitle.setText(title);
            mTitle.setVisibility(View.VISIBLE);
            mLogoIcon.setVisibility(View.GONE);
        }else{
            mTitle.setVisibility(View.GONE);
            mLogoIcon.setVisibility(View.VISIBLE);
        }
    }

    public String getTitle(){
        return mTitle.getText().toString();
    }

    public void setTitle(int resID){
        setTitle(getResources().getString(resID));
    }

    /**
     * 카트버튼 위의 스티커를 표시한다. 스티커가 알리는 값이 0 이면 스티커는 사라진다.
     * @param count
     */
    public void showSticker(int count){
        if (count > 0) {
            mRightSticker.setText(String.valueOf(count));
            mRightSticker.setVisibility(VISIBLE);
//            startStickerAnimation();
        } else {
            mRightSticker.setText(String.valueOf(count));
            mRightSticker.clearAnimation();
            mRightSticker.setVisibility(GONE);
        }
    }

    public void startStickerAnimation() {
        mRightSticker.startAnimation(mEmergeAnimation);
    }

//    public ImageView getSearchButton(){
//        return mSearchButton;
//    }

    public ImageView getInputDelete(){
        return mInputDelete;
    }

    /**
     * CartButton위에 표시되는 Sticker를 없앤다.
     */
    public void dismissSticker(){
        mRightSticker.setVisibility(View.GONE);
    }

    public void setActionbarMode(int mode){

        mMode = mode;

        switch(mode){
            case ACTIONBAR_MODE_SEARCH_INPUT_FULL :
            case ACTIONBAR_MODE_SEARCH_INPUT :
                mLayoutRight.setVisibility(GONE);
                mLayoutLeft.setVisibility(GONE);
//                mLayoutInput.setVisibility(VISIBLE);

                mInputIcon.setVisibility(GONE);
                mSearchInputIcon.setVisibility(GONE);
                mInputIcon.setVisibility(View.GONE);
                mSearchInputIcon.setVisibility(View.VISIBLE);
                break;

            case ACTIONBAR_MODE_DEFAULT :
//                mLayoutInput.setVisibility(View.GONE);
                mLayoutLeft.setVisibility(View.VISIBLE);
                mLayoutRight.setVisibility(View.VISIBLE);
//                mSearchButton.setVisibility(View.GONE);
                break;

            case ACTIONBAR_MODE_DEFAULT_ICON :
//                mLayoutInput.setVisibility(View.GONE);
                mLayoutLeft.setVisibility(View.VISIBLE);
                mLayoutRight.setVisibility(View.VISIBLE);
                mTitle.setVisibility(View.GONE);
                mLogoIcon.setVisibility(View.VISIBLE);
//                mSearchButton.setVisibility(View.GONE);
                break;

            case ACTIONBAR_MODE_DEFAULT_TITLE :
//                mLayoutInput.setVisibility(View.GONE);
                mLayoutLeft.setVisibility(View.VISIBLE);
                mLayoutRight.setVisibility(View.VISIBLE);
                mTitle.setVisibility(View.VISIBLE);
                mLogoIcon.setVisibility(View.GONE);
//                mSearchButton.setVisibility(View.GONE);
                break;

            case ACTIONBAR_MODE_NONE :
//                mLayoutInput.setVisibility(View.VISIBLE);
                mLayoutLeft.setVisibility(View.VISIBLE);

                mInputIcon.setVisibility(View.VISIBLE);
                mLayoutRight.setVisibility(View.VISIBLE);
                mSearchInputIcon.setVisibility(View.GONE);
//                mSearchButton.setVisibility(View.GONE);
                break;

            case ACTIONBAR_MODE_PRODUCT_DETAIL :
//                mLayoutInput.setVisibility(View.GONE);
                mLayoutLeft.setVisibility(View.VISIBLE);
                mLayoutRight.setVisibility(View.VISIBLE);
                mTitle.setVisibility(View.VISIBLE);
                mLogoIcon.setVisibility(View.GONE);
//                mSearchButton.setVisibility(View.VISIBLE);
                break;

            case ACTIONBAR_MODE_EMPTY :
            default:
//                mLayoutInput.setVisibility(View.GONE);
                mLayoutRight.setVisibility(View.GONE);
                mLayoutLeft.setVisibility(View.VISIBLE);
//                mSearchButton.setVisibility(View.GONE);

        }
    }

    public int getActionBarMode(){
        return mMode;
    }

    /**
     * 0x00 ~ 0xFF의 값을 이용하여 알파값을 변경한다.
     * @param value 0x00 ~ 0xFF
     */
    public void setAlphaValue(int value){
        mAlphaValue = value;
//        getBackground().setAlpha(mAlphaValue);
        mBackround.setAlpha((float) mAlphaValue / (float)0xFF);
        mTitle.setAlpha((float)mAlphaValue / (float)0xFF);
    }

    /**
     *
     * @param b true일 경우 알파값을 0으로 만들어 투명하게 만든다. false일 경우 이전의 값으로 복원 시킨다.
     */
    public void setAlphaSmooth(boolean b){
        if(b){
            smoothAlpha(0xFF, mAlphaValue > 0 ? mAlphaValue : 0x00);
        }else{
            smoothAlpha(mAlphaValue, 0xFF);
        }
    }

    /**
     * smooth하게 알파값을 변경한다.
     * @param offset
     */
    public void setAlphaSmooth(float offset){
        if(offset <= 1 && offset >=0 ){
            int value = (int)(offset * 0xFF);
            smoothAlpha(mAlphaValue, value);
        }
    }

    /**
     * 알파값 변경 애니매이션
     * @param from
     * @param to
     */
    private void smoothAlpha(int from, int to){
        if(from != to){
            ValueAnimator animator = ValueAnimator.ofInt(from, to);
            animator.addUpdateListener(alphaUpdateListener);
            animator.setDuration(500L);
            animator.start();
        }
    }

    /**
     * 0 ~ 1 사이의 부동소수점을 이용하여 투명화를 적용한다.
     * @param offset
     */
    public void setTransparent(float offset){
        int value = (int)(offset * 0xFF);
        setAlphaValue(value < (float)0xFF * 0.2 ? 0x00 : value);
    }

    private ValueAnimator.AnimatorUpdateListener alphaUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            if(animation != null && animation.getAnimatedValue() != null && animation.getAnimatedValue() instanceof Integer){
                int value = (Integer) animation.getAnimatedValue();
                mBackround.setAlpha((float)value / (float)0xFF);
                mTitle.setAlpha((float)value / (float)0xFF);
            }
        }
    };
}
