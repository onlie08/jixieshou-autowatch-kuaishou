package com.ch.scripts;

import android.os.Bundle;
import android.view.accessibility.AccessibilityNodeInfo;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.ch.application.MyApplication;
import com.ch.core.search.node.NodeInfo;
import com.ch.core.utils.Constant;
import com.ch.core.utils.Utils;
import com.ch.jixieshou.BuildConfig;
import com.ch.model.AppInfo;
import com.tencent.bugly.crashreport.CrashReport;

import static android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_TAKE_SCREENSHOT;
import static com.ch.core.utils.ActionUtils.pressHome;

public class HuoShanAdvertScript extends BaseScript {

    private String TAG = this.getClass().getSimpleName();

    private volatile static HuoShanAdvertScript instance; //声明成 volatile

    public static HuoShanAdvertScript getSingleton(AppInfo appInfo) {
        if (instance == null) {
            synchronized (HuoShanAdvertScript.class) {
                if (instance == null) {
                    instance = new HuoShanAdvertScript(appInfo);
                }
            }
        }
        return instance;
    }

    private int pageId = -1;//0:首页 1:个人中心  2:广告页 3：幸运大转盘 4:看广告赚金币
    private int lastPageId = -1; //上次的页面
    private int samePageCount = 0; //同一个页面停留次数

    @Override
    protected boolean isTargetPkg() {
        if (MyApplication.getAppInstance().getAccessbilityService().isWrokFine()) {
            if (!MyApplication.getAppInstance().getAccessbilityService().containsPkg(Constant.PN_HUO_SHAN)) {
                return false;
            }
        }
        return true;
    }

    public HuoShanAdvertScript(AppInfo appInfo) {
        super(appInfo);
    }

    @Override
    protected void executeScript() {
        if (!isTargetPkg()) {
            return;
        }
        if (!NetworkUtils.isAvailable()) {
            return;
        }
        if (ScreenUtils.isScreenLock()) {
            return;
        }


        pageId = checkPageId();
        if (pageId == lastPageId) {
            samePageCount++;
        } else {
            samePageCount = 0;
        }
        lastPageId = pageId;
        LogUtils.d(TAG, "pageId:" + pageId + " samePageCount:" + samePageCount);

        doSamePageDeal();

        if (clickAdvert()) return;

        if (pageId == 0) {

            doPageId0Things();

        } else if (pageId == 1) {

            doPageId1Things();

        } else if (pageId == 2) {

            doPageId2Things();

        } else if (pageId == 3) {

            doPageId3Things();

        } else {
            if (samePageCount >= 2) {
                if (clickContent("我知道了")) return;
                if (clickContent("开心收下")) return;
                if (clickContent("精选")) return;
            }
            Utils.sleep(1500);
            clickBack();
        }

    }

    private int gotoPersonCount = 0;

    private void doPageId0Things() {
        LogUtils.d(TAG, "doPageId0Things");

        gotoPersonCount++;
        if (gotoPersonCount > 4) {
            gotoPersonCount = 0;
            if (clickContent("火苗管理，按钮")) return;
            return;
        }

        scrollUp();
        return;

    }

    private void doPageId1Things() {
        LogUtils.d(TAG, "doPageId1Things");
        if (samePageCount >= 2) {
            if (clickContent("立即签到")) {
                Utils.sleep(1000);
                clickBack();
                return;
            }
        }
        if (samePageCount >= 4) {
//
            if (findContent("今日已达上限")) {
                setTodayDone(true);
                CrashReport.postCatchedException(new Exception("火山今日任务完成"));
            }
            clickBack();
            return;
        }

        NodeInfo nodeInfo = findByText("限时任务赚火苗");
        if (null != nodeInfo) {
            clickXY(MyApplication.getScreenWidth() - SizeUtils.dp2px(60), nodeInfo.getRect().centerY());
            Utils.sleep(2000);

        }

        if (clickTotalMatchContent("开宝箱得火苗")) {
            return;
        }

        if (!SPUtils.getInstance().getBoolean("invite_huoshan", false)) {
            if (clickTotalMatchContent("去填写")) {
                return;
            } else {
                scrollDown();
                Utils.sleep(2000);
            }
            return;
        }

//        NodeInfo nodeInfo = findByText("限时任务赚火苗");
//        if(null != nodeInfo){
//            clickXY(MyApplication.getScreenWidth()-SizeUtils.dp2px(40),nodeInfo.getRect().centerY());
//            Utils.sleep(2000);
//        }
        scrollUpSlow();
        Utils.sleep(2000);
        return;
    }

    private void doPageId2Things() {
        LogUtils.d(TAG, "doPageId2Things");

        if (clickContent("继续观看")) return;
    }

    private void doPageId3Things() {
        LogUtils.d(TAG, "doPageId3Things");
        if (samePageCount > 5) {
            SPUtils.getInstance().put("invite_huoshan", true);
            clickBack();
        }
        NodeInfo nodeInfo = findById("shareCode");

        if (null != nodeInfo) {
            clickXY(nodeInfo.getRect().centerX(), nodeInfo.getRect().centerY());
        }
        Utils.sleep(2000);
        AccessibilityNodeInfo textInfo = findAccessibilityNode();
        if (textInfo != null) {
            Bundle arguments = new Bundle();
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, MyApplication.recommendBean.getCode_huoshan());
            textInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
            Utils.sleep(2000);
            clickBack();
            Utils.sleep(2000);
            SPUtils.getInstance().put("invite_huoshan", true);
            if (clickContent("提交")) return;
        }
    }

    public AccessibilityNodeInfo findAccessibilityNode() {
        AccessibilityNodeInfo root = MyApplication.getAppInstance().getAccessbilityService().getRootInActiveWindow();
        if (root == null) return null;
        AccessibilityNodeInfo root1 = root.findFocus(AccessibilityNodeInfo.FOCUS_INPUT);
        AccessibilityNodeInfo root2 = root1.getChild(0);
        AccessibilityNodeInfo root3 = root2.getChild(0);
        AccessibilityNodeInfo root4 = root3.getChild(1);
        AccessibilityNodeInfo root5 = root4.getChild(3);
        if (null != root5) {
            return root5;
        }
        return null;
    }

    /**
     * 弹出框里点击看广告
     */
    private boolean clickAdvert() {
        if (clickContent("视频再")) return true;
        if (clickContent("再看一个")) return true;
        if (clickContent("看广告再得")) return true;
        return false;
    }

    /**
     * 检查是在那个页面
     *
     * @return //0:首页 1:个人中心  2:阅读页  3:广告页
     */
    private int checkPageId() {
        if (findContent("火苗管理，按钮")) {
            return 0;
        }
        if (findContent("火苗账单")) {
            return 1;
        }

        if (findContent("后可领取奖励")) {
            return 2;
        }

        if (findContent("输入邀请码解锁现金红包")) {
            return 3;
        }

        return -1;
    }


    @Override
    protected int getMinSleepTime() {
        if (pageId == 2) {
            return 2000;
        } else if (pageId == 1) {
            return 2000;
        } else if (pageId == 3) {
            return 3000;
        } else if (pageId == 0) {
            return 4000;
        } else if (pageId == -1) {
            return 1000;
        } else {
            return 3000;
        }

    }

    @Override
    protected int getMaxSleepTime() {
        if (pageId == 2) {
            return 2000;
        } else if (pageId == 1) {
            return 2000;
        } else if (pageId == 3) {
            return 3000;
        } else if (pageId == 0) {
            return 8000;
        } else if (pageId == -1) {
            return 1000;
        } else {
            return 3000;
        }
    }

    @Override
    protected void getRecognitionResult() {
    }


    public boolean isCurrentScipte() {
        return getAppInfo().getPkgName().equals(Constant.PN_HUO_SHAN) ? true : false;
    }

    int resumeCount = 0;

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

            resumeCount++;
            if (resumeCount > 5) {
                LogUtils.d(TAG, "自动恢复到抖音火山版");
                startApp();
                Utils.sleep(2000);
            }
            if (resumeCount > 10) {
                if (BuildConfig.DEBUG) {
                    MyApplication.getAppInstance().getAccessbilityService().performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT);
                    Utils.sleep(2000);
                }
                clickBack();
                Utils.sleep(2000);
                clickBack();
                Utils.sleep(2000);
                LogUtils.d(TAG, "爱奇艺极速版是不是anr了?");
                dealNoResponse();
                Utils.sleep(2000);
                resumeCount = 0;
                CrashReport.postCatchedException(new Throwable("抖音火山版无响应"));
            }
            return false;
        }
        resumeCount = 0;
        return true;
    }

    @Override
    public void destory() {
        if (isTargetPkg()) {
            clickBack();
            Utils.sleep(100);
            clickBack();
            Utils.sleep(1000);
        }
        pressHome();
        stop = true;
    }

    /**
     * 处理返回解决不了的弹出框，但是能找到资源的
     *
     * @return
     */
    private boolean dealNoResponse2() {
        if (clickContent("重新加载")) return true;
        if (clickContent("知道")) return true;
        if (clickContent("继续赚金币")) return true;
        if (clickContent("去赚钱")) return true;
        if (clickTotalMatchContent("允许")) return true;
        if (clickTotalMatchContent("以后再说")) return true;
        if (clickContent("立即添加")) return true;
        if (clickContent("关闭")) return true;
        if (clickContent("重试")) return true;
        if (clickContent("取消")) return true;
        return false;
    }

    @Override
    protected void doSamePageDeal() {
        if (samePageCount > 10 && samePageCount < 13) {
            Utils.sleep(1500);
            clickBack();
        }

        if (samePageCount > 12 && samePageCount < 16) {
            dealNoResponse2();

        }

        if (samePageCount > 15) {
            doRandomClick();
        }
        if (samePageCount > 30) {
            clickBack();
            clickBack();
            samePageCount = 0;
        }

    }
}

