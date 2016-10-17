package com.fivetrue.gimpo.ac05.vo.config;

import java.util.ArrayList;

/**
 * Created by kwonojin on 16. 3. 14..
 */
public class AppConfig {


    public String appId;

    public int appVersionCode = 0;
    public String appSecretKey = null;
    public String appMarketUrl = null;
    public String senderId = null;

    /**
     * Naver Cafe Info
     */
    public String clubId = null;
    public String clubUrl = null;
    public String clubMyInfo = null;

    /**
     * Firebase Info
     */

    public String fDatabaseUrl;
    public String fStorageUrl;

    public int forceUpdate = 0;

    public String adminUrl;
    public String defaultImageUrl;



    public int getAppVersionCode() {
        return appVersionCode;
    }

    public String getAppMarketUrl() {
        return appMarketUrl;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getClubUrl() {
        return clubUrl;
    }

    public String getClubMyInfo() {
        return clubMyInfo;
    }

    public int getForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(int forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    public String getAdminUrl() {
        return adminUrl;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public void setAppVersionCode(int appVersionCode) {
        this.appVersionCode = appVersionCode;
    }

    public String getAppSecretKey() {
        return appSecretKey;
    }

    public void setAppSecretKey(String appSecretKey) {
        this.appSecretKey = appSecretKey;
    }

    public void setAppMarketUrl(String appMarketUrl) {
        this.appMarketUrl = appMarketUrl;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getClubId() {
        return clubId;
    }

    public void setClubId(String clubId) {
        this.clubId = clubId;
    }

    public void setClubUrl(String clubUrl) {
        this.clubUrl = clubUrl;
    }

    public void setClubMyInfo(String clubMyInfo) {
        this.clubMyInfo = clubMyInfo;
    }

    public String getfDatabaseUrl() {
        return fDatabaseUrl;
    }

    public void setfDatabaseUrl(String fDatabaseUrl) {
        this.fDatabaseUrl = fDatabaseUrl;
    }

    public String getfStorageUrl() {
        return fStorageUrl;
    }

    public void setfStorageUrl(String fStorageUrl) {
        this.fStorageUrl = fStorageUrl;
    }

    public void setAdminUrl(String adminUrl) {
        this.adminUrl = adminUrl;
    }

    public String getDefaultImageUrl() {
        return defaultImageUrl;
    }

    public void setDefaultImageUrl(String defaultImageUrl) {
        this.defaultImageUrl = defaultImageUrl;
    }
}
