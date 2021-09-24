package com.ch.model;

public class ScreenShootEvet {
    private String packageName;
    private int pageId;

    public ScreenShootEvet(String packageName, int pageId) {
        this.packageName = packageName;
        this.pageId = pageId;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getPageId() {
        return pageId;
    }

    public void setPageId(int pageId) {
        this.pageId = pageId;
    }
}
