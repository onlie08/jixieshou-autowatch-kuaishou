package com.ch.scripts;

import android.graphics.Point;
import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.ch.application.MyApplication;
import com.ch.core.utils.Constant;
import com.ch.core.utils.Utils;
import com.ch.jixieshou.BuildConfig;
import com.ch.model.AppInfo;
import com.ch.model.ScreenShootEvet;
import com.google.gson.Gson;
import com.tencent.bugly.crashreport.CrashReport;

import org.greenrobot.eventbus.EventBus;

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

        }
        else if (pageId == 1) {

            doPageId1Things();

        }
        else if (pageId == 2) {

            doPageId2Things();

        }
        else if (pageId == 3) {

            doPageId3Things();

        }
        else {
            if(samePageCount > 5){
                if(clickTotalMatchContent("推荐")) Utils.sleep(3000);
            }
            Utils.sleep(1500);
            clickBack();
        }

    }

    private void doPageId0Things() {
        LogUtils.d(TAG, "doPageId0Things");
        if(clickContent("天天赚特币"))return;

    }

    boolean task1 = false;
    boolean task2 = false;
    boolean task3 = false;
    boolean task4 = false;
    boolean task5 = false;
    private void doPageId1Things() {
        LogUtils.d(TAG, "doPageId1Things");
        if(samePageCount > 2){
            clickBack();
            return;
        }
        if(clickContent("立即重试"))return;
        if(clickContent("放弃膨胀 领取"))return;

        if(clickTotalMatchContent("可开红包")){
            Utils.sleep(3000);
            clickTotalMatchContent("立即抽奖");
            Utils.sleep(5000);
            return;
        }
        if(clickContent("立即领币"))return;
        if(clickContent("继续赚币"))return;
        return;
    }

    private void doPageId2Things() {
        LogUtils.d(TAG, "doPageId2Things");

        if(!task1){
            if(clickContent("看直播福利最高赚")){
                doScan(35);
                Utils.sleep(3000);
                task1 = true;
                return;
            }
        }

        if(!task2){
            if(clickContent("逛1元秒杀最高赚")){
                doScan(16);
                Utils.sleep(3000);
                task2 = true;
                return;
            }
        }

        if(!task3){
            if(clickContent("逛品质好物最高赚")){
                doScan(16);
                Utils.sleep(3000);
                task3 = true;
                return;
            }
        }

        if(!task4){
            if(clickContent("逛超值抵扣最高赚")){
                doScan(35);
                Utils.sleep(3000);
                task4 = true;
                return;
            }
        }

        if(!task5){
            if(clickContent("逛30%抵扣最高")){
                doScan(16);
                Utils.sleep(3000);
                task5 = true;
                return;
            }
        }
        if(checkDone()){
            setTodayDone(true);
            return;
        }
        scrollUpSlow();
    }

    private boolean checkDone() {
        if(task1 && task2 && task3 && task4 && task5){
            return true;
        }
        return false;
    }

    private void doScan(int second){
        for(int i = 0;i<second;i++){
            if(isCurrentScipte()){
                Utils.sleep(1000);
                scrollUp();
            }

        }
    }

    private void doPageId3Things() {
        LogUtils.d(TAG, "doPageId3Things");
        if(findContent("今日已完成")){
            while (checkPageId() != 2 && isTargetPkg()){
                clickBack();
                Utils.sleep(1500);
            }
            return;
        }
        if(clickContent("点击逛下一个")){
            samePageCount = 0;
            return;
        }
       scrollUp();

    }

    private void doPageId4Things() {
        LogUtils.d(TAG, "doPageId4Things");

    }

    private void doPageId5Things() {
        LogUtils.d(TAG, "doPageId5Things");
        if(autoInvite()){
            clickBack();
        }
    }

    /**
     * 弹出框里点击看广告
     */
    private boolean clickAdvert() {
        if (clickContent("视频再")) return true;
        if (clickContent("再看一个")) return true;
        if (clickContent("看广告再得")) return true;
//        if (clickContent("继续赚金币")) return true;

        return false;
    }

    /**
     * 检查是在那个页面
     *
     * @return //0:首页 1:个人中心  2:阅读页  3:广告页
     */
    private int checkPageId() {
        if(findContent("搜索") && findContent("天天赚特币")){
            return 0;
        }
        if(findContent("今日已完成任务")){
            return 2;
        }
        if(findContent("我的特币") || findContent("特币超值兑")){
            return 1;
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
        }else if (pageId == 0) {
            return 2000;
        }else if (pageId == -1) {
            return 1000;
        } else {
            return 3000;
        }
    }

    @Override
    protected void getRecognitionResult() {
        String sp_zhuanjinbi = SPUtils.getInstance().getString(Constant.JINGDONG_ZHUANJINBI,"");
        if(!TextUtils.isEmpty(sp_zhuanjinbi)){
            point_ZhuanJinBi = new Gson().fromJson(sp_zhuanjinbi,Point.class);
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
                LogUtils.d(TAG, "自动恢复到头条极速版");
                CrashReport.postCatchedException(new Throwable("自动恢复到头条极速版"));
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
        if(isTodayDone()){
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


    private boolean autoInvite() {
        if(true){
            return true;
        }
        //[50,718][1150,838]
        getRecognitionResult();

//        if(null == point_TianXieYaoQingMa1){
//            SPUtils.getInstance().put(Constant.BAIDU_TIANXIEYAOQINGMA2, "");
//            SPUtils.getInstance().put(Constant.BAIDU_TIANXIEYAOQINGMA3, "");
//            SPUtils.getInstance().put(Constant.BAIDU_ZHANTIE, "");
//        }

        if(null != point_ZhanTie){
            clickXY(point_ZhanTie.x, point_ZhanTie.y);
            Utils.sleep(1000);
            clickXY(point_TianXieYaoQingMa3.x, point_TianXieYaoQingMa3.y);
            Utils.sleep(2000);
            CrashReport.postCatchedException(new Throwable("京东自动填写邀请码成功"));
        }

//        if(null == point_TianXieYaoQingMa1){
//            EventBus.getDefault().post(new ScreenShootEvet(Constant.PN_TAO_TE,Constant.PAGE_INVITE));
//            return false;
//        }
//
//        if(null != point_TianXieYaoQingMa1 && point_TianXieYaoQingMa2 == null){
//            clickXY(point_TianXieYaoQingMa1.x, point_TianXieYaoQingMa1.y);
//            Utils.sleep(1000);
//            EventBus.getDefault().post(new ScreenShootEvet(Constant.PN_TAO_TE,Constant.PAGE_INVITE));
//        }
//
//        if(null != point_TianXieYaoQingMa2 && point_TianXieYaoQingMa3 != null){
//            ActionUtils.longPress(point_TianXieYaoQingMa2.x, (point_TianXieYaoQingMa3.y + point_TianXieYaoQingMa2.y)/2);
//            Utils.sleep(1500);
//            EventBus.getDefault().post(new ScreenShootEvet(Constant.PN_TAO_TE,Constant.PAGE_INVITE));
//        }
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

