package com.ch.scripts;

import android.graphics.Point;
import android.text.TextUtils;

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
import com.google.gson.Gson;
import com.tencent.bugly.crashreport.CrashReport;

import static android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_TAKE_SCREENSHOT;
import static com.ch.core.utils.ActionUtils.pressHome;

public class TaoTeScript extends BaseScript {

    private String TAG = this.getClass().getSimpleName();
    private Point point_ZhuanJinBi;
    private Point point_RenWu;
    private Point point_TianXieYaoQingMa3;
    private Point point_ZhanTie;

    private volatile static TaoTeScript instance; //声明成 volatile

    public static TaoTeScript getSingleton(AppInfo appInfo) {
        if (instance == null) {
            synchronized (TaoTeScript.class) {
                if (instance == null) {
                    instance = new TaoTeScript(appInfo);
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
            if (!MyApplication.getAppInstance().getAccessbilityService().containsPkg(Constant.PN_TAO_TE)) {
                return false;
            }
        }
        return true;
    }

    public TaoTeScript(AppInfo appInfo) {
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
//
//        if (clickAdvert()) return;
//
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

        }else if (pageId == 5) {

            doPageId5Things();

        }else if (pageId == 6) {

            doPageId6Things();

        } else {
            if (samePageCount > 5) {
                if (clickTotalMatchContent("推荐")) Utils.sleep(3000);
            }
            Utils.sleep(1500);
            clickBack();
        }

    }

    /**
     * 喂养小鸡
     */
    private void doPageId6Things() {
        clickXY(MyApplication.getScreenWidth() - SizeUtils.dp2px(60),MyApplication.getScreenHeight() - SizeUtils.dp2px(60));
        Utils.sleep(2000);
        clickBack();
//        if(clickTotalMatchContent("领饲料")){
//            Utils.sleep(1000);
//            if(clickTotalMatchContent("每日免费领饲料")){
//                clickBack();
//                return;
//            }
//        }

    }

    /**
     * 发财鸭升级
     */
    private void doPageId5Things() {
        if(clickTotalMatchContent("领取奖励")){
            return;
        }

        if(clickContent("消耗")){
            Utils.sleep(2000);
            clickBack();
            return;
        }

//        if(clickTotalMatchContent("签到")){
//            return;
//        }
    }

    /**
     * 签到界面
     */
    private void doPageId4Things() {
        if(clickTotalMatchContent("点击签到")){

            return;
        }
        if(clickTotalMatchContent("点击领取")){
            Utils.sleep(1000);
            clickBack();
            return;
        }
        clickBack();
//        if (findTotalMatchContent("做任务领取更多现金")) {
//            if (clickTotalMatchContent("逛街赚钱")) {
//                Utils.sleep(2000);
//                doScan(35);
//                clickBack();
//            }
//            if (clickTotalMatchContent("浏览精选货品")) {
//                Utils.sleep(2000);
//                doScan(35);
//                clickBack();
//            }
//
//            return;
//        }
//        if (clickTotalMatchContent("继续领取现金")) {
//
//        }
    }

    private void doPageId0Things() {
        LogUtils.d(TAG, "doPageId0Things");
//        if (clickContent("天天赚特币")) return;

        NodeInfo nodeInfo = findTotalMatchByText("搜索");
        clickXY(SizeUtils.dp2px(30), nodeInfo.getRect().centerY());
        return;
    }

    boolean task1 = false;
    boolean task2 = false;
    boolean task3 = false;
    boolean task4 = false;
    boolean task5 = false;
    boolean task6 = false;

    private void doPageId1Things() {
        LogUtils.d(TAG, "doPageId1Things");
        if (samePageCount > 2) {
            clickBack();
            return;
        }
        if (clickContent("立即领币")) {
            Utils.sleep(1000);
            clickBack();
            return;
        }

        if (clickContent("点击继续")) return;
        if (clickContent("立即重试")) return;
        if (clickContent("放弃膨胀 领取")) return;

        if (clickTotalMatchContent("可开红包")) {
            Utils.sleep(3000);
            if (clickTotalMatchContent("立即抽奖")) {
                Utils.sleep(5000);
                return;
            }
        }

//        if (clickContent("继续赚币")) return;
        return;
    }

    private void doPageId2Things() {
        LogUtils.d(TAG, "doPageId2Things");

        if (!task1) {
            if (clickContent("看直播福利最高赚") || clickContent("看视频福利最高赚")) {
                doScan(35);
                Utils.sleep(3000);
                task1 = true;
                return;
            }
        }

        if (!task2) {
            if (clickContent("逛1元秒杀最高赚")) {
                doScan(16);
                Utils.sleep(3000);
                task2 = true;
                return;
            }
        }

        if (!task3) {
            if (clickContent("逛品质好物最高赚")) {
                doScan(16);
                Utils.sleep(3000);
                task3 = true;
                return;
            }
        }

        if (!task4) {
            if (clickContent("逛超值抵扣最高赚")) {
                doScan(35);
                Utils.sleep(3000);
                task4 = true;
                return;
            }
        }

        if (!task5) {
            if (clickContent("逛30%抵扣最高")) {
                doScan(16);
                Utils.sleep(3000);
                task5 = true;
                return;
            }
        }
        if (!task6) {
            if (clickContent("逛特价好物")) {
                doScan(16);
                Utils.sleep(3000);
                task6 = true;
                return;
            }
        }
        if (checkDone()) {
            setTodayDone(true);
            CrashReport.postCatchedException(new Exception("淘特今日任务完成"));
            skipTask();
            return;
        }
        scrollUpSlow();
    }

    private boolean checkDone() {
        if (task1 && task2 && task3 && task4 && task5 && task6) {
            return true;
        }
        return false;
    }

    private void doScan(int second) {
        for (int i = 0; i < second; i++) {
            if (isCurrentScipte()) {
                Utils.sleep(1000);
                scrollUp();
            }

        }
    }

    int pos = 0;

    private void doPageId3Things() {
        LogUtils.d(TAG, "doPageId3Things");
        if (clickTotalMatchContent("去领取")) {
            return;
        }
        pos++;

        if (pos >= 5) {
            pos = 0;
        }
        switch (pos) {
            case 0:
                if (clickTotalMatchContent("去浏览")) {
                    Utils.sleep(1000);
                    doScan(60);
                    return;
                }
                break;
            case 1:
                if (clickTotalMatchContent("赚特币当钱花")) {
                    return;
                }
                break;
            case 2:
                if (clickTotalMatchContent("签到领现金红包")) {
                    return;
                }
                break;
            case 3:
                if (clickTotalMatchContent("升级鸭鸭赚现金")) {
                    return;
                }
            case 4:
                if (clickTotalMatchContent("喂小鸡好货0元领")) {
                    return;
                }
                break;
        }
    }

    /**
     * 检查是在那个页面
     *
     * @return //0:首页 1:个人中心  2:阅读页  3:广告页
     */
    private int checkPageId() {
        if (findContent("搜索") && findContent("全部分类")) {
            return 0;
        }
        if (findContent("今日已完成任务")) {
            return 2;
        }
        if (findContent("我的积分")) {
            return 3;
        }
        if (findContent("我的特币") || findContent("特币超值兑")) {
            return 1;
        }
        if (findContent("我的签到账户")) {
            return 4;
        }
        if (findContent("鸭鸭图鉴")) {
            return 5;
        }
        if (findContent("小鸡3.0首页")) {
            return 6;
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
            return 2000;
        } else if (pageId == 4) {
            return 2000;
        } else if (pageId == 0) {
            return 2000;
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
            return 2000;
        } else if (pageId == 4) {
            return 2000;
        } else if (pageId == 0) {
            return 2000;
        } else if (pageId == -1) {
            return 1000;
        } else {
            return 3000;
        }
    }

    @Override
    protected void getRecognitionResult() {
        String sp_zhuanjinbi = SPUtils.getInstance().getString(Constant.JINGDONG_ZHUANJINBI, "");
        if (!TextUtils.isEmpty(sp_zhuanjinbi)) {
            point_ZhuanJinBi = new Gson().fromJson(sp_zhuanjinbi, Point.class);
        }

    }


    public boolean isCurrentScipte() {
        return getAppInfo().getPkgName().equals(Constant.PN_TAO_TE) ? true : false;
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
                LogUtils.d(TAG, "自动恢复到淘特");
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
                LogUtils.d(TAG, "淘特是不是anr了?");
                dealNoResponse();
                Utils.sleep(2000);
                resumeCount = 0;
                CrashReport.postCatchedException(new Throwable("淘特无响应"));

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
        if (isTodayDone()) {
            task1 = false;
            task2 = false;
            task3 = false;
            task4 = false;
            task5 = false;
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
        if (clickContent("允许")) return true;
        if (clickContent("立即添加")) return true;
        if (clickTotalMatchContent("以后再说")) return true;
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
            tryClickDialog();
        }
        if (samePageCount > 30) {
            clickBack();
            clickBack();
            samePageCount = 0;
        }

    }
}

