package com.ch.event;

import com.ch.model.AppInfo;

import java.util.List;

public class AddAllTaskEvent {
    private List<AppInfo> appInfo;

    public List<AppInfo> getAppInfo() {
        return appInfo;
    }

    public void setAppInfo(List<AppInfo> appInfo) {
        this.appInfo = appInfo;
    }

    public AddAllTaskEvent(List<AppInfo> appInfo) {
        this.appInfo = appInfo;
    }
}
