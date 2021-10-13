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
import com.ch.model.ScreenShootEvet;
import com.google.gson.Gson;
import com.tencent.bugly.crashreport.CrashReport;

import org.greenrobot.eventbus.EventBus;

import java.util.Random;

import static android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_TAKE_SCREENSHOT;
import static com.ch.core.utils.ActionUtils.pressHome;

public class TouTiaoAdvertScript extends BaseScript {

    private String TAG = this.getClass().getSimpleName();
    private Point point_ShouYe;
    private Point point_RenWu;
    private Point point_KaiBaoXiangDeJinBi;
    private Point point_ShuRuHaoYouYaoQingMa;
    private Point point_ZhanTie;

    private volatile static TouTiaoAdvertScript instance; //声明成 volatile

    public static TouTiaoAdvertScript getSingleton(AppInfo appInfo) {
        if (instance == null) {
            synchronized (TouTiaoAdvertScript.class) {
                if (instance == null) {
                    instance = new TouTiaoAdvertScript(appInfo);
                }
            }
        }
        return instance;
    }

    private boolean adverting = false;

    private int pageId = -1;//0:首页 1:个人中心  2:阅读页  3:广告页
    private int lastPageId = -1; //上次的页面
    private int samePageCount = 0; //同一个页面停留次数

    @Override
    protected boolean isTargetPkg() {
        if (MyApplication.getAppInstance().getAccessbilityService().isWrokFine()) {
            if (!MyApplication.getAppInstance().getAccessbilityService().containsPkg(Constant.PN_TOU_TIAO)) {
                return false;
            }
        }
        return true;
    }

    public TouTiaoAdvertScript(AppInfo appInfo) {
        super(appInfo);
        getRecognitionResult();
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



        pageId = checkPageId();
        if (pageId == lastPageId) {
            samePageCount++;
        } else {
            samePageCount = 0;
        }
        lastPageId = pageId;
        doSamePageDeal();
        LogUtils.d(TAG, "pageId:" + pageId + " gotoPersonCount:" + gotoPersonCount + " samePageCount:" + samePageCount);

        if (clickAdvert()) return;

        if (pageId == 0) {
            if (point_ShouYe == null) {
                getRecognitionResult();
                if (point_ShouYe == null) {
                    EventBus.getDefault().post(new ScreenShootEvet(Constant.PN_TOU_TIAO, Constant.PAGE_MAIN));
                }
                return;
            }

            if (point_RenWu == null) {
                getRecognitionResult();
                if (point_RenWu == null) {
                    EventBus.getDefault().post(new ScreenShootEvet(Constant.PN_TOU_TIAO, Constant.PAGE_MAIN));
                }
                CrashReport.postCatchedException(new Throwable("截图时在任务倒计时中"));
                return;
            }

            doPageId0Things();

        } else if (pageId == 1) {

            if (point_KaiBaoXiangDeJinBi == null) {
                getRecognitionResult();
                if (point_KaiBaoXiangDeJinBi == null) {
                    EventBus.getDefault().post(new ScreenShootEvet(Constant.PN_TOU_TIAO, Constant.PAGE_TASK));
                }
                return;
            }

            doPageId1Things();

        } else if (pageId == 2) {

            doPageId2Things();

        } else if (pageId == 3) {
            baoXiangClickCount = 0;
            doPageId3Things();

        } else {
            if (clickContent("看视频得金币")) Utils.sleep(1500);
            if (clickContent("视频再领")) return;
            if (findContent("邀请码") && findContent("马上提交")) {
                if (autoInvite()) {
                    clickBack();
                    clickBack();
                }
                return;
            }
            Utils.sleep(1500);
            clickBack();
        }

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

        if (samePageCount > 35) {
            clickXY(point_ShouYe.x, point_ShouYe.y);
        }
    }

    private int gotoPersonCount = 0;

    private void doPageId0Things() {
        gotoPersonCount++;
        if (gotoPersonCount > 4) {
            gotoPersonCount = 0;
            clickXY(point_RenWu.x, point_RenWu.y);
            return;
        }
        LogUtils.d(TAG, "doPageId0Things");

        if (clickContent("领金币")) return;

        scrollUp();

        Utils.sleep(3000);


        if (clickContent("0评论")) return;

        if (clickContent("继续阅读")) return;


    }

    int baoXiangClickCount = 0;

    private void doPageId1Things() {
//        count++;

        if(clickContent("点击翻倍"))return;
        if(samePageCount >= 2){
            if (clickContent("我知道了")) ;
            if (clickContent("视频再领")) ;
            if (clickContent("打开签到提醒"));
        }
        if(samePageCount >= 4){
            clickXY(point_ShouYe.x,point_ShouYe.y);
        }
        LogUtils.d(TAG, "doPageId1Things");

        if (!findContent("看广告赚金币")) {
            scrollUpSlow();
            return;
        }

        if (clickContent("填写邀请码")) {
            SPUtils.getInstance().put("invitexy", "");
            SPUtils.getInstance().put("findEdit", "");
            return;
        }

        if (!findContent("已完成 10/10 次")) {
            if (findContent("领福利")) {
                if (clickContent("看广告赚金币")) return;
            }
        }

        if (findContent("分") && findContent("秒")) {
            clickXY(point_ShouYe.x, point_ShouYe.y);
//            if (clickId("k2")) return;
//            clickBack();
        } else {
            clickXY(point_KaiBaoXiangDeJinBi.x, point_KaiBaoXiangDeJinBi.y);
        }

    }

    private void doPageId2Things() {
//        count++;
        LogUtils.d(TAG, "doPageId2Things");

        if (clickContent("领金币")) return;

        scrollUp();

        if (findId("bz")) {
            clickBack();
        }
        if (findContent("已显示全部评论") || findContent("暂无评论，点击抢沙发")) {
            clickBack();
        }

    }

    private void doPageId3Things() {
        LogUtils.d(TAG, "doPageId3Things");
        if (clickContent("继续观看")) return;
//        boolean isAdvert = isAdverting();
//        if (isAdvert) {
//            adverting = isAdvert;
//            return;
//        }
//        if (adverting && !isAdvert) {
//            clickBack();
//            adverting = isAdvert;
//            return;
//        }
//
//        clickBack();


    }

    /**
     * 弹出框里点击看广告
     */
    private boolean clickAdvert() {
        if (clickContent("再看一个获得")) return true;

//        if (clickContent("视频再领")) return true;

        if (clickContent("继续观看")) return true;
        if (clickContent("看视频领")) return true;


        return false;
    }

    /**
     * 检查是在那个页面
     *
     * @return //0:首页 1:个人中心  2:阅读页  3:广告页
     */
    private int checkPageId() {
//        if (findId("k2") && findContent("频道管理")&& findContent("发布")) {
        if (findContent("频道管理") && findContent("发布")) {
            return 0;
        }

        if (findContent("搜索") || findContent("更多操作")) {
            return 2;
        }

        if ((findContent("s后可领取"))) {
            return 3;
        }

        if ((findContent("s") && findContent("关闭")) || findContent("试玩")) {
            return 3;
        }

        if ((findContent("日常任务") || findContent("看广告赚金币")) && findContent("金币")) {
            return 1;
        }
        return -1;
    }


    @Override
    protected int getMinSleepTime() {
        if (pageId == 2) {
            return 1000;
        } else if (pageId == 1) {
            return 2000;
        } else if (pageId == -1) {
            return 1000;
        } else {
            return 4000;
        }

    }

    @Override
    protected int getMaxSleepTime() {
        if (pageId == 2) {
            return 1000;
        } else if (pageId == 1) {
            return 2000;
        } else if (pageId == -1) {
            return 1000;
        } else {
            return 4000;
        }
    }

    @Override
    protected void getRecognitionResult() {
        String sp_shouye = SPUtils.getInstance().getString(Constant.TOUTIAO_SHOUYE, "");
        if (!TextUtils.isEmpty(sp_shouye)) {
            point_ShouYe = new Gson().fromJson(sp_shouye, Point.class);
        }

        String sp_renwu = SPUtils.getInstance().getString(Constant.TOUTIAO_RENWU, "");
        if (!TextUtils.isEmpty(sp_renwu)) {
            point_RenWu = new Gson().fromJson(sp_renwu, Point.class);
        }

        String sp_kaibaoxiangdejinbi = SPUtils.getInstance().getString(Constant.TOUTIAO_KAIBAOXIANGDEJINBI, "");
        if (!TextUtils.isEmpty(sp_kaibaoxiangdejinbi)) {
            point_KaiBaoXiangDeJinBi = new Gson().fromJson(sp_kaibaoxiangdejinbi, Point.class);
        }

        String sp_shuruhaoyouyaoqingma = SPUtils.getInstance().getString(Constant.TOUTIAO_SHURUHAOYOUYAOQINGMA, "");
        if (!TextUtils.isEmpty(sp_shuruhaoyouyaoqingma)) {
            point_ShuRuHaoYouYaoQingMa = new Gson().fromJson(sp_shuruhaoyouyaoqingma, Point.class);
        }

        String sp_zhantie = SPUtils.getInstance().getString(Constant.TOUTIAO_ZHANTIE, "");
        if (!TextUtils.isEmpty(sp_zhantie)) {
            point_ZhanTie = new Gson().fromJson(sp_zhantie, Point.class);
        }
    }


    public boolean isCurrentScipte() {
        return getAppInfo().getPkgName().equals(Constant.PN_TOU_TIAO) ? true : false;
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
            if (resumeCount > 20) {
                LogUtils.d(TAG, "自动恢复到头条极速版");
                CrashReport.postCatchedException(new Throwable("自动恢复到头条极速版"));
                startApp();
            }
            if (resumeCount > 30) {
                if (BuildConfig.DEBUG) {
                    MyApplication.getAppInstance().getAccessbilityService().performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT);
                }
                LogUtils.d(TAG, "头条极速版是不是anr了?");
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
            clickBack();
            Utils.sleep(100);
            clickBack();
            Utils.sleep(1000);
        }
        pressHome();

        stop = true;
    }

    /**
     * 可能是有弹出框，先做返回处理
     *
     * @return
     */
    private boolean dealNoResponse1() {
        clickBack();
        return false;
    }

    /**
     * 处理返回解决不了的弹出框，但是能找到资源的
     *
     * @return
     */
    private boolean dealNoResponse2() {
        if (clickContent("网络不给力，点击屏幕重试")) return true;
        if (clickContent("禁止")) return true;
        if (clickContent("知道")) return true;
        if (clickContent("去赚钱")) return true;
        if (clickContent("视频再领")) return true;
        if (clickContent("仅在使用中允许")) return true;
        if (clickContent("立即添加")) return true;
        if (clickContent("关闭")) return true;
        if (clickContent("重试")) return true;
        if (clickContent("取消")) return true;
        if (clickContent("开心收下")) return true;
        return false;
    }


    /**
     * 处理不在该app时的处理，比如系统弹出框
     *
     * @return
     */
    private boolean dealNoResponse() {
        if (clickContent("本次运行允许")) return true;
        if (clickContent("仅在使用中允许")) return true;
        if (clickContent("始终允许")) return true;
        if (clickContent("禁止")) return true;

        if (clickContent("关闭")) return true;
        if (clickContent("重试")) return true;
        if (clickContent("取消")) return true;
        if (clickContent("知道")) return true;

        clickXY(point_ShouYe.x, point_ShouYe.y);
        ActionUtils.pressHome();
        return false;
    }

    private boolean autoInvite() {
        getRecognitionResult();
        if (null == point_ShuRuHaoYouYaoQingMa) {
            SPUtils.getInstance().put(Constant.TOUTIAO_ZHANTIE, "");
        }

        if (null != point_ZhanTie) {
            clickXY(point_ZhanTie.x, point_ZhanTie.y);
            Utils.sleep(1000);

            clickContent("马上提交");
            Utils.sleep(2000);
            CrashReport.postCatchedException(new Throwable("头条自动填写邀请码成功"));
            return true;
        }

        if (null != point_ShuRuHaoYouYaoQingMa) {
            ActionUtils.longPress(point_ShuRuHaoYouYaoQingMa.x, point_ShuRuHaoYouYaoQingMa.y);
            Utils.sleep(1500);
            EventBus.getDefault().post(new ScreenShootEvet(Constant.PN_TOU_TIAO, Constant.PAGE_INVITE));
        } else {
            EventBus.getDefault().post(new ScreenShootEvet(Constant.PN_TOU_TIAO, Constant.PAGE_INVITE));
            return false;
        }
        return false;
    }
}

