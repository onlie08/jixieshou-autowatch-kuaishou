package com.ch.event;

import com.ch.model.AppInfo;

public class RefreshTaskEvent {
    private AppInfo appInfo;
    private AppInfo editedAppInfo;

    public RefreshTaskEvent(AppInfo appInfo, AppInfo editedAppInfo) {
        this.appInfo = appInfo;
        this.editedAppInfo = editedAppInfo;
    }

    public AppInfo getAppInfo() {
        return appInfo;
    }

    public void setAppInfo(AppInfo appInfo) {
        this.appInfo = appInfo;
    }

    public AppInfo getEditedAppInfo() {
        return editedAppInfo;
    }

    public void setEditedAppInfo(AppInfo editedAppInfo) {
        this.editedAppInfo = editedAppInfo;
    }
}
