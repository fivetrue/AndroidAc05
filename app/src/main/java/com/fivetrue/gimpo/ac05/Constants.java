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
}