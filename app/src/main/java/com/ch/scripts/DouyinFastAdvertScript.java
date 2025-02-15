package com.ch.scripts;

import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.ch.model.AppInfo;
import com.ch.model.ScreenShootEvet;
import com.google.gson.Gson;
import com.tencent.bugly.crashreport.CrashReport;

import org.greenrobot.eventbus.EventBus;

import static android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_TAKE_SCREENSHOT;
import static com.ch.core.utils.ActionUtils.pressHome;

public class DouyinFastAdvertScript extends BaseScript {
    private String TAG = this.getClass().getSimpleName();
    private Point point_ShouYe;
    private Point point_LaiZhuanQian;

    private volatile static DouyinFastAdvertScript instance; //声明成 volatile

    public static DouyinFastAdvertScript getSingleton(AppInfo appInfo) {
        if (instance == null) {
            synchronized (DouyinFastAdvertScript.class) {
                if (instance == null) {
                    instance = new DouyinFastAdvertScript(appInfo);
                }
            }
        }
        return instance;
    }

    private boolean adverting = false;
    private int samePageCount = 0; //同一个页面停留次数
    private int lastPageId = -1; //上次的页面
    private int pageId = -1;//0:首页 1:个人中心  2:阅读页  3:广告页

    @Override
    protected boolean isTargetPkg() {
        if (MyApplication.getAppInstance().getAccessbilityService().isWrokFine()) {
            if (!MyApplication.getAppInstance().getAccessbilityService().containsPkg(Constant.PN_DOU_YIN)) {
                return false;
            }
        }
        return true;
    }

    public DouyinFastAdvertScript(AppInfo appInfo) {
        super(appInfo);
        getRecognitionResult();
    }

    @Override
    protected void executeScript() {
        LogUtils.d(TAG, "executeScript");
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

        doSamePageDeal();
        LogUtils.d(TAG, "pageId:" + pageId + " samePageCount:" + samePageCount);

        if (clickAdvert()) return;

        if (pageId == 0) {
            if (point_ShouYe == null) {
                getRecognitionResult();
                if (point_ShouYe == null) {
                    EventBus.getDefault().post(new ScreenShootEvet(Constant.PN_DOU_YIN, Constant.PAGE_MAIN));
                }
                return;
            }

            if (point_LaiZhuanQian == null) {
                getRecognitionResult();
                if (point_LaiZhuanQian == null) {
                    EventBus.getDefault().post(new ScreenShootEvet(Constant.PN_DOU_YIN, Constant.PAGE_MAIN));
                }
                return;
            }
            doPageId0Things();

        } else if (pageId == 1) {

            doPageId1Things();

        } else if (pageId == 2) {

            doPageId2Things();

        } else if (pageId == 3) {

            doPageId3Things();

        } else if (pageId == 4) {
            Utils.sleep(30000);


        } else {
            clickBack();
        }
    }


    /**
     * 处理返回解决不了的弹出框，但是能找到资源的
     *
     * @return
     */
    private boolean dealNoResponse2() {
        if (clickContent("仅在使用中允许")) return true;
        if (clickContent("重试")) return true;
        if (clickTotalMatchContent("以后再说")) return true;
        if (clickTotalMatchContent("允许")) return true;
        if (clickTotalMatchContent("取消")) return true;
        if (clickTotalMatchContent("不允许")) return true;
        if (clickTotalMatchContent("禁止")) return true;

        if (clickContent("暂时不要")) return true;
        if (clickContent("知道")) return true;
        if (clickContent("立即签到")) return true;
        if (clickTotalMatchContent("以后再说")) return true;
        if (clickTotalMatchContent("允许")) return true;
        if (clickContent("立即添加")) return true;
        if (clickTotalMatchContent("关闭")) return true;
        if (clickContent("重试")) return true;
        if (clickTotalMatchContent("取消")) return true;


        return false;
    }

    private void doPageId0Things() {
        LogUtils.d(TAG, "doPageId0Things");
        scrollUp();
        Utils.sleep(2000);

        if (samePageCount > 3) {
            clickXY(point_LaiZhuanQian.x, point_LaiZhuanQian.y);

            //没有去赚钱按钮而是去拍摄的情况
            Utils.sleep(2000);
            if(findTotalMatchContent("选择音乐")){
                clickBack();
                Utils.sleep(2000);
                if(clickContent("/8")){
                    return;
                }
            }
            return;
        }

    }


    private void doPageId1Things() {
        LogUtils.d(TAG, "doPageId1Things");
        if (samePageCount > 2) {
            if(clickContent("邀请好友 立赚高额现金")) Utils.sleep(2000);
            if(clickContent("立即签到 +")) Utils.sleep(2000);
//            if(clickContent("去赚钱")) Utils.sleep(2000);
//            clickContent("去赚钱");
//            tryClickDialog();
        }

        if (samePageCount > 4) {
            scrollDown();
            Utils.sleep(2000);
            tryClickDialog();
        }
//        if(clickContent("开宝箱得金币"))Utils.sleep(2000);

        if(findContent("填写好友邀请码")){
            NodeInfo nodeInfo = findByText("填写好友邀请码");
            clickXY(nodeInfo.getRect().centerX(),nodeInfo.getRect().centerY()+SizeUtils.dp2px(40));
            Utils.sleep(2000);
            AccessibilityNodeInfo editText = findEditText();
            if(null != editText){
                Bundle arguments = new Bundle();
                arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, MyApplication.recommendBean.getCode_douyin());
                editText.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
                Utils.sleep(2000);
                clickBack();
                Utils.sleep(2000);

                clickXY(MyApplication.getScreenWidth()-SizeUtils.dp2px(70),nodeInfo.getRect().centerY()+SizeUtils.dp2px(40));

                Utils.sleep(2000);
                SPUtils.getInstance().put("invite_douyin", true);
                CrashReport.postCatchedException(new Exception("抖音邀请码自动填写成功"));
                clickBack();
                Utils.sleep(2000);
//                clickBack();
                return;
//                if(clickTotalMatchContent("确认")){
//                    Utils.sleep(2000);
//                    SPUtils.getInstance().put("invite_douyin", true);
//                    CrashReport.postCatchedException(new Exception("抖音邀请码自动填写成功"));
//                    clickBack();
//                    Utils.sleep(2000);
//                    clickBack();
//                    return;
//                }
            }
        }

        NodeInfo nodeInfo = findByText("点击领金币");
        if (null != nodeInfo) {
            Point point = new Point(nodeInfo.getRect().centerX() + SizeUtils.dp2px(50), nodeInfo.getRect().centerY());
            clickXY(point.x, point.y);
            return;
        }

        if (clickContent("开宝箱得金币")) return;

        if (!findContent("看广告")) {
            scrollUpSlow();
            return;
        }
        clickContent("看广告");
        Utils.sleep(2000);



        if (findContent("明日再来")) {
            setTodayDone(true);
            CrashReport.postCatchedException(new Exception("抖音极速版今日任务完成"));
            skipTask();
        }
//        if (!findContent("去逛街")) {
//            scrollUpSlow();
//            Utils.sleep(2000);
//        }
        if (clickContent("逛街赚钱")) return;
//        if(!findContent("后浏览还可得金币") && !findContent("明日浏览可得金币")){
//            if(clickContent("逛街赚钱"))return;
//        }

//        scrollDown();
//        Utils.sleep(2000);
//        scrollDown();
        return;
    }

    private void doPageId2Things() {
        LogUtils.d(TAG, "doPageId2Things");
        if (clickContent("继续观看")) return;
    }

    private void doPageId3Things() {
        LogUtils.d(TAG, "doPageId2Things");
        if (clickContent("回到页面")) return;
        scrollUpSlow();
    }

    private boolean isAdverting() {
        NodeInfo nodeInfo1 = findByText("后可领取");
        if (nodeInfo1 != null) {
            LogUtils.dTag(TAG, "找到后可领取");
            return true;
        }
        return false;
    }


    @Override
    protected int getMinSleepTime() {
        if (pageId == 3) {
            return 10000;
        } else if (pageId == -1) {
            return 1000;
        } else if (pageId == 0) {
            return 2000;
        }
        return 2000;
    }

    @Override
    protected int getMaxSleepTime() {
        if (pageId == 3) {
            return 10000;
        } else if (pageId == -1) {
            return 1000;
        } else if (pageId == 0) {
            return 3000;
        }
        return 2000;
    }

    @Override
    protected void getRecognitionResult() {
        String sp_shouye = SPUtils.getInstance().getString(Constant.DOUYIN_SHOUYE, "");
        if (!TextUtils.isEmpty(sp_shouye)) {
            point_ShouYe = new Gson().fromJson(sp_shouye, Point.class);
        }
        String sp_laizhuanqian = SPUtils.getInstance().getString(Constant.DOUYIN_LAIZHUANQIAN, "");
        if (!TextUtils.isEmpty(sp_laizhuanqian)) {
            point_LaiZhuanQian = new Gson().fromJson(sp_laizhuanqian, Point.class);
        }
    }


    public boolean isCurrentScipte() {
        return getAppInfo().getPkgName().equals(Constant.PN_DOU_YIN) ? true : false;
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
                LogUtils.d(TAG, "自动恢复到抖音极速版");
                startApp();
                Utils.sleep(2000);
            }
            if (resumeCount > 10) {
                if (com.ch.jixieshou.BuildConfig.DEBUG) {
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
                CrashReport.postCatchedException(new Throwable("抖音极速版无响应"));
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

    //看广告
    private boolean clickAdvert() {
        LogUtils.d(TAG, "clickAdvert()");

        if (clickContent("看广告视频再赚")) return true;

        if (clickContent("再看一个获取")) return true;

        return false;
    }


    /**
     * 检查是在那个页面
     *
     * @return //0:首页 1:个人中心  2:阅读页  3:广告页
     */
    private int checkPageId() {
        if (findContent("跳过广告") && findContent("点击跳转至第三方页面")) {
            return 4;
        }

        if (findContent("赚钱任务") || findContent("开宝箱得金币") || findContent("现金收益") || findContent("看广告赚金币") || findContent("看视频，赚金币") || findContent("看小说赚金币")) {
            return 1;
        }

        if ((findContent("首页") && findContent("，按钮")) || findContent("点击进直播间开宝箱")) {
            return 0;
        }


        if (findContent("后可领取")) {
            return 2;
        }

        if (findContent("逛街赚钱")) {
            return 3;
        }

        //todo 邀请码自动填写功能
        return -1;
    }

    @Override
    protected void doSamePageDeal() {
        if (samePageCount > 10 && samePageCount < 13) {
            dealNoResponse2();
        }

        if (samePageCount > 12 && samePageCount < 16) {
            Utils.sleep(1500);
            clickBack();
        }

        if (samePageCount > 15) {
            tryClickDialog();
        }
    }

    public AccessibilityNodeInfo findEditText() {
        AccessibilityNodeInfo root = MyApplication.getAppInstance().getAccessbilityService().getRootInActiveWindow();
        if (root == null) return null;
        AccessibilityNodeInfo root0 = root.findFocus(AccessibilityNodeInfo.FOCUS_INPUT);
        if (null != root0) {
            return root0;
        }
        return null;
    }
}
