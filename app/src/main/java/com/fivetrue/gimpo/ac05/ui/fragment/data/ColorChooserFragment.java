package com.fivetrue.gimpo.ac05.ui.fragment.data;

import android.content.Context;
import android.graphics.Color;

import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.rss.FeedMessage;
import com.fivetrue.gimpo.ac05.ui.fragment.BaseFragment;

/**
 * Created by kwonojin on 16. 6. 21..
 */
public abstract class ColorChooserFragment extends BaseFragment{

    public interface OnPageDataClickListener{
        void onClickPageData(FeedMessage message, Integer textColor, Integer bgColor);
    }

    private OnPageDataClickListener mOnPageDataClickListener = null;

    abstract protected int getPageTitleColor();

    abstract protected int getPageTitleBgColor();

    abstract protected int getPageContentColor();

    abstract protected int getPageContentBgColor();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(getActivity() != null && getActivity() instanceof OnPageDataClickListener){
            mOnPageDataClickListener = (OnPageDataClickListener) getActivity();
        }
    }

    protected int parseColor(String color){
        int c = Color.WHITE;
        if(getActivity() != null){
            c = getResources().getColor(R.color.colorAccent);
            if(color != null){
                c = Color.parseColor(color);
            }
        }

        return c;
    }

    protected void onClickPageData(FeedMessage message, Integer textColor, Integer bgColor){
        if(mOnPageDataClickListener != null){
            mOnPageDataClickListener.onClickPageData(message, textColor, bgColor);
        }
    }
}
