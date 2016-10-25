package com.daejong.seoulpharm.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Hyunwoo on 2016. 10. 1..
 */
public class PharmItem {

    @SerializedName("MAIN_KEY")
    private String mainKey;
    @SerializedName("NAME_KOR")
    private String nameKor;
    @SerializedName("ADD_KOR_ROAD")
    private String addKorRoad;
    @SerializedName("H_KOR_CITY")
    private String hKorCity;
    @SerializedName("H_KOR_GU")
    private String hKorGu;
    @SerializedName("H_KOR_DONG")
    private String hKorDong;
    @SerializedName("TEL")
    private String tel;
    @SerializedName("AVAIL_LAN")
    private String availLan;

    // NOT API
    private String latitude;
    private String longtitude;

    // constructor
    public PharmItem() {

    }
    public PharmItem(String mainKey, String nameKor, String addKorRoad, String hKorCity, String hKorGu, String hKorDong, String tel, String availLan) {
        this.mainKey = mainKey;
        this.nameKor = nameKor;
        this.addKorRoad = addKorRoad;
        this.hKorCity = hKorCity;
        this.hKorGu = hKorGu;
        this.hKorDong = hKorDong;
        this.tel = tel;
        this.availLan = availLan;
    }

    // getter & setter
    public String getMainKey() {
        return mainKey;
    }

    public void setMainKey(String mainKey) {
        this.mainKey = mainKey;
    }

    public String getNameKor() {
        return nameKor;
    }

    public void setNameKor(String nameKor) {
        this.nameKor = nameKor;
    }

    public String getAddKorRoad() {
        return addKorRoad;
    }

    public void setAddKorRoad(String addKorRoad) {
        this.addKorRoad = addKorRoad;
    }

    public String gethKorCity() {
        return hKorCity;
    }

    public void sethKorCity(String hKorCity) {
        this.hKorCity = hKorCity;
    }

    public String gethKorGu() {
        return hKorGu;
    }

    public void sethKorGu(String hKorGu) {
        this.hKorGu = hKorGu;
    }

    public String gethKorDong() {
        return hKorDong;
    }

    public void sethKorDong(String hKorDong) {
        this.hKorDong = hKorDong;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getAvailLan() {
        return availLan;
    }

    public void setAvailLan(String availLan) {
        this.availLan = availLan;
    }

    public String getLatitude() {
        return this.latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongtitude() {
        return this.longtitude;
    }

    public void setLongtitude(String longtitude) {
        this.longtitude = longtitude;
    }

}
