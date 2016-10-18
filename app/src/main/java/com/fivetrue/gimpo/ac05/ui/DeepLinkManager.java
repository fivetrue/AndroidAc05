package com.fivetrue.gimpo.ac05.ui;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.android.volley.VolleyError;
import com.fivetrue.fivetrueandroid.net.BaseApiResponse;
import com.fivetrue.fivetrueandroid.net.ErrorCode;
import com.fivetrue.fivetrueandroid.net.NetworkManager;
import com.fivetrue.fivetrueandroid.ui.BaseActivity;
import com.fivetrue.gimpo.ac05.chatting.FirebaseChattingService;
import com.fivetrue.gimpo.ac05.net.request.MainPageDataRequest;
import com.fivetrue.gimpo.ac05.service.notification.NotificationHelper;
import com.fivetrue.gimpo.ac05.vo.data.MainDataEntry;
import com.fivetrue.gimpo.ac05.vo.data.TownDataEntry;
import com.fivetrue.gimpo.ac05.vo.notification.NotificationData;

/**
 * Created by kwonojin on 2016. 10. 13..
 */

public class DeepLinkManager {

    private static final String TAG = "DeepLinkManager";

    public static final String APP_SCHEME = "gimpoac05";
    public static final String HTTP_SCHEME = "http";
    public static final String HTTPS_SCHEME = "https";

    public static final String[] AVALIABLE_SCHEME = {APP_SCHEME, HTTP_SCHEME, HTTPS_SCHEME};

    public static final String NOTIFICATION_HOST = "notification";
    public static final String CHATTING_HOST = "chatting";

    public static final String PARAM_URL = "url";
    public static final String PARAM_TITLE = "title";


    public static void goLink(final BaseActivity activity, Intent intent){
        if(activity != null && intent != null){
            Uri uri = intent.getData();
            NotificationData notificationData = intent.getParcelableExtra(NotificationHelper.KEY_NOTIFICATION_PARCELABLE);
            if(uri != null){
                String scheme = uri.getScheme();
                String host = uri.getHost();
                String path = uri.getPath();

                String title = uri.getQueryParameter(PARAM_TITLE);
                if(title == null && notificationData != null){
                    title = notificationData.getTitle();
                }
                if(checkAvaliableScheme(scheme)){
                    if(isNetworkScheme(scheme)){

                        Intent i = new Intent(activity, WebViewActivity.class);
                        i.putExtra("title", title);
                        i.putExtra("url", uri.toString());
                        activity.startActivity(i);
                    }else if(isAppScheme(scheme)){
                        if(host.equals(NOTIFICATION_HOST)){
                            String url = uri.getQueryParameter(PARAM_URL);
                            if(url != null && url.startsWith("http")){
                                Intent i = new Intent(activity, WebViewActivity.class);
                                i.putExtra("title", title);
                                i.putExtra("url", url);
                                activity.startActivity(i);
                            }else{
                                MainPageDataRequest request = new MainPageDataRequest(activity, new BaseApiResponse.OnResponseListener<MainDataEntry>() {
                                    @Override
                                    public void onResponse(BaseApiResponse<MainDataEntry> response) {
                                        if(response != null){
                                            if(response.getErrorCode() == ErrorCode.OK){
                                                TownDataEntry entry = response.getData().getTown();
                                                if(entry != null){
                                                    Intent i = new Intent(activity, TownDataListActivity.class);
                                                    i.putExtra("title", entry.getTitle());
                                                    i.putExtra(TownDataEntry.class.getName(), entry);
                                                    activity.startActivity(i);
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onError(VolleyError error) {

                                    }
                                });
                                request.setCache(false);
                                NetworkManager.getInstance().request(request);
                            }
                        }else if(host.equals(CHATTING_HOST)){
                            String type = uri.getQueryParameter("type");
                            if(type != null){
                                int t = FirebaseChattingService.PUBLIC_CHATTING_NOTIFICATION_ID;
                                try{
                                   t = Integer.parseInt(type.trim());
                                }catch(NumberFormatException e){
                                    Log.w(TAG, "goLink: ", e);
                                }finally {
                                    switch (t){
                                        case FirebaseChattingService.PUBLIC_CHATTING_NOTIFICATION_ID :
                                        case FirebaseChattingService.DISTRICT_CHATTING_NOTIFICATION_ID :{
                                            Intent i = new Intent(activity, ChattingActivity.class);
                                            i.putExtra("type", t);
                                            activity.startActivity(i);
                                        }
                                            break;

                                        case FirebaseChattingService.GALLERY_NOTIFICATION_ID:{
                                            Intent i = new Intent(activity, GalleryActivity.class);
                                            i.putExtra("type", t);
                                            activity.startActivity(i);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static boolean checkAvaliableScheme(String scheme){
        for(String s : AVALIABLE_SCHEME){
            if(s.equals(scheme)){
                return true;
            }
        }
        return false;
    }

    private static boolean isNetworkScheme(String scheme){
        if(scheme != null){
            return scheme.equals(HTTP_SCHEME) || scheme.equals(HTTPS_SCHEME);
        }

        return false;
    }
    private static boolean isAppScheme(String scheme){
        if(scheme != null){
            return scheme.equals(APP_SCHEME);
        }
        return false;
    }

    public static Uri makeChattingLink(int type){
        String uri = APP_SCHEME + "://" + CHATTING_HOST + "?type=" + type;
        return Uri.parse(uri);
    }
}
