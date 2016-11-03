package com.fivetrue.gimpo.ac05.vo.data;

import android.app.Activity;

import com.fivetrue.gimpo.ac05.ui.adapter.BaseItemListAdapter;
import com.fivetrue.gimpo.ac05.vo.IBaseItem;

import java.util.List;

/**
 * Created by kwonojin on 2016. 10. 27..
 */

public class MainItem {

    public final String title;
    public final String subTitle;
    public final Class<? extends Activity> targetClass;
    public BaseItemListAdapter<?> adapter;


    public MainItem(String title, String subTitle, Class<? extends Activity> targetClass, BaseItemListAdapter<?> adapter){
        this.title = title;
        this.subTitle = subTitle;
        this.targetClass = targetClass;
        this.adapter = adapter;
    }
}
