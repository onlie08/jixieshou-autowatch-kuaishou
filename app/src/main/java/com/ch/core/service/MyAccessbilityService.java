package com.ch.core.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import com.blankj.utilcode.util.LogUtils;
import com.ch.application.MyApplication;
import com.ch.core.bus.BusEvent;
import com.ch.core.bus.BusManager;
import com.ch.core.bus.EventType;
import com.ch.core.utils.Logger;
import com.ch.core.utils.StringUtil;
import com.ch.core.utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.leancloud.utils.LogUtil;

import static com.ch.core.bus.EventType.accessiblity_connected;

public class MyAccessbilityService extends AccessibilityService {

    private int noRootCount = 0;
    private static final int maxNoRootCount = 3;
    private boolean isWork = false;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
//        Logger.d("MyAccessbilityService event: " + event);
    }

    @Override
    public void onInterrupt() {
        Logger.e("MyAccessbilityService onInterrupt");
    }

    public AccessibilityNodeInfo[] getRoots() {
        AccessibilityNodeInfo activeRoot = getRootInActiveWindow();
        String activeRootPkg = Utils.getRootPackageName(activeRoot);

        Map<String, AccessibilityNodeInfo> map = new HashMap<>();
        if(activeRoot != null){
            map.put(activeRootPkg, activeRoot);
        }

        if (Build.VERSION.SDK_INT >= 21) {
            List<AccessibilityWindowInfo> windows = getWindows();
            for (AccessibilityWindowInfo w : windows) {
                if(w.getRoot() == null || getPackageName().equals(Utils.getRootPackageName(w.getRoot()))) {
                    continue;
                }
                String rootPkg = Utils.getRootPackageName(w.getRoot());
                if(getPackageName().equals(rootPkg)) {
                    continue;
                }
                if(rootPkg.equals(activeRootPkg)) {
                    continue;
                }
                map.put(rootPkg, w.getRoot());
            }
        }
        if (map.isEmpty()) {
            noRootCount++;
        } else {
            if(!isWork) {
                MyApplication.getAppInstance().getMainActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        BusManager.getBus().post(new BusEvent<>(EventType.roots_ready));
                    }
                });
            }
            isWork = true;
            noRootCount = 0;
        }
        if (noRootCount >= maxNoRootCount) {
            isWork = false;
            MyApplication.getAppInstance().getMainActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    BusManager.getBus().post(new BusEvent<>(EventType.no_roots_alert));
                }
            });
        }
        return map.values().toArray(new AccessibilityNodeInfo[0]);
    }

    public boolean containsPkg(String pkg) {
        if(StringUtil.isEmpty(pkg)) {
            return false;
        }
        AccessibilityNodeInfo[] roots = getRoots();
        for(AccessibilityNodeInfo root: roots) {
            if(pkg.equals(Utils.getRootPackageName(root))) {
                return true;
            }
        }
        return false;
    }

    @Override

    public void onCreate() {
        super.onCreate();
        Logger.d("MyAccessbilityService on create");
        BusManager.getBus().post(new BusEvent<>(EventType.set_accessiblity, this));
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Logger.d("MyAccessbilityService onstart");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.d("MyAccessbilityService onStartCommand");
        BusManager.getBus().post(new BusEvent<>(EventType.set_accessiblity, this));
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Logger.d("MyAccessbilityService onDestroy");
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Logger.d("MyAccessbilityService onunbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        Logger.d("MyAccessbilityService onrebind");
        super.onRebind(intent);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Logger.d("MyAccessbilityService ontaskremoved");
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Logger.d("MyAccessbilityService onServiceConnected");
        BusManager.getBus().post(new BusEvent<>(accessiblity_connected));
        isWork = true;
    }

    @Override
    public AccessibilityNodeInfo findFocus(int focus) {
        Logger.d("MyAccessbilityService findFocus:"+focus);
        return super.findFocus(focus);
    }

    public boolean isWrokFine() {
        return isWork;
    }



}
