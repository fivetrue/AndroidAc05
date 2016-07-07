package com.fivetrue.gimpo.ac05.net.request;

import android.content.Context;

import com.fivetrue.gimpo.ac05.Constants;
import com.fivetrue.gimpo.ac05.net.BaseApiResponse;

/**
 * Created by kwonojin on 16. 3. 29..
 */
public class UpdateUserDistrictRequest extends BasicRequest {

    private static final String TAG = "UpdateUserDistrictRequest";

    private static final String API = Constants.API_SERVER_HOST + "/api/user/update/district";


    public UpdateUserDistrictRequest(Context context, BaseApiResponse response) {
        super(context, API, response);
    }


}
