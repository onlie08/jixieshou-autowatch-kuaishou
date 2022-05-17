package com.ch.scripts;


import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.ch.application.MyApplication;
import com.ch.core.bus.BusEvent;
import com.ch.core.bus.BusManager;
import com.ch.core.bus.EventType;
import com.ch.core.search.node.NodeInfo;
import com.ch.core.utils.Constant;
import com.ch.core.utils.Utils;
import com.ch.model.AppInfo;

import java.util.List;

public class FSRedPackageScript extends BaseTimingScript {
    private String TAG = this.getClass().getSimpleName();

    private int pageId = -1;

    private volatile static FSRedPackageScript instance; //声明成 volatile

    public static FSRedPackageScript getSingleton(AppInfo appInfo) {
        if (instance == null) {
            synchronized (FSRedPackageScript.class) {
                if (instance == null) {
                    instance = new FSRedPackageScript(appInfo);
                }
            }
        }
        return instance;
    }

    public FSRedPackageScript(AppInfo appInfo) {
        super(appInfo);
    }

    @Override
    protected void getRecognitionResult() {

    }

    @Override
    protected void executeScript() {
        MyApplication.getAppInstance().getMainActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BusManager.getBus().post(new BusEvent<>(EventType.refresh_time, ""));
            }
        });

        if (!isTargetPkg()) {
            return;
        }
        if (!NetworkUtils.isAvailable()) {
            return;
        }
        if (ScreenUtils.isScreenLock()) {
            return;
        }
        LogUtils.d(TAG, "是目标软件");
        pageId = checkPageId();
        LogUtils.d(TAG, "pageId:" + pageId);
        switch (pageId) {
            case 1:
                if (clickContent("[红包]")) return;
                break;
            case 2:
                getStatues();
                if (unDoneList.size() > doneList.size() + my.size()) {
                    for (int i = unDoneList.size() - 1; i >= 0; i--) {
                        NodeInfo nodeInfo = unDoneList.get(i);
                        clickXY(nodeInfo.getRect().centerX(), nodeInfo.getRect().centerY());
                        Utils.sleep(500);
                        pageId = checkPageId();
                        LogUtils.d(TAG, "pageId:" + pageId);
                        if (pageId == -1) {
                            Utils.sleep(1000);
                            pageId = checkPageId();
                            LogUtils.d(TAG, "pageId:" + pageId);
                        }
                        if (pageId == 3) {
                            if (clickId("f4f")) return;
                            //[404,1376][674,1646]
                            clickXY(MyApplication.getAppInstance().getScreenWidth() / 2, 1500);
                            return;
                        } else {
                            clickBack();
                            Utils.sleep(1000);
                            getStatues();
                            if (unDoneList.size() == doneList.size() + my.size()) {
                                return;
                            }
                        }
                    }

                }

                break;
            case 3:
                if (clickId("f4f")) return;
                //[404,1376][674,1646]
                clickXY(MyApplication.getAppInstance().getScreenWidth() / 2, 1500);
                break;
            case 4:
                AccessibilityNodeInfo root = MyApplication.getAppInstance().getAccessbilityService().getRootInActiveWindow();
                if (root == null) return;
                final List<AccessibilityNodeInfo> list = root.findAccessibilityNodeInfosByText("丰声红包");
                if(null == list || list.isEmpty()){
                    return;
                }
                for(AccessibilityNodeInfo accessibilityNodeInfo : list){
                    if(accessibilityNodeInfo.getParent().getChildCount() == 4){
                        Rect rect = new Rect();
                        accessibilityNodeInfo.getBoundsInScreen(rect);
                        clickXY(rect.centerX(),rect.centerY());
                        Utils.sleep(500);
                    }
                    LogUtils.d(TAG,"getChildCount:"+accessibilityNodeInfo.getParent().getChildCount());
                }


//                clickContent("丰声红包");
                break;
            case 5:
                if(clickId("iv_grab_redpacket_open")){
//                    Utils.sleep(1000);
//                    clickBack();
                }
                break;
            default:
                clickBack();
                break;
        }

    }

    List<NodeInfo> unDoneList;
    List<NodeInfo> doneList;
    List<NodeInfo> my;

    private void getStatues() {
        unDoneList = findContents("微信红包");
        doneList = findContents("已领取");
        my = findContents("已被领完");
    }

    private int checkPageId() {
        if (findId("iv_more") && findId("emoji_button")) {
            return 4;
        }
        if (findContent("普通红包 / 来自")) {
            return 5;
        }


        if (findContent("全部") && findContent("@我")) {
            return 1;
        }
        if (findContent("智能审批") && findContent("打卡")) {
            return 2;
        }
        if (findContent("已进入打卡范围")) {
            return 3;
        }
        if (findContent("工号密码登录")) {
            return 0;
        }

        return -1;
    }

    @Override
    protected boolean isTargetPkg() {
        if (MyApplication.getAppInstance().getAccessbilityService().isWrokFine()) {
            if (!MyApplication.getAppInstance().getAccessbilityService().containsPkg(Constant.PN_FENG_SHENG)) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void doSamePageDeal() {

    }

    public boolean isCurrentScipte() {
        return getAppInfo().getPkgName().equals(Constant.PN_FENG_SHENG) ? true : false;
    }

    @Override
    public void pauseApp() {

    }

    @Override
    public boolean isDestinationPage() {
        // 检查当前包名是否有本年应用
        if (!isTargetPkg() && isCurrentScipte()) {
            if (!NetworkUtils.isAvailable()) {
                return false;
            }
            if (ScreenUtils.isScreenLock()) {
                return false;
            }
            return false;
        }
        return true;
    }

    @Override
    public void destory() {

    }
}
