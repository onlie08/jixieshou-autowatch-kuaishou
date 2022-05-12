package com.ch.core.service;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import com.ch.application.MyApplication;
import com.ch.core.bus.BusEvent;
import com.ch.core.bus.BusManager;
import com.ch.core.bus.EventType;
import com.ch.core.search.node.Dumper;
import com.ch.core.search.node.TreeInfo;
import com.ch.core.utils.Logger;
import com.ch.core.utils.StringUtil;
import com.ch.core.utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ch.core.bus.EventType.accessiblity_connected;

public class MyAccessbilityService extends AccessibilityService {

    private int noRootCount = 0;
    private static final int maxNoRootCount = 3;
    private boolean isWork = false;
    private AccessibilityNodeInfo[] curRoots;
    private TreeInfo curTreeInfo;
    private TreeInfo CurAllPageTreeInfo;
    private boolean isSettingRoot = false;
    private boolean serviceing = false;
//    public static AccessibilityEvent curPageEvent;


    public boolean isSettingRoot() {
        return isSettingRoot;
    }

    public void setSettingRoot(boolean settingRoot) {
        isSettingRoot = settingRoot;
    }

    public TreeInfo getCurTreeInfo() {
//        Log.d("MyAccessbilityService","获取缓存的curTreeInfo");
        return curTreeInfo;
    }

    public void setCurTreeInfo(TreeInfo curTreeInfo) {
        this.curTreeInfo = curTreeInfo;
    }

    public TreeInfo getCurAllPageTreeInfo() {
//        Log.d("MyAccessbilityService","获取缓存的CurAllPageTreeInfo");
//        return curTreeInfo;
        setAllPageRoot();
        return CurAllPageTreeInfo;
    }

    public void setCurAllPageTreeInfo(TreeInfo curAllPageTreeInfo) {
        CurAllPageTreeInfo = curAllPageTreeInfo;
    }

    public AccessibilityNodeInfo[] getCurRoots() {
//        Log.d("MyAccessbilityService","获取缓存的root");
        return curRoots;
    }

    public void setCurRoots(AccessibilityNodeInfo[] curRoots) {
        this.curRoots = curRoots;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        final int eventType = event.getEventType();
        if (eventType == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            List<CharSequence> texts = event.getText();
            for (CharSequence text : texts) {
                String content = text.toString();
                if (!TextUtils.isEmpty(content)) {
                    if (content.contains("[微信红包]")) {
                        openWeChatPage(event);
                    }
                }
            }
        }else if(eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED){
//            Logger.d("MyAccessbilityService event: " + event.getClassName() + " event.getEventType:"+event.getEventType());
        }else if(eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED){
//            Logger.d("MyAccessbilityService event: " + event.getClassName() + " event.getEventType:"+event.getEventType());
        }else if(eventType == AccessibilityEvent.TYPE_WINDOWS_CHANGED){
            Logger.d("MeiTianZhuanDian event: " + event.getClassName() + " event.getEventType:"+event.getEventType());
//            Utils.sleep(1000);
//            setRoot();
//            Logger.d("MyAccessbilityService event: " + event.getClassName() + " event.getEventType:"+event.getEventType());
        }else if(eventType == AccessibilityEvent.TYPE_VIEW_CLICKED){
            Logger.d("MeiTianZhuanDian event: " + event.getClassName() + " event.getEventType:"+event.getEventType());
//            Utils.sleep(1000);
//            setRoot();
//            Logger.d("MyAccessbilityService event: " + event.getClassName() + " event.getEventType:"+event.getEventType());
        }
//        Logger.d("MyAccessbilityService event: " + event.getClassName() + " event.getEventType:"+event.getEventType());
    }

    public void setRoot(){
        Log.d("MeiTianZhuanDian","开始更新缓存root");
//        if(!serviceing)return;
        if(isSettingRoot)return;
        isSettingRoot = true;
        AccessibilityNodeInfo activeRoot = getRootInActiveWindow();
        String activeRootPkg = Utils.getRootPackageName(activeRoot);

        Map<String, AccessibilityNodeInfo> map = new HashMap<>();
        if (activeRoot != null) {
            map.put(activeRootPkg, activeRoot);
        }

        if (Build.VERSION.SDK_INT >= 21) {
            List<AccessibilityWindowInfo> windows = getWindows();
            for (AccessibilityWindowInfo w : windows) {
                if (w.getRoot() == null || getPackageName().equals(Utils.getRootPackageName(w.getRoot()))) {
                    continue;
                }
                String rootPkg = Utils.getRootPackageName(w.getRoot());
                if (getPackageName().equals(rootPkg)) {
                    continue;
                }
                if (rootPkg.equals(activeRootPkg)) {
                    continue;
                }
                map.put(rootPkg, w.getRoot());
            }
        }
        if (map.isEmpty()) {
            noRootCount++;
        } else {
            if (!isWork) {
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
        setCurRoots(map.values().toArray(new AccessibilityNodeInfo[0]));
//        setCurAllPageTreeInfo(new Dumper(curRoots).withIncludeOutsideSceenControl(true).dump());
        setCurTreeInfo(new Dumper(curRoots).withIncludeOutsideSceenControl(false).dump());
        Log.d("MeiTianZhuanDian","结束更新缓存root");
        isSettingRoot = false;
    }

    public void setAllPageRoot(){
        AccessibilityNodeInfo activeRoot = getRootInActiveWindow();
        String activeRootPkg = Utils.getRootPackageName(activeRoot);

        Map<String, AccessibilityNodeInfo> map = new HashMap<>();
        if (activeRoot != null) {
            map.put(activeRootPkg, activeRoot);
        }

        if (Build.VERSION.SDK_INT >= 21) {
            List<AccessibilityWindowInfo> windows = getWindows();
            for (AccessibilityWindowInfo w : windows) {
                if (w.getRoot() == null || getPackageName().equals(Utils.getRootPackageName(w.getRoot()))) {
                    continue;
                }
                String rootPkg = Utils.getRootPackageName(w.getRoot());
                if (getPackageName().equals(rootPkg)) {
                    continue;
                }
                if (rootPkg.equals(activeRootPkg)) {
                    continue;
                }
                map.put(rootPkg, w.getRoot());
            }
        }
        if (map.isEmpty()) {
            noRootCount++;
        } else {
            if (!isWork) {
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
        setCurAllPageTreeInfo(new Dumper(curRoots).withIncludeOutsideSceenControl(true).dump());
    }

    @Override
    public void onInterrupt() {
        Logger.e("MyAccessbilityService onInterrupt");
    }

//    public AccessibilityNodeInfo[] getRoots() {
//        if(null != curRoots){
//            Log.d("MyAccessbilityService","获取缓存的root");
//            return curRoots;
//        }
//        TreeInfo treeInfo = new Dumper(curRoots).withIncludeOutsideSceenControl(true).dump();
//
//        AccessibilityNodeInfo activeRoot = getRootInActiveWindow();
//        String activeRootPkg = Utils.getRootPackageName(activeRoot);
//
//        Map<String, AccessibilityNodeInfo> map = new HashMap<>();
//        if (activeRoot != null) {
//            map.put(activeRootPkg, activeRoot);
//        }
//
//        if (Build.VERSION.SDK_INT >= 21) {
//            List<AccessibilityWindowInfo> windows = getWindows();
//            for (AccessibilityWindowInfo w : windows) {
//                if (w.getRoot() == null || getPackageName().equals(Utils.getRootPackageName(w.getRoot()))) {
//                    continue;
//                }
//                String rootPkg = Utils.getRootPackageName(w.getRoot());
//                if (getPackageName().equals(rootPkg)) {
//                    continue;
//                }
//                if (rootPkg.equals(activeRootPkg)) {
//                    continue;
//                }
//                map.put(rootPkg, w.getRoot());
//            }
//        }
//        if (map.isEmpty()) {
//            noRootCount++;
//        } else {
//            if (!isWork) {
//                MyApplication.getAppInstance().getMainActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        BusManager.getBus().post(new BusEvent<>(EventType.roots_ready));
//                    }
//                });
//            }
//            isWork = true;
//            noRootCount = 0;
//        }
//        if (noRootCount >= maxNoRootCount) {
//            isWork = false;
//            MyApplication.getAppInstance().getMainActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    BusManager.getBus().post(new BusEvent<>(EventType.no_roots_alert));
//                }
//            });
//        }
//        return map.values().toArray(new AccessibilityNodeInfo[0]);
//    }

    public boolean containsPkg(String pkg) {
        if (StringUtil.isEmpty(pkg)) {
            return false;
        }
        AccessibilityNodeInfo[] roots = getCurRoots();
        if(null == roots)return false;
        for (AccessibilityNodeInfo root : roots) {
            if (pkg.equals(Utils.getRootPackageName(root))) {
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
        serviceing = true;
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
        serviceing = false;
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
        Logger.d("MyAccessbilityService findFocus:" + focus);
        return super.findFocus(focus);
    }

    public boolean isWrokFine() {
        return isWork;
    }


    /**
     * 开启红包所在的聊天页面
     */
    private void openWeChatPage(AccessibilityEvent event) {
        if (event.getParcelableData() != null && event.getParcelableData() instanceof Notification) {
            Notification notification = (Notification) event.getParcelableData();
            //打开对应的聊天界面
            PendingIntent pendingIntent = notification.contentIntent;
            try {
                pendingIntent.send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        }
    }
}
