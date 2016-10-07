package com.fivetrue.gimpo.ac05.ui.fragment;

import android.graphics.Color;

import com.fivetrue.fivetrueandroid.ui.fragment.BaseFragment;
import com.fivetrue.gimpo.ac05.R;

/**
 * Created by kwonojin on 16. 6. 21..
 */
public abstract class ColorChooserFragment extends BaseFragment {

    abstract protected int getPageTitleColor();

    abstract protected int getPageTitleBgColor();

    abstract protected int getPageContentColor();

    abstract protected int getPageContentBgColor();

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
}
