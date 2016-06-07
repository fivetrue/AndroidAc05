/*
 * Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fivetrue.gimpo.ac05.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.ui.adapter.BasePagerAdapter;

import java.util.List;
import java.util.Locale;

public class PagerSlidingTabStrip extends HorizontalScrollView {

    private static final String TAG = PagerSlidingTabStrip.class.getSimpleName();

    public interface PagerTabContent {
        int getStringResource();
        int getIconResource();
        boolean isShowingIcon();
    }

    public interface OnPageChangeCompleteListener {
        void onPageScrollStateChanged(int state);
    }


    public interface IconTabProvider {
        public int getPageIconResId(int position);
    }

    private LinearLayout.LayoutParams defaultTabLayoutParams;
    private LinearLayout.LayoutParams expandedTabLayoutParams;

    private final PageListener pageListener = new PageListener();
    public OnPageChangeListener delegatePageListener;

    private LinearLayout tabsContainer;
    private ViewPager pager;

    private List<PagerTabContent> mTabContentList = null;

    public boolean isRefresh = false;

    private int currentPosition = 0;
    private float currentPositionOffset = 0f;

    private Paint mPaintIndicator = null;
    private Paint mPaintBackround = null;

    private boolean checkedTabWidths = false;

    private int indicatorColor = 0xFF3386ed;

    private boolean shouldExpand = false;
    private boolean textAllCaps = true;
    private boolean textOutline = false;

    private int scrollOffset = 52;
    private int indicatorHeight = 3;
    private int dividerMargin = 12;
    private int tabPadding = 10;
    private int dividerWidth = 1;

    private int tabTextSize = 15;
    private int selectTextColor = 0xffffffff;
    private int tabTextColor = 0xccffffff;
    private Typeface tabTypeface = Typeface.SANS_SERIF;
    private int tabTypefaceStyle = Typeface.NORMAL;
    private int selectTabTypefaceStyle = Typeface.BOLD;

    private int lastScrollX = 0;

    private int tabBackgroundColor = android.R.color.transparent;

    private OnPageChangeCompleteListener onPageChangeCompleteListener = null;

    private Locale locale;

    public PagerSlidingTabStrip(Context context) {
        this(context, null);
    }

    public PagerSlidingTabStrip(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PagerSlidingTabStrip(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setFillViewport(true);
        setWillNotDraw(false);

        DisplayMetrics dm = getResources().getDisplayMetrics();

        scrollOffset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, scrollOffset, dm);
        indicatorHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, indicatorHeight, dm);
        dividerMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerMargin, dm);
        tabPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, tabPadding, dm);
        dividerWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerWidth, dm);
        tabTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, tabTextSize, dm);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PagerSlidingTabStrip);
        indicatorColor = a.getColor(R.styleable.PagerSlidingTabStrip_indicatorColor, indicatorColor);
        indicatorHeight = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_indicatorHeight, indicatorHeight);
        dividerMargin = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_dividerMargin, dividerMargin);
        tabPadding = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_tabsPadding, tabPadding);
        tabBackgroundColor = a.getColor(R.styleable.PagerSlidingTabStrip_tabsBackgroundColor, getResources().getColor(tabBackgroundColor));
        shouldExpand = a.getBoolean(R.styleable.PagerSlidingTabStrip_shouldExpand, shouldExpand);
        scrollOffset = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_scrollOffset, scrollOffset);
        textAllCaps = a.getBoolean(R.styleable.PagerSlidingTabStrip_tabTextAllCaps, textAllCaps);
        textOutline = a.getBoolean(R.styleable.PagerSlidingTabStrip_tabsTextOutline, textOutline);
        tabTextSize = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_tabsTextSize, tabTextSize);
        tabTextColor = a.getColor(R.styleable.PagerSlidingTabStrip_tabsTextColor, tabTextColor);
        a.recycle();

        tabsContainer = new LinearLayout(context);
        tabsContainer.setOrientation(LinearLayout.HORIZONTAL);
        tabsContainer.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        addView(tabsContainer);

        mPaintIndicator = new Paint();
        mPaintIndicator.setAntiAlias(true);
        mPaintIndicator.setColor(indicatorColor);
        mPaintIndicator.setStyle(Paint.Style.FILL);

        mPaintBackround = new Paint();
        mPaintBackround.setAntiAlias(true);
        mPaintBackround.setColor(tabBackgroundColor);
        mPaintBackround.setStyle(Paint.Style.FILL);

        defaultTabLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        expandedTabLayoutParams = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);

        if (locale == null) {
            locale = getResources().getConfiguration().locale;
        }
    }

    public void setViewPager(ViewPager pager) {
        this.pager = pager;

        if (pager.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }

        pager.setOnPageChangeListener(pageListener);

        notifyDataSetChanged();
    }

    public void setEnableItem(int position, boolean enabled){
        if(position < 0 && position > mTabContentList.size())
            return;
        tabsContainer.getChildAt(position).setEnabled(enabled);
    }

    /**
     * @return the tabsContainer
     */
    public LinearLayout getTabsContainer() {
        return tabsContainer;
    }

    public TextView getTextViewAt(int position){
        return (TextView)tabsContainer.getChildAt(position);
    }

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        this.delegatePageListener = listener;
    }

    public void notifyDataSetChanged() {
        tabsContainer.removeAllViews();
        if(pager != null && pager.getAdapter() != null && pager.getAdapter() instanceof BasePagerAdapter){
            BasePagerAdapter adapter = (BasePagerAdapter) pager.getAdapter();
            for (int i = 0; i < adapter.getCount(); i++) {
                if(adapter.getItem(i) instanceof PagerTabContent){
                    PagerTabContent content = (PagerTabContent) adapter.getItem(i);
                    if(content.isShowingIcon()){
                        addIconTab(i, content.getIconResource());
                    }else{
                        addTextTab(i, content.getStringResource());
                    }
                }
            }

            updateTabStyles();

            checkedTabWidths = false;

            getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

                @SuppressWarnings("deprecation")
                @SuppressLint("NewApi")
                @Override
                public void onGlobalLayout() {

                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }

                    currentPosition = pager.getCurrentItem();
                    scrollToChild(currentPosition, 0);
                }
            });
        }
    }

    class HoverTextView extends TextView{

        public HoverTextView(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if(textOutline){
                for(int i = 0 ; i < 5 ; i ++){
                    super.onDraw(canvas);
                }
            }else{
                super.onDraw(canvas);
            }
        }
    }

    private void addTextTab(final int position, int stringResource) {

        HoverTextView tab = new HoverTextView(getContext());
        tab.setText(stringResource);
        tab.setFocusable(true);
        tab.setGravity(Gravity.CENTER);
        tab.setSingleLine();
        if(textOutline){
            tab.setShadowLayer(5, 0, 0, Color.BLACK);
        }

        tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(position);
                pager.setTag("click");
            }
        });

        tabsContainer.addView(tab);
    }

    private void addIconTab(final int position, int resId) {

        ImageButton tab = new ImageButton(getContext());
        tab.setFocusable(true);
        tab.setImageResource(resId);

        tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(position);
            }
        });

        tabsContainer.addView(tab);

    }

    private void updateTabStyles() {

        for (int i = 0; i < getCount(); i++) {

            View v = tabsContainer.getChildAt(i);
            v.setLayoutParams(defaultTabLayoutParams);
//            v.setBackgroundResource(tabBackgroundColor);
            if (shouldExpand) {
                v.setPadding(0, 0, 0, 0);
            } else {
                if(i == 0){
                    v.setPadding(tabPadding + tabPadding, 0, tabPadding, 0);
                }else{
                    v.setPadding(tabPadding, 0, tabPadding, 0);
                }
            }

            if (v instanceof TextView) {

                TextView tab = (TextView) v;
                tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTextSize);
                tab.setTypeface(tabTypeface, tabTypefaceStyle);
                tab.setTextColor(tabTextColor);
//				tab.setMinWidth(tabMinWidth);

                // setAllCaps() is only available from API 14, so the upper case is made manually if we are on a
                // pre-ICS-build
//				if (textAllCaps) {
//					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
//						tab.setAllCaps(true);
//					} else {
//						tab.setText(tab.getText().toString().toUpperCase(locale));
//					}
//				}
            }
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (!shouldExpand || MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.UNSPECIFIED) {
            return;
        }

        int myWidth = getMeasuredWidth();
        int childWidth = 0;
        for (int i = 0; i < getCount(); i++) {
            childWidth += tabsContainer.getChildAt(i).getMeasuredWidth();
        }

        if (!checkedTabWidths && childWidth > 0 && myWidth > 0) {

            if (childWidth <= myWidth) {
                for (int i = 0; i < getCount(); i++) {
                    tabsContainer.getChildAt(i).setLayoutParams(expandedTabLayoutParams);
                }
            }

            checkedTabWidths = true;
        }
    }

    public int getCount(){
        int count = 0;
        if(pager != null && pager.getAdapter() != null){
            count = pager.getAdapter().getCount();
        }
        return count;
    }

    private void scrollToChild(int position, int offset) {

        if (getCount() == 0) {
            return;
        }

        int newScrollX = tabsContainer.getChildAt(position).getLeft() + offset;

        if (position > 0 || offset > 0) {
            newScrollX -= scrollOffset;
        }

        if (newScrollX != lastScrollX) {
            lastScrollX = newScrollX;
            scrollTo(newScrollX, 0);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isInEditMode() || getCount() == 0) {
            return;
        }

        int height = getHeight();

        // draw indicator line

        // default: line below current tab
        View currentTab = tabsContainer.getChildAt(currentPosition);
        float lineLeft = currentTab.getLeft();
        float lineRight = currentTab.getRight();

        // if there is an offset, start interpolating left and right coordinates between current and next tab
        if (currentPositionOffset > 0f && currentPosition < getCount() - 1) {

            View nextTab = tabsContainer.getChildAt(currentPosition + 1);
            final float nextTabLeft = nextTab.getLeft();
            final float nextTabRight = nextTab.getRight();

            lineLeft = (currentPositionOffset * nextTabLeft + (1f - currentPositionOffset) * lineLeft);
            lineRight = (currentPositionOffset * nextTabRight + (1f - currentPositionOffset) * lineRight);
        }

        canvas.drawRect(0, 0, getWidth(), getHeight(), mPaintBackround);
        canvas.drawRect(lineLeft, height - indicatorHeight, lineRight, height, mPaintIndicator);
//		dividerPaint.setColor(dividerColor);
//		for (int i = 0; i < tabCount - 1; i++) {
//			View tab = tabsContainer.getChildAt(i);
//			canvas.drawLine(tab.getRight(), dividerMargin, tab.getRight(), height - dividerMargin, dividerPaint);
//		}
    }

    private void selectTabTextColor(int position){
        for (int i = 0; i < tabsContainer.getChildCount(); i++) {
            TextView tabText = (TextView) tabsContainer.getChildAt(i);
            if(i == position){
                tabText.setTextColor(selectTextColor);
                tabText.setTypeface(null, selectTabTypefaceStyle);
            } else {
                tabText.setTextColor(tabTextColor);
                tabText.setTypeface(null, tabTypefaceStyle);
            }
        }
    }

    private class PageListener implements OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            currentPosition = position;
            currentPositionOffset = positionOffset;

            if(tabsContainer.getChildAt(position) == null){
                return ;
            }

            selectTabTextColor(position);
            scrollToChild(position, (int) (positionOffset * tabsContainer.getChildAt(position).getWidth()));

            invalidate();

            if (delegatePageListener != null) {
                delegatePageListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageScrollStateChanged(final int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE) {
            }

            if (delegatePageListener != null) {
                delegatePageListener.onPageScrollStateChanged(state);
            }
        }

        @Override
        public void onPageSelected(int position) {
            if (delegatePageListener != null) {
                delegatePageListener.onPageSelected(position);
            }
        }

    }

    public void setIndicatorColor(int indicatorColor) {
        this.indicatorColor = indicatorColor;
        invalidate();
    }

    public void setIndicatorColorResource(int resId) {
        this.indicatorColor = getResources().getColor(resId);
        invalidate();
    }

    public int getIndicatorColor() {
        return this.indicatorColor;
    }

    public void setIndicatorHeight(int indicatorLineHeightPx) {
        this.indicatorHeight = indicatorLineHeightPx;
        invalidate();
    }

    public int getIndicatorHeight() {
        return indicatorHeight;
    }

    public void setDividerMargin(int dividerPaddingPx) {
        this.dividerMargin = dividerPaddingPx;
        invalidate();
    }

    public int getDividerMargin() {
        return dividerMargin;
    }

    public void setScrollOffset(int scrollOffsetPx) {
        this.scrollOffset = scrollOffsetPx;
        invalidate();
    }

    public int getScrollOffset() {
        return scrollOffset;
    }

    public void setShouldExpand(boolean shouldExpand) {
        this.shouldExpand = shouldExpand;
        requestLayout();
    }

    public boolean getShouldExpand() {
        return shouldExpand;
    }

    public boolean isTextAllCaps() {
        return textAllCaps;
    }

    public void setAllCaps(boolean textAllCaps) {
        this.textAllCaps = textAllCaps;
    }

    public void setTextSize(int textSizePx) {
        this.tabTextSize = textSizePx;
        updateTabStyles();
    }

    public int getTextSize() {
        return tabTextSize;
    }

    public void setTextColor(int textColor) {
        this.tabTextColor = textColor;
        updateTabStyles();
    }

    public void setTextColorResource(int resId) {
        this.tabTextColor = getResources().getColor(resId);
        updateTabStyles();
    }

    public int getTextColor() {
        return tabTextColor;
    }

    public void setTypeface(Typeface typeface, int style) {
        this.tabTypeface = typeface;
        this.tabTypefaceStyle = style;
        updateTabStyles();
    }

    public void setTabBackground(int resId) {
        this.tabBackgroundColor = resId;
    }

    public int getTabBackground() {
        return tabBackgroundColor;
    }

    public void setTabPaddingLeftRight(int paddingPx) {
        this.tabPadding = paddingPx;
        updateTabStyles();
    }

    public int getTabPaddingLeftRight() {
        return tabPadding;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        currentPosition = savedState.currentPosition;
        requestLayout();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.currentPosition = currentPosition;
        return savedState;
    }

    static class SavedState extends BaseSavedState {
        int currentPosition;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            currentPosition = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(currentPosition);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    public OnPageChangeCompleteListener getOnPageChangeCompleteListener() {
        return onPageChangeCompleteListener;
    }

    public void setOnPageChangeCompleteListener(
            OnPageChangeCompleteListener onPageChangeCompleteListener) {
        this.onPageChangeCompleteListener = onPageChangeCompleteListener;
    }

    public int getSelectTextColor() {
        return selectTextColor;
    }

    public void setSelectTextColor(int selectTextColor) {
        this.selectTextColor = selectTextColor;
    }
}

