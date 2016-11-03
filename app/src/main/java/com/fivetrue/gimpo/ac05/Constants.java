package com.fivetrue.gimpo.ac05;

/**
 * Created by kwonojin on 16. 6. 2..
 */
public class Constants {

    public static final String API_SERVER_HOST = "http://52.78.61.23/AC05-TEST";  //AC05-TEST
//    public static final String API_SERVER_HOST = "http://52.78.61.23/AC05";  //AC05
//    public static final String API_SERVER_HOST = "http://192.168.219.138:8080/gimpo-ac05";
//    public static final String API_SERVER_HOST = "http://192.168.43.126:8080/gimpo-ac05";
//    public static final String API_SERVER_HOST = "http://10.10.5.172:8080/gimpo-ac05";

    public static final String API_CHECK_REDIRECT = API_SERVER_HOST + "/push/check?redirect=%s&id=%s&email=%s";

    public static final String ACTION_NOTIFICATION = "com.fivetrue.gimpo.ac05.notification";
    public static final String ACTION_UPDATE_CAPTURED_PAGE = "com.fivetrue.gimpo.ac05.update.captured";



    public static final String DEFAULT_DEFAULT_IMAGE_URL = "https://firebasestorage.googleapis.com/v0/b/project-2283695982257928447.appspot.com/o/images%2Fdefault%2Fmain_visual.jpg?alt=media&token=1fe7c02e-ac51-43e0-9200-c95e82d3be21";
    public static final String DEFAULT_GIMPO_BI_IMAGE_URL = "https://firebasestorage.googleapis.com/v0/b/project-2283695982257928447.appspot.com/o/images%2Fdefault%2Fgimpo_ci.jpg?alt=media&token=1112cad5-f1e5-4bc0-9f34-2a49f2528c10";
    public static final String DEFAULT_TOWN_BI_IMAGE_URL = "https://firebasestorage.googleapis.com/v0/b/project-2283695982257928447.appspot.com/o/images%2Fdefault%2Ftown_bi.jpg?alt=media&token=717cd166-59bc-47ab-a093-7d11ca588434";

    public static final int PUBLIC_CHATTING_ID = 0x123;
    public static final int DISTRICT_CHATTING_ID = 0x312;
    public static final int PERSON_MESSAGE_ID = 0x412;
    public static final int SCRAP_CONTENT_ID = 0x533;
    public static final int TOWN_NEWS_CONTENT_ID = 0x543;

    public static final int NOTIFY_MESSASGE_ID = 0x633;

    public static final String JS_INTERFACE_NAME = "Gimpo";

    /**
     * Person.
     */
    public static final String FIREBASE_DB_ROOT_BAD_USER = "badUser";

    /**
     * Config
     */
    public static final String FIREBASE_DB_ROOT_CONFIG = "config";
}