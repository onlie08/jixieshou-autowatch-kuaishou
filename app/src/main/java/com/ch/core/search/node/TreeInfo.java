package com.ch.core.search.node;


import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;

public class TreeInfo {

    // 所有view的基本信息列表
    private ArrayList<NodeInfo> rects = new ArrayList<NodeInfo>();

    private AccessibilityNodeInfo webNodeInfo;

    // 整个界面的ui tree xml
    private String windowinfo = "";

    // dump web元素是否出错
    private boolean isDumpWebError = false;

    public ArrayList<NodeInfo> getRects() {
        return rects;
    }

    public void addRect(NodeInfo rect) {
        this.rects.add(rect);
    }

    public String getWindowinfo() {
        return windowinfo;
    }

    public void setWindowinfo(String windowinfo) {
        this.windowinfo = windowinfo;
    }

    public boolean isDumpWebError() {
        return isDumpWebError;
    }

    public void setDumpWebError(boolean dumpWebError) {
        isDumpWebError = dumpWebError;
    }

    public AccessibilityNodeInfo getWebNodeInfo() {
        return webNodeInfo;
    }

    public void setWebNodeInfo(AccessibilityNodeInfo webNodeInfo) {
        this.webNodeInfo = webNodeInfo;
    }
}
