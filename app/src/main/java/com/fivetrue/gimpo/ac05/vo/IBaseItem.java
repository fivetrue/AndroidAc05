package com.fivetrue.gimpo.ac05.vo;

/**
 * Created by kwonojin on 2016. 10. 4..
 */

public interface IBaseItem {

    long ONE_DAY = 1000 * 60 * 60 * 24;

    String getImageUrl();

    String getTitle();

    String getContent();

    String getSubContent();

    long getTime();

    String getUrl();

    boolean isShowingNew();
}
