package com.fivetrue.gimpo.ac05.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.fivetrue.gimpo.ac05.firebase.database.ScrapContentDatabase;
import com.fivetrue.gimpo.ac05.firebase.model.ScrapContent;
import com.fivetrue.gimpo.ac05.ui.ScrapContentActivity;
import com.fivetrue.gimpo.ac05.ui.SplashActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by kwonojin on 2016. 10. 31..
 */

public class DeeplinkUtil {


    public enum Host{
        Scrap, TownNews
    }

    private static final String TAG = "DeeplinkUtil";
    public static final String SCHEME = "gimpoac05";



    public static void goPage(final Activity activity, final Intent receivedIntent){
        if(receivedIntent != null
                && receivedIntent.getData() != null
                && SCHEME.equals(receivedIntent.getData().getScheme())){
            Uri uri = receivedIntent.getData();
            String host = uri.getHost();
            switch (Host.valueOf(host)){
                case Scrap:
                {
                    String key = uri.getQueryParameter("key");
                    new ScrapContentDatabase().getReference().child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            ScrapContent content = dataSnapshot.getValue(ScrapContent.class);
                            content.key = dataSnapshot.getKey();
                            Intent intent = new Intent(activity, ScrapContentActivity.class);
                            intent.putExtra(ScrapContent.class.getName(), content);
                            activity.startActivity(intent);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            goSplash(activity, receivedIntent);
                        }
                    });
                }
                    break;

            }
        }else{
            goSplash(activity, receivedIntent);
        }
    }

    private static void goSplash(Activity activity, Intent receivedIntent){
        Intent intent = new Intent(receivedIntent);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setClass(activity, SplashActivity.class);
        activity.startActivity(intent);
    }

    public static String makeLink(Host host, String... parameterSet){
        String link = null;
        if(host != null && parameterSet != null){
            link = SCHEME + "://";
            link += host.name();
            for(String string : parameterSet){
                if(!link.contains("?")){
                    link += "?" + string;
                }else{
                    link += "&" + string;
                }
            }
        }
        return link;
    }
}
