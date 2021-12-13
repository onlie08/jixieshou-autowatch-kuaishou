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

public class FanQieScript extends BaseScript {

    private String TAG = this.getClass().getSimpleName();

    private volatile static FanQieScript instance; //声明成 volatile

    public static FanQieScript getSingleton(AppInfo appInfo) {
        if (instance == null) {
            synchronized (FanQieScript.class) {
                if (instance == null) {
                    instance = new FanQieScript(appInfo);
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
            if (!MyApplication.getAppInstance().getAccessbilityService().containsPkg(Constant.PN_FAN_QIE)) {
                return false;
            }
        }
        return true;
    }

    public FanQieScript(AppInfo appInfo) {
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

        } else if (pageId == 4) {

            doPageId4Things();

        } else {
            if (clickContent("看小视频免费听")) return;

            if (samePageCount >= 4) {
                if (clickContent("继续观看")) return;
                NodeInfo nodeInfo = findByText("反馈");
                if (null != nodeInfo) {
                    clickXY(MyApplication.getScreenWidth() - SizeUtils.dp2px(40), nodeInfo.getRect().centerY());
                    Utils.sleep(1500);
                }
            }
            Utils.sleep(1500);
            clickBack();
        }

    }

    private void doPageId4Things() {
        if (samePageCount > 2) {
            SPUtils.getInstance().put("invite_fanqie", true);
            return;
        }
        clickId("input");

        Utils.sleep(2000);

        AccessibilityNodeInfo textInfo = findAccessibilityNode();
        if (textInfo != null) {
            Bundle arguments = new Bundle();
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, MyApplication.recommendBean.getCode_fanqie());
            textInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
            Utils.sleep(2000);
            clickBack();
            Utils.sleep(2000);
            if (clickContent("马上提交")) {
                Utils.sleep(2000);
                SPUtils.getInstance().put("invite_fanqie", true);
                return;
            }
        }
    }

    //    private int gotoPersonCount = 0;
    private void doPageId0Things() {
        LogUtils.d(TAG, "doPageId0Things");
        NodeInfo nodeInfo = findByText("听过");
        clickXY(MyApplication.getScreenWidth() - nodeInfo.getRect().centerX(), nodeInfo.getRect().centerY());
//        clickContent("")
//        gotoPersonCount++;
//        if (gotoPersonCount > 4) {
//            gotoPersonCount = 0;
//            if(clickContent("火苗管理，按钮"))return;
//            return;
//        }
//
//        scrollUp();
        return;

    }

    private void doPageId1Things() {
        LogUtils.d(TAG, "doPageId1Things");
        if (samePageCount > 2) {
            if (clickContent("立即签到 +")) return;
//            clickContent("立即签到");
            scrollDown();
            Utils.sleep(1000);
        }
        if (samePageCount > 4) {
//            setTodayDone(true);
            NodeInfo nodeInfo = findByText("首页");
            clickXY(MyApplication.getScreenWidth() / 2, nodeInfo.getRect().centerY());
            return;
        }
        if (clickContent("开宝箱得金币")) return;

        if (clickContent("点击领取")) return;
        if (clickContent("立即领取")) return;
        if (clickContent("立即观看")) return;
        if (!SPUtils.getInstance().getBoolean("invite_fanqie", false)) {
            if (clickContent("填写邀请码")) return;
        }
//        if(clickTotalMatchContent("听书赚金币")){
//            Utils.sleep(500);
//            if(findContent("今日任务完成了，去看看")){
//                setTodayDone(true);
//            }
//        }
        scrollUpSlow();
        return;
    }

    private void doPageId2Things() {
        LogUtils.d(TAG, "doPageId2Things");

        if (clickContent("继续观看")) return;

        if (samePageCount >= 10) {
            NodeInfo nodeInfo = findByText("反馈");
            if (null != nodeInfo) {
                clickXY(MyApplication.getScreenWidth() - SizeUtils.dp2px(40), nodeInfo.getRect().centerY());
                Utils.sleep(1500);
            }
        }

    }

    private void doPageId3Things() {
        LogUtils.d(TAG, "doPageId3Things");
        if (clickContent("看小视频免费听")) return;

        if (clickContent("立即领取")) return;
        if (samePageCount > 5) {
            clickBack();
        }

    }

    public AccessibilityNodeInfo findAccessibilityNode() {
        AccessibilityNodeInfo root = MyApplication.getAppInstance().getAccessbilityService().getRootInActiveWindow();
        if (root == null) return null;
        AccessibilityNodeInfo root1 = root.findFocus(AccessibilityNodeInfo.FOCUS_INPUT);
        AccessibilityNodeInfo root2 = root1.getChild(0);
        AccessibilityNodeInfo root3 = root2.getChild(0);
        AccessibilityNodeInfo root4 = root3.getChild(0);
        AccessibilityNodeInfo root5 = root4.getChild(2);
        if (null != root5) {
            return root5;
        }
        return null;
    }

    /**
     * 弹出框里点击看广告
     */
    private boolean clickAdvert() {
        if (clickTotalMatchContent("领红包")) return true;
        if (clickContent("视频再")) return true;
        if (clickContent("再看一个")) return false;
        if (clickContent("看广告再得")) return true;
        return false;
    }

    /**
     * 检查是在那个页面
     *
     * @return //0:首页 1:个人中心  2:阅读页  3:广告页
     */
    private int checkPageId() {
        if (findContent("分类") && findContent("搜索")) {
            return 0;
        }
        if (findContent("日常任务") || findContent("金币收益") || findContent("睡觉赚金币") || findContent("听歌赚钱")) {
            return 1;
        }

        if (findContent("后可领取奖励")) {
            return 2;
        }

        if (!findTotalMatchContent("0s") && !findTotalMatchContent("1s") && findContent("反馈") && findContent("跳过")) {
            return 2;
        }

        if (findContent("定时") && findContent("语速")) {
            return 3;
        }
//        if (findContent("反馈")&& findContent("跳过")) {
//            return 2;
//        }

        if (findContent("马上提交")) {
            return 4;
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
        return getAppInfo().getPkgName().equals(Constant.PN_FAN_QIE) ? true : false;
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
                startApp();
                Utils.sleep(2000);
                samePageCount = 0;
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
                dealNoResponse();
                Utils.sleep(2000);
                resumeCount = 0;
                CrashReport.postCatchedException(new Throwable("番茄畅听无响应"));

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
        if (clickTotalMatchContent("优先体验")) return true;
        if (clickTotalMatchContent("以后再说")) return true;
        if (clickContent("重新加载")) return true;
        if (clickContent("知道")) return true;
        if (clickContent("继续赚金币")) return true;
        if (clickContent("去赚钱")) return true;
        if (clickTotalMatchContent("以后再说")) return true;
        if (clickTotalMatchContent("允许")) return true;
        if (clickContent("立即添加")) return true;
        if (clickContent("关闭")) return true;
        if (clickContent("重试")) return true;
        if (clickContent("取消")) return true;
        if (clickContent("继续观看")) return true;
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

