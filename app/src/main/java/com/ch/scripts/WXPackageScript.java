package com.ch.scripts;


import android.view.accessibility.AccessibilityNodeInfo;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.ch.application.MyApplication;
import com.ch.core.bus.BusEvent;
import com.ch.core.bus.BusManager;
import com.ch.core.bus.EventType;
import com.ch.core.search.node.NodeInfo;
import com.ch.core.utils.Constant;
import com.ch.core.utils.Utils;
import com.ch.model.AppInfo;

import java.util.List;

public class WXPackageScript extends BaseTimingScript {
    private String TAG = this.getClass().getSimpleName();

    private int pageId = -1;

    private volatile static WXPackageScript instance; //声明成 volatile

    public static WXPackageScript getSingleton(AppInfo appInfo) {
        if (instance == null) {
            synchronized (WXPackageScript.class) {
                if (instance == null) {
                    instance = new WXPackageScript(appInfo);
                }
            }
        }
        return instance;
    }

    public WXPackageScript(AppInfo appInfo) {
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

        LogUtils.d(TAG, "是目标软件");

        if (clickContent("查看领取详情")) {
            Utils.sleep(1000);
            clickXY(MyApplication.getScreenWidth() / 2, MyApplication.getScreenHeight() - SizeUtils.dp2px(30));
            return;
        }


        if (clickContent("领取红包")) {
            Utils.sleep(500);
            //[405,1478][675,1754]
            clickXY(500,1600);
            Utils.sleep(500);
            if (clickContent("查看领取详情")) {
                Utils.sleep(1000);
                clickXY(MyApplication.getScreenWidth() / 2, MyApplication.getScreenHeight() - SizeUtils.dp2px(30));
                return;
            }
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
            if (!MyApplication.getAppInstance().getAccessbilityService().containsPkg(Constant.PN_WEI_XIN)) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void doSamePageDeal() {

    }

    public boolean isCurrentScipte() {
        return getAppInfo().getPkgName().equals(Constant.PN_WEI_XIN) ? true : false;
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
