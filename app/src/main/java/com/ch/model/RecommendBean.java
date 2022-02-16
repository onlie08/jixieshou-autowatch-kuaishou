package com.ch.model;

import android.os.Parcel;
import android.os.Parcelable;

public class RecommendBean implements Parcelable {
    private String recommendCode;
    private String code_toutiao;
    private String code_douyin;
    private String code_kuaishou;
    private String code_diantao;
    private String code_baidu;
    private String code_aiqiyi;
    private String code_meitianzhuandian;
    private String code_meituan;
    private String code_eleme;
    private String code_huoshan;
    private String code_fanqie;
    private String code_taote;
    private String code_jingdong;

    public String getCode_taote() {
        return code_taote;
    }

    public void setCode_taote(String code_taote) {
        this.code_taote = code_taote;
    }

    public String getCode_jingdong() {
        return code_jingdong;
    }

    public void setCode_jingdong(String code_jingdong) {
        this.code_jingdong = code_jingdong;
    }

    public String getCode_fanqie() {
        return code_fanqie;
    }

    public void setCode_fanqie(String code_fanqie) {
        this.code_fanqie = code_fanqie;
    }

    public String getRecommendCode() {
        return recommendCode;
    }

    public void setRecommendCode(String recommendCode) {
        this.recommendCode = recommendCode;
    }

    public String getCode_meitianzhuandian() {
        return code_meitianzhuandian;
    }

    public void setCode_meitianzhuandian(String code_meitianzhuandian) {
        this.code_meitianzhuandian = code_meitianzhuandian;
    }

    public String getCode_toutiao() {
        return code_toutiao;
    }

    public String getCode_huoshan() {
        return code_huoshan;
    }

    public void setCode_huoshan(String code_huoshan) {
        this.code_huoshan = code_huoshan;
    }

    public void setCode_toutiao(String code_toutiao) {
        this.code_toutiao = code_toutiao;
    }

    public String getCode_douyin() {
        return code_douyin;
    }

    public void setCode_douyin(String code_douyin) {
        this.code_douyin = code_douyin;
    }

    public String getCode_kuaishou() {
        return code_kuaishou;
    }

    public void setCode_kuaishou(String code_kuaishou) {
        this.code_kuaishou = code_kuaishou;
    }

    public String getCode_diantao() {
        return code_diantao;
    }

    public void setCode_diantao(String code_diantao) {
        this.code_diantao = code_diantao;
    }

    public String getCode_baidu() {
        return code_baidu;
    }

    public void setCode_baidu(String code_baidu) {
        this.code_baidu = code_baidu;
    }

    public String getCode_aiqiyi() {
        return code_aiqiyi;
    }

    public void setCode_aiqiyi(String code_aiqiyi) {
        this.code_aiqiyi = code_aiqiyi;
    }

    public String getCode_meituan() {
        return code_meituan;
    }

    public void setCode_meituan(String code_meituan) {
        this.code_meituan = code_meituan;
    }

    public String getCode_eleme() {
        return code_eleme;
    }

    public void setCode_eleme(String code_eleme) {
        this.code_eleme = code_eleme;
    }

    public RecommendBean() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.recommendCode);
        dest.writeString(this.code_toutiao);
        dest.writeString(this.code_douyin);
        dest.writeString(this.code_kuaishou);
        dest.writeString(this.code_diantao);
        dest.writeString(this.code_baidu);
        dest.writeString(this.code_aiqiyi);
        dest.writeString(this.code_meitianzhuandian);
        dest.writeString(this.code_meituan);
        dest.writeString(this.code_eleme);
        dest.writeString(this.code_huoshan);
        dest.writeString(this.code_fanqie);
        dest.writeString(this.code_taote);
        dest.writeString(this.code_jingdong);
    }

    protected RecommendBean(Parcel in) {
        this.recommendCode = in.readString();
        this.code_toutiao = in.readString();
        this.code_douyin = in.readString();
        this.code_kuaishou = in.readString();
        this.code_diantao = in.readString();
        this.code_baidu = in.readString();
        this.code_aiqiyi = in.readString();
        this.code_meitianzhuandian = in.readString();
        this.code_meituan = in.readString();
        this.code_eleme = in.readString();
        this.code_huoshan = in.readString();
        this.code_fanqie = in.readString();
        this.code_taote = in.readString();
        this.code_jingdong = in.readString();
    }

    public static final Creator<RecommendBean> CREATOR = new Creator<RecommendBean>() {
        @Override
        public RecommendBean createFromParcel(Parcel source) {
            return new RecommendBean(source);
        }

        @Override
        public RecommendBean[] newArray(int size) {
            return new RecommendBean[size];
        }
    };
}
