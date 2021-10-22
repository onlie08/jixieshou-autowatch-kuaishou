package com.ch.scripts;

import android.graphics.Point;
import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.ch.application.MyApplication;
import com.ch.core.search.node.NodeInfo;
import com.ch.core.utils.ActionUtils;
import com.ch.core.utils.Constant;
import com.ch.core.utils.Utils;
import com.ch.jixieshou.BuildConfig;
import com.ch.model.AppInfo;
import com.ch.model.RecognitionBean;
import com.ch.model.ScreenShootEvet;
import com.google.gson.Gson;
import com.tencent.bugly.crashreport.CrashReport;

import org.greenrobot.eventbus.EventBus;

import java.util.Random;

import static android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_TAKE_SCREENSHOT;
import static com.ch.core.utils.ActionUtils.click;
import static com.ch.core.utils.ActionUtils.pressHome;

public class AiQiYiAdvertScript extends BaseScript {

    private String TAG = this.getClass().getSimpleName();

    private Point point_ShouYe;
    private Point point_ZhuanQian;
    private Point point_TianXieHaoYouYaoQingMa;
    private Point point_ZhanTie;
    private Point point_LiJiKaiYun;

    private volatile static AiQiYiAdvertScript instance; //声明成 volatile

    public static AiQiYiAdvertScript getSingleton(AppInfo appInfo) {
        if (instance == null) {
            synchronized (AiQiYiAdvertScript.class) {
                if (instance == null) {
                    instance = new AiQiYiAdvertScript(appInfo);
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
            if (!MyApplication.getAppInstance().getAccessbilityService().containsPkg(Constant.PN_AI_QI_YI)) {
                return false;
            }
        }
        return true;
    }

    public AiQiYiAdvertScript(AppInfo appInfo) {
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
                    EventBus.getDefault().post(new ScreenShootEvet(Constant.PN_AI_QI_YI, Constant.PAGE_MAIN));
                }
                return;
            }

            if (point_ZhuanQian == null) {
                getRecognitionResult();
                if (point_ZhuanQian == null) {
                    EventBus.getDefault().post(new ScreenShootEvet(Constant.PN_AI_QI_YI, Constant.PAGE_MAIN));
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

            doPageId4Things();

        } else if (pageId == 5) {

            doPageId5Things();

        } else {
            if (samePageCount >= 2) {
                scrollDown();
                Utils.sleep(2000);
                if(clickContent("刷新页面"))return;

            }
            Utils.sleep(1500);
            clickBack();
        }

    }



    private void doPageId5Things() {
        if (autoInvite()) {
            clickBack();
        }
    }

    private void doPageId4Things() {
        scrollUp();
    }


    private void doPageId0Things() {
        if (clickContent("领金币")) return;
        if (!advertDone4) {
            if (clickContent("赚金币")) {
                Utils.sleep(1500);
                if (clickContent("明天可再来领取哦")) {
                    advertDone4 = true;
                }
                return;
            }
        }

//        if (clickContent("开宝箱")) return;

        clickXY(point_ZhuanQian.x, point_ZhuanQian.y);
    }

    boolean advertDone3 = false;
    boolean advertDone4 = false;

    private void doPageId1Things() {
        if (!findContent("已完成")) {
            if (clickContent("填写邀请码奖励")) {
                SPUtils.getInstance().put(Constant.AIQIYI_TIANXIEHAOYOUYAOQINGMA, "");
                SPUtils.getInstance().put(Constant.AIQIYI_ZHANTIE, "");
                Utils.sleep(1000);
                return;
            }
        }


        LogUtils.d(TAG, "doPageId1Things");

        if (!findContent("明日可领")) {
            if (clickContent("开宝箱领金币")) return;
            return;
        }


        if (!findContent("1000金币轻松赚")) {
            scrollUpSlow();
            return;
        }
        if (!findContent("今日进度10/10")) {
            if (clickContent("1000金币轻松赚")) return;
        }

        if (!advertDone3) {
            if (!findContent("超级大转盘")) {
                scrollUpSlow();
                return;
            }
            if (clickContent("超级大转盘")) return;
        }

        if (!findContent("看电视剧广告赚")) {
            scrollUpSlow();
            return;
        }

        if (clickContent("看电视剧广告赚")) {
            Utils.sleep(1500);
            if(clickContent("点击开始赚钱")){
                Utils.sleep(2000);
                clickXY(500,500);
            }else {
                Utils.sleep(2000);
                clickXY(500,500);
            }
            return;
        }

    }

    private void doPageId2Things() {
        LogUtils.d(TAG, "doPageId2Things");
        if (clickContent("继续观看")) return;
    }

    private void doPageId3Things() {
        if (findContent("第二天再来")) {
            advertDone3 = true;
            clickBack();
            return;
        }
        if (findContent("后再来")) {
            advertDone3 = true;
            clickBack();
            return;
        }

        getRecognitionResult();
        if (point_LiJiKaiYun == null) {
            EventBus.getDefault().post(new ScreenShootEvet(Constant.PN_AI_QI_YI, Constant.PAGE_ADVERT));
            return;
        }
        clickXY(point_LiJiKaiYun.x, point_LiJiKaiYun.y);
//        dealNoResponse3();
    }

    /**
     * 弹出框里点击看广告
     */
    private boolean clickAdvert() {
        if (clickContent("再赚")) return true;
        if (clickContent("继续赚金币")) return true;

        return false;
    }

    /**
     * 检查是在那个页面
     *
     * @return //0:首页 1:个人中心  2:阅读页  3:广告页
     */
    private int checkPageId() {
        if (findContent("电视剧") && findContent("好片快看")) {
            return 0;
        }

        if (findContent("幸运大转盘")) {
            return 3;
        }

        if (findContent("s后可领取奖励")) {
            return 2;
        }

        if (findContent("活动规则") && findContent("金币")) {
            return 1;
        }

        if (findContent("看广告赚金币") || findContent("看广告赚10倍金币")) {
            return 4;
        }
        if (findContent("只支持填写数字")) {
            return 5;
        }

        return -1;
    }

    //0:首页 1:个人中心  2:广告页 3：幸运大转盘 4:看广告赚金币
    @Override
    protected int getMinSleepTime() {
        if (pageId == 2) {
            return 3000;
        } else if (pageId == 1) {
            return 2000;
        } else if (pageId == 3) {
            return 3000;
        } else if (pageId == 4) {
            return 20000;
        } else if (pageId == -1) {
            return 1000;
        }else {
            return 2000;
        }

    }

    @Override
    protected int getMaxSleepTime() {
        if (pageId == 2) {
            return 3000;
        } else if (pageId == 1) {
            return 2000;
        } else if (pageId == 3) {
            return 3000;
        } else if (pageId == 4) {
            return 20000;
        }else if (pageId == -1) {
            return 1000;
        } else {
            return 2000;
        }
    }

    @Override
    protected void getRecognitionResult() {
        String sp_shouye = SPUtils.getInstance().getString(Constant.AIQIYI_SHOUYE, "");
        if (!TextUtils.isEmpty(sp_shouye)) {
            point_ShouYe = new Gson().fromJson(sp_shouye, Point.class);
        }
        String sp_zhuanqian = SPUtils.getInstance().getString(Constant.AIQIYI_ZHUANQIAN, "");
        if (!TextUtils.isEmpty(sp_zhuanqian)) {
            point_ZhuanQian = new Gson().fromJson(sp_zhuanqian, Point.class);
        }
        String sp_tianxiehaoyouyaoqingma = SPUtils.getInstance().getString(Constant.AIQIYI_TIANXIEHAOYOUYAOQINGMA, "");
        if (!TextUtils.isEmpty(sp_tianxiehaoyouyaoqingma)) {
            point_TianXieHaoYouYaoQingMa = new Gson().fromJson(sp_tianxiehaoyouyaoqingma, Point.class);
        }

        String sp_zhantie = SPUtils.getInstance().getString(Constant.AIQIYI_ZHANTIE, "");
        if (!TextUtils.isEmpty(sp_zhantie)) {
            point_ZhanTie = new Gson().fromJson(sp_zhantie, Point.class);
        }

        String sp_lijikaiyun = SPUtils.getInstance().getString(Constant.AIQIYI_LIJIKAIYUN, "");
        if (!TextUtils.isEmpty(sp_lijikaiyun)) {
            point_LiJiKaiYun = new Gson().fromJson(sp_lijikaiyun, Point.class);
        }
    }


    public boolean isCurrentScipte() {
        return getAppInfo().getPkgName().equals(Constant.PN_AI_QI_YI) ? true : false;
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
                LogUtils.d(TAG, "爱奇艺极速版是不是anr了?");
                dealNoResponse();
                Utils.sleep(1000);
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
     * 处理返回解决不了的弹出框，但是能找到资源的
     *
     * @return
     */
    private boolean dealNoResponse2() {
        if (clickContent("知道")) return true;
        if (clickContent("继续赚金币")) return true;
        if (clickContent("去赚钱")) return true;
        if (clickContent("仅在使用中允许")) return true;
        if (clickContent("立即添加")) return true;
        if (clickContent("关闭")) return true;
        if (clickContent("重试")) return true;
        if (clickContent("取消")) return true;
        if (clickContent("我的收益")) return true;
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

        if (clickId("k2")) return true;

        return false;
    }

    private boolean autoInvite() {
        //[50,718][1150,838]
        getRecognitionResult();
        if (null == point_TianXieHaoYouYaoQingMa) {
            SPUtils.getInstance().put(Constant.AIQIYI_ZHANTIE, "");
        }
        if (null != point_ZhanTie) {
            clickXY(point_ZhanTie.x, point_ZhanTie.y);
            Utils.sleep(1000);

            clickContent("提交");
            Utils.sleep(2000);
            SPUtils.getInstance().put(Constant.AIQIYI_INVITE_SUCCESS, true);
            CrashReport.postCatchedException(new Throwable("爱奇艺自动填写邀请码成功"));
            return true;
        }

        if (null != point_TianXieHaoYouYaoQingMa) {
            ActionUtils.longPress(point_TianXieHaoYouYaoQingMa.x, point_TianXieHaoYouYaoQingMa.y);
            Utils.sleep(1500);
            EventBus.getDefault().post(new ScreenShootEvet(Constant.PN_AI_QI_YI, Constant.PAGE_INVITE));
        } else {
            EventBus.getDefault().post(new ScreenShootEvet(Constant.PN_AI_QI_YI, Constant.PAGE_INVITE));
            return false;
        }
        return false;
    }

//    private void setText(){
//        AccessibilityNodeInfo textInfo = service.findFirst(AbstractTF.newClassName(ST_EDITTEXT));//假设只有一个edittext
//        if (textInfo != null) {
//            Bundle arguments = new Bundle();
//            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, "文本内容");
//            textInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
//        }
//    }

    @Override
    protected void doSamePageDeal() {
        if (samePageCount > 10 && samePageCount < 13) {
            Utils.sleep(1500);
            clickBack();
        }

        if (samePageCount > 12 && samePageCount < 16) {
            advertDone3 = false;
            dealNoResponse2();
        }

        if (samePageCount > 15) {
            doRandomClick();
        }

    }
}

