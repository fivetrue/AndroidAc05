package com.fivetrue.gimpo.ac05.vo.config;

/**
 * Created by kwonojin on 16. 3. 14..
 */
public class AppConfig {

    private String appId = null;
    private String appSercureKey = null;
    private String appLatestVersion = null;
    private String appVersionName = null;
    private String appMarketUrl = null;
    private String senderId = null;

    private String naverClientId = null;
    private String naverClientSecret = null;


    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppSercureKey() {
        return appSercureKey;
    }

    public void setAppSercureKey(String appSercureKey) {
        this.appSercureKey = appSercureKey;
    }

    public String getAppLatestVersion() {
        return appLatestVersion;
    }

    public void setAppLatestVersion(String appLatestVersion) {
        this.appLatestVersion = appLatestVersion;
    }

    public String getAppVersionName() {
        return appVersionName;
    }

    public void setAppVersionName(String appVersionName) {
        this.appVersionName = appVersionName;
    }

    public String getAppMarketUrl() {
        return appMarketUrl;
    }

    public void setAppMarketUrl(String appMarketUrl) {
        this.appMarketUrl = appMarketUrl;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getNaverClientId() {
        return naverClientId;
    }

    public void setNaverClientId(String naverClientId) {
        this.naverClientId = naverClientId;
    }

    public String getNaverClientSecret() {
        return naverClientSecret;
    }

    public void setNaverClientSecret(String naverClientSecret) {
        this.naverClientSecret = naverClientSecret;
    }

    @Override
    public String toString() {
        return "AppConfig{" +
                "appId='" + appId + '\'' +
                ", appSercureKey='" + appSercureKey + '\'' +
                ", appLatestVersion='" + appLatestVersion + '\'' +
                ", appVersionName='" + appVersionName + '\'' +
                ", appMarketUrl='" + appMarketUrl + '\'' +
                ", senderId='" + senderId + '\'' +
                ", naverClientId='" + naverClientId + '\'' +
                ", naverClientSecret='" + naverClientSecret + '\'' +
                '}';
    }
}
