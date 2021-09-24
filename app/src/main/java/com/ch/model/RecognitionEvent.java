package com.ch.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class RecognitionEvent implements Parcelable {
    private List<RecognitionBean> recognitionBeans;
    private String packageName;
    private String pageId;

    public List<RecognitionBean> getRecognitionBeans() {
        return recognitionBeans;
    }

    public void setRecognitionBeans(List<RecognitionBean> recognitionBeans) {
        this.recognitionBeans = recognitionBeans;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public RecognitionEvent(List<RecognitionBean> recognitionBeans, String packageName, String pageId) {
        this.recognitionBeans = recognitionBeans;
        this.packageName = packageName;
        this.pageId = pageId;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.recognitionBeans);
        dest.writeString(this.packageName);
        dest.writeString(this.pageId);
    }

    protected RecognitionEvent(Parcel in) {
        this.recognitionBeans = in.createTypedArrayList(RecognitionBean.CREATOR);
        this.packageName = in.readString();
        this.pageId = in.readString();
    }

    public static final Parcelable.Creator<RecognitionEvent> CREATOR = new Parcelable.Creator<RecognitionEvent>() {
        @Override
        public RecognitionEvent createFromParcel(Parcel source) {
            return new RecognitionEvent(source);
        }

        @Override
        public RecognitionEvent[] newArray(int size) {
            return new RecognitionEvent[size];
        }
    };
}
