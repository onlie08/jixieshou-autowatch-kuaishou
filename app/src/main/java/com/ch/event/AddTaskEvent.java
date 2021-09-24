package com.ch.event;

import com.ch.model.AppInfo;

public class AddTaskEvent {
    private AppInfo appInfo;

    public AddTaskEvent(AppInfo appInfo) {
        this.appInfo = appInfo;
    }

    public AppInfo getAppInfo() {
        return appInfo;
    }

    public void setAppInfo(AppInfo appInfo) {
        this.appInfo = appInfo;
    }
}
