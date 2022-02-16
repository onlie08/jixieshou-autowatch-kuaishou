package com.ch.model;

import android.graphics.Point;
import android.os.Parcel;
import android.os.Parcelable;

public class RecognitionBean implements Parcelable {
    private Point p1;
    private Point p2;
    private Point p3;
    private Point p4;
    private String res;
    private String probability;

    public Point getP1() {
        return p1;
    }

    public void setP1(Point p1) {
        this.p1 = p1;
    }

    public Point getP2() {
        return p2;
    }

    public void setP2(Point p2) {
        this.p2 = p2;
    }

    public Point getP3() {
        return p3;
    }

    public void setP3(Point p3) {
        this.p3 = p3;
    }

    public Point getP4() {
        return p4;
    }

    public void setP4(Point p4) {
        this.p4 = p4;
    }

    public String getRes() {
        return res;
    }

    public void setRes(String res) {
        this.res = res;
    }

    public String getProbability() {
        return probability;
    }

    public void setProbability(String probability) {
        this.probability = probability;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.p1, flags);
        dest.writeParcelable(this.p2, flags);
        dest.writeParcelable(this.p3, flags);
        dest.writeParcelable(this.p4, flags);
        dest.writeString(this.res);
        dest.writeString(this.probability);
    }

    public RecognitionBean() {
    }

    protected RecognitionBean(Parcel in) {
        this.p1 = in.readParcelable(Point.class.getClassLoader());
        this.p2 = in.readParcelable(Point.class.getClassLoader());
        this.p3 = in.readParcelable(Point.class.getClassLoader());
        this.p4 = in.readParcelable(Point.class.getClassLoader());
        this.res = in.readString();
        this.probability = in.readString();
    }

    public static final Creator<RecognitionBean> CREATOR = new Creator<RecognitionBean>() {
        @Override
        public RecognitionBean createFromParcel(Parcel source) {
            return new RecognitionBean(source);
        }

        @Override
        public RecognitionBean[] newArray(int size) {
            return new RecognitionBean[size];
        }
    };
}
