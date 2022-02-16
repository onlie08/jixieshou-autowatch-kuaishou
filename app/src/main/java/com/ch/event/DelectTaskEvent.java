package com.ch.event;

import com.ch.model.AppInfo;

public class DelectTaskEvent {
    private AppInfo appInfo;

    public DelectTaskEvent(AppInfo appInfo) {
        this.appInfo = appInfo;
    }

    public AppInfo getAppInfo() {
        return appInfo;
    }

    public void setAppInfo(AppInfo appInfo) {
        this.appInfo = appInfo;
    }
}
