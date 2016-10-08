package com.fivetrue.gimpo.ac05;

/**
 * Created by kwonojin on 16. 6. 2..
 */
public class Constants {

    public static final String API_SERVER_HOST = "http://52.78.61.23/AC05";  //AWS
//    public static final String API_SERVER_HOST = "http://192.168.219.138:8080/gimpo-ac05";
//    public static final String API_SERVER_HOST = "http://192.168.43.126:8080/gimpo-ac05";
//    public static final String API_SERVER_HOST = "http://10.10.5.172:8080/gimpo-ac05";

    public static final String API_CHECK_REDIRECT = API_SERVER_HOST + "/push/check?redirect=%s&id=%s&email=%s";

    public static final String DEFAULT_DEFAULT_IMAGE_URL = "https://firebasestorage.googleapis.com/v0/b/gimpo-1334.appspot.com/o/images%2Fdefault%2Fmain_visual.jpg?alt=media&token=22749f39-f93d-4646-8977-5ec3acd578d2";
    public static final String DEFAULT_GIMPO_BI_IMAGE_URL = "https://firebasestorage.googleapis.com/v0/b/gimpo-1334.appspot.com/o/images%2Fdefault%2Fgimpo_ci.jpg?alt=media&token=92f66124-1c81-4809-9800-35b3c44221c6";
    public static final String DEFAULT_TOWN_BI_IMAGE_URL = "https://firebasestorage.googleapis.com/v0/b/gimpo-1334.appspot.com/o/images%2Fdefault%2Ftown_bi.jpg?alt=media&token=703740b1-bf93-4c4f-975e-940c8eb80ea9";

}