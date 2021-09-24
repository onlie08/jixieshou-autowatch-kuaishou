package com.ch.scripts;

import android.graphics.Point;
import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.ch.application.MyApplication;
import com.ch.core.utils.ActionUtils;
import com.ch.core.utils.Constant;
import com.ch.core.utils.Utils;
import com.ch.jixieshou.BuildConfig;
import com.ch.model.AppInfo;
import com.ch.model.RecognitionBean;
import com.ch.model.ScreenShootEvet;
import com.google.gson.Gson;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.commonsdk.debug.E;

import org.greenrobot.eventbus.EventBus;

import java.util.Random;

import static android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_TAKE_SCREENSHOT;
import static com.ch.core.utils.ActionUtils.pressHome;

/**
 * 点淘急速版脚本
 */
public class DianTaoFastScript extends BaseScript {
    private String TAG = this.getClass().getSimpleName();
    private volatile static DianTaoFastScript instance; //声明成 volatile

    public static DianTaoFastScript getSingleton(AppInfo appInfo) {
        if (instance == null) {
            synchronized (AiQiYiAdvertScript.class) {
                if (instance == null) {
                    instance = new DianTaoFastScript(appInfo);
                }
            }
        }
        return instance;
    }

    private int pageId = -1;//0:首页 1:个人中心  2:直播页 3:邀请码
    private int lastPageId = -1; //上次的页面
    private int samePageCount = 0; //同一个页面停留次数

    private Point point_RenWu;
    private Point point_ShuruYaoQingMa;
    private Point point_ZhanTie;

    public DianTaoFastScript(AppInfo appInfo) {
        super(appInfo);
        getRecognitionResult();
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

        if (samePageCount > 10 && samePageCount < 13) {
            Utils.sleep(1500);
            clickBack();
        }

        if (samePageCount > 12 && samePageCount < 16) {
            dealNoResponse2();

        }

        if (samePageCount > 15) {
            dealNoResponse3();
        }

        if (doTask()) return;

        pageId = checkPageId();
        if (pageId == lastPageId) {
            samePageCount++;
        } else {
            samePageCount = 0;
        }
        lastPageId = pageId;
        LogUtils.d(TAG, "pageId:" + pageId + " samePageCount:" + samePageCount);

        if (pageId == 0) {

            doPageId0Things();

        } else if (pageId == 1) {
            if (point_RenWu == null) {
                getRecognitionResult();
                if (point_RenWu == null) {
                    EventBus.getDefault().post(new ScreenShootEvet(Constant.PN_DIAN_TAO,Constant.PAGE_TASK));
                }
                CrashReport.postCatchedException(new Throwable("截图时在任务倒计时中"));
                return;
            }

            doPageId1Things();

        } else if (pageId == 2) {

            doPageId2Things();

        } else if (pageId == 3) {

            doPageId3Things();

        } else {
            Utils.sleep(1500);
            clickBack();
//            dealNoResponse();
        }
//        dealNoResponse();
    }

    boolean editPage = false;

    private void doPageId3Things() {
        if (findContent("抱歉 你已经抽过奖了")) {
            SPUtils.getInstance().put("dt_invite", true);
            clickBack();
            return;
        }

        if (!editPage) {
            if (clickContent("填写邀请码立即抽奖")) {
                SPUtils.getInstance().put(Constant.DIANTAO_SHURUYAOQINGMA, "");
                SPUtils.getInstance().put(Constant.DIANTAO_ZHANTIE, "");
                editPage = true;
                return;
            }
        }

        if (findContent("提交 去抽奖")) {
            if (autoInvite()) {
                return;
            }
            return;
        }

    }

    private boolean doTask() {
        if (clickContent("继续做任务")) return true;

        if (clickContent("去看直播赚")) return true;

        return false;
    }

    @Override
    protected boolean isTargetPkg() {
        if (MyApplication.getAppInstance().getAccessbilityService().isWrokFine()) {
            if (!MyApplication.getAppInstance().getAccessbilityService().containsPkg(Constant.PN_DIAN_TAO)) {
                return false;
            }
        }
        return true;
    }

    private void doPageId0Things() {

        if (clickId("gold_common_image")) return;

    }

    private void doPageId1Things() {
        if (!findContent("今日签到")) {
            scrollUpSlow();
            return;
        }

        if(!findContent("00:")){
            clickXY(point_RenWu.x,point_RenWu.y);
        }
//        if (!findContent("待开奖")) {
//            if (clickContent("今日签到")) return;
//        }

        if (!SPUtils.getInstance().getBoolean("dt_invite", false)) {
            if (clickContent("新人填写邀请码")){
                editPage =false;
                return;
            }
        }

        if (!findContent("看直播，赚元宝")) {
            scrollUpSlow();
            return;
        }
        if (clickContent("看直播，赚元宝")) return;

    }

    int timeCount = 0;

    private void doPageId2Things() {
        if (findContent("6/6")) {
            timeCount++;
            if (timeCount > 4) {
                if (clickId("gold_turns_container")) return;
            }
        } else {
            timeCount = 0;
        }
    }

    private void dealNoResponse() {
        if (clickContent("允许")) return;
        if (clickContent("取消")) return;
        if (clickContent("知道")) return;
        if (clickContent("知道")) return;
        if (clickContent("添加")) return;
        if (clickContent("关闭")) return;
        if (clickContent("重试")) return;

    }


    /**
     * 检查是在那个页面
     *
     * @return //0:首页 1:个人中心  2:直播
     */
    private int checkPageId() {
        if (findId("homepage_container") && findId("gold_common_image")) {
            return 0;
        }

        if (findContent("元宝中心") && findContent("我的成就")) {
            return 1;
        }

        if (findId("taolive_room_watermark_text")) {
            return 2;
        }

        if (findContent("填邀请码 赚元宝")) {
            return 3;
        }

        return -1;
    }


    @Override
    protected int getMinSleepTime() {
        return 5000;
    }

    @Override
    protected int getMaxSleepTime() {
        return 5000;
    }




    public boolean isCurrentScipte() {
        return getAppInfo().getPkgName().equals(Constant.PN_DIAN_TAO) ? true : false;
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
            if (resumeCount > 50) {
                LogUtils.d(TAG, "自动恢复到点淘");
                CrashReport.postCatchedException(new Throwable("自动恢复到点淘"));
                startApp();
            }
            if (resumeCount > 60) {
                if (BuildConfig.DEBUG) {
                    MyApplication.getAppInstance().getAccessbilityService().performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT);
                }
                LogUtils.d(TAG, "点淘极速版是不是anr了?");
                dealNoResponse();
                clickBack();
            }
            return false;
        }
        resumeCount = 0;
        return true;
    }

    @Override
    public void destory() {
        if (isTargetPkg()) {
            pressHome();
//            clickBack();
//            clickBack();
        }
        stop = true;
    }

    /**
     * 处理返回解决不了的弹出框，但是能找到资源的
     *
     * @return
     */
    private boolean dealNoResponse2() {
        if (clickContent("知道")) return true;
        if (clickContent("继续赚金币")) return true;
        if (clickContent("去赚钱")) return true;
        if (clickContent("允许")) return true;
        if (clickContent("立即添加")) return true;
        if (clickContent("关闭")) return true;
        if (clickContent("重试")) return true;
        if (clickContent("取消")) return true;
        return false;
    }

    /**
     * 处理返回解决不了的弹出框，而且也不能找到资源的
     *
     * @return
     */
    private boolean dealNoResponse3() {
        int height = ScreenUtils.getScreenHeight();
        int height1 = height / 20;
        int width = ScreenUtils.getScreenWidth();
        Random rand = new Random();
        int randHeight = 20 + rand.nextInt(height1 - 20);
        LogUtils.d(TAG, "x:" + (width / 2) + " y:" + (randHeight * 20));
        clickXY(width / 2, randHeight * 20);
        return false;
    }


    private boolean autoInvite() {
        //[50,718][1150,838]
        getRecognitionResult();
        if(null == point_ShuruYaoQingMa){
            SPUtils.getInstance().put(Constant.DIANTAO_ZHANTIE, "");
        }
        if(null != point_ZhanTie){
            clickXY(point_ZhanTie.x, point_ZhanTie.y);
            Utils.sleep(1000);

            clickContent("提交 去抽奖");
            Utils.sleep(2000);
            CrashReport.postCatchedException(new Throwable("头条自动填写邀请码成功"));
            return true;
        }

        if(null != point_ShuruYaoQingMa){
            ActionUtils.longPress(point_ShuruYaoQingMa.x, point_ShuruYaoQingMa.y);
            Utils.sleep(1500);
            EventBus.getDefault().post(new ScreenShootEvet(Constant.PN_DIAN_TAO,Constant.PAGE_INVITE));
        }else {
            EventBus.getDefault().post(new ScreenShootEvet(Constant.PN_DIAN_TAO,Constant.PAGE_INVITE));
            return false;
        }
        return false;
    }

    @Override
    protected void getRecognitionResult() {
        String sp_renwu = SPUtils.getInstance().getString(Constant.DIANTAO_RENWU,"");
        if(!TextUtils.isEmpty(sp_renwu)){
            point_RenWu = new Gson().fromJson(sp_renwu,Point.class);
        }

        String sp_shuruyaoqingma = SPUtils.getInstance().getString(Constant.DIANTAO_SHURUYAOQINGMA,"");
        if(!TextUtils.isEmpty(sp_shuruyaoqingma)){
            point_ShuruYaoQingMa = new Gson().fromJson(sp_shuruyaoqingma,Point.class);
        }

        String sp_zhantie = SPUtils.getInstance().getString(Constant.DIANTAO_ZHANTIE,"");
        if(!TextUtils.isEmpty(sp_zhantie)){
            point_ZhanTie = new Gson().fromJson(sp_zhantie,Point.class);
        }
    }

}
