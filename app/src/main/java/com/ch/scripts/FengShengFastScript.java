package com.ch.scripts;

import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.ch.application.MyApplication;
import com.ch.core.executor.builder.SFStepBuilder;
import com.ch.core.search.node.NodeInfo;
import com.ch.core.utils.Constant;
import com.ch.core.utils.Logger;
import com.ch.core.utils.Utils;
import com.ch.jixieshou.BuildConfig;
import com.ch.model.AppInfo;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.Calendar;
import java.util.List;

import static android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_TAKE_SCREENSHOT;
import static com.ch.core.utils.ActionUtils.pressHome;

public class FengShengFastScript extends BaseScript {
    private String TAG = this.getClass().getSimpleName();
    private int pageId = -1;//0:首页 1:个人中心  2:阅读页  3:广告页
    private int lastPageId = -1; //上次的页面
    private int samePageCount = 0; //同一个页面停留次数

    public FengShengFastScript(AppInfo appInfo) {
        super(appInfo);
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
    protected void executeScript() {
        if (!isTargetPkg()) {
            return;
        }
        if (!NetworkUtils.isAvailable()) {
            LogUtils.d(TAG, "网络不可用");
            return;
        }
        if (ScreenUtils.isScreenLock()) {
            LogUtils.d(TAG, "屏幕锁定了");
            return;
        }
        if(checkCardTime()){
            skipTask();
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

        switch (pageId) {
            case 0:
                doLoginPage();
                break;
            case 1:
                doMainPage();
                break;
            case 2:
                doJumpPage();
                break;
            case 3:
                doCardPage();
                break;
            default:
                Utils.sleep(1500);
                clickBack();
                break;
        }
    }

    private boolean checkCardTime() {
        Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);//时
        LogUtils.d(TAG,"mhour:"+mHour);
        if(mHour > 8){
            return false;
        }
        return true;
    }

    private void doCardPage() {
        NodeInfo nodeInfo = findByText("当前时间");
        clickXY(MyApplication.getScreenWidth()/2,nodeInfo.getRect().centerY()-SizeUtils.dp2px(100));
        Utils.sleep(3000);
        if(findContent("打卡成功")){
            clickContent("知道了");
            Utils.sleep(2000);
            clickBack();
            setTodayDone(true);
            skipTask();

        }
    }

    private void doJumpPage() {
        clickTotalMatchContent("打卡");
    }

    private void doMainPage() {
        if(samePageCount > 2){
            tryClickDialog();
        }
        clickXY(MyApplication.getScreenWidth()*3/4-SizeUtils.dp2px(40),MyApplication.getScreenHeight()-SizeUtils.dp2px(20));
        Utils.sleep(3000);
        return;

    }

    private void doLoginPage() {
        clickTotalMatchContent("登录");Utils.sleep(3000);

        if(findContent("使用该账号的密码")){
            clickBack();
            Utils.sleep(2000);
//            clickTotalMatchContent("记住密码");
//            Utils.sleep(2000);
        }

        AccessibilityNodeInfo root = MyApplication.getAppInstance().getAccessbilityService().getRootInActiveWindow();
        if (root == null) return ;
        AccessibilityNodeInfo editPassword = root.findFocus(AccessibilityNodeInfo.FOCUS_INPUT);
        if (editPassword != null) {
            Bundle arguments = new Bundle();
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, "Onlie-chcn1314");
            editPassword.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
            Utils.sleep(1000);
            clickTotalMatchContent("登录");
            Utils.sleep(3000);
        }
    }

    @Override
    protected int getMinSleepTime() {
        return 3000;
    }

    @Override
    protected int getMaxSleepTime() {
        return 3000;
    }

    @Override
    protected void getRecognitionResult() {

    }


    public boolean isCurrentScipte() {
        return getAppInfo().getPkgName().equals(Constant.PN_FENG_SHENG) ? true : false;
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
                LogUtils.d(TAG, "自动恢复到丰声极速版");
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
                dealNoResponse();
                Utils.sleep(2000);
                resumeCount = 0;
                CrashReport.postCatchedException(new Throwable("丰声极速版无响应"));

            }
            return false;
        }
        resumeCount = 0;
        return true;
    }

    @Override
    public void destory() {
        clickBack();
        Utils.sleep(100);
        clickBack();
        Utils.sleep(1000);
        pressHome();
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
            tryClickDialog();
        }

    }

    /**
     * 处理返回解决不了的弹出框，但是能找到资源的
     *
     * @return
     */
    private boolean dealNoResponse2() {
        if (clickContent("网络不给力，点击屏幕重试")) return true;
        if (clickContent("知道")) return true;
        if (clickContent("去赚钱")) return true;
        if (clickContent("视频再领")) return true;
        if (clickContent("仅在使用中允许")) return true;
        if (clickTotalMatchContent("禁止")) return true;
        if (clickContent("立即添加")) return true;
        if (clickTotalMatchContent("关闭")) return true;
        if (clickContent("重试")) return true;
        if (clickTotalMatchContent("以后再说")) return true;
        if (clickTotalMatchContent("取消")) return true;
        if (clickContent("开心收下")) return true;
        if (clickTotalMatchContent("不允许")) return true;
        if (clickTotalMatchContent("允许")) return true;
        return false;
    }

    /**
     * 检查是在那个页面
     *
     * @return //0:首页 1:个人中心  2:阅读页  3:广告页
     */
    private int checkPageId() {
        if (findContent("工号密码登录")) {
            return 0;
        }
        if (findTotalMatchContent("全部") && findContent("@我")) {
            return 1;
        }
        if (findTotalMatchContent("智能审批") && findContent("打卡")) {
            return 2;
        }
        if (findTotalMatchContent("已进入打卡范围")) {
            return 3;
        }

        return -1;
    }



}
