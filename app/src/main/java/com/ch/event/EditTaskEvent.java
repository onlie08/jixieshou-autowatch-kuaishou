package com.ch.event;

import com.ch.model.AppInfo;

public class EditTaskEvent {
    private AppInfo appInfo;

    public EditTaskEvent(AppInfo appInfo) {
        this.appInfo = appInfo;
    }

    public AppInfo getAppInfo() {
        return appInfo;
    }

    public void setAppInfo(AppInfo appInfo) {
        this.appInfo = appInfo;
    }
}
