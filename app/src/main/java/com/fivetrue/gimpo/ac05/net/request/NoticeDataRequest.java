package com.fivetrue.gimpo.ac05.net.request;

import android.content.Context;

import com.fivetrue.gimpo.ac05.Constants;
import com.fivetrue.gimpo.ac05.net.BaseApiResponse;

/**
 * Created by kwonojin on 16. 3. 19..
 */
public class NoticeDataRequest extends BasicRequest {

    private static final String API = Constants.API_SERVER_HOST + "/api/data/noti";


    public NoticeDataRequest(Context context, BaseApiResponse response) {
        super(context, API, response);
    }
}
