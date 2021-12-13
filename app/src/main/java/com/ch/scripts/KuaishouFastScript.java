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

import static android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_TAKE_SCREENSHOT;
import static com.ch.core.utils.ActionUtils.pressHome;

/**
 * 快手急速版脚本
 */
public class KuaishouFastScript extends BaseScript {
    private String TAG = this.getClass().getSimpleName();

    private Point point_XiangHaoYouXunWenYaoQingMa;
    private Point point_ZhanTie;


    private volatile static KuaishouFastScript instance; //声明成 volatile

    public static KuaishouFastScript getSingleton(AppInfo appInfo) {
        if (instance == null) {
            synchronized (KuaishouFastScript.class) {
                if (instance == null) {
                    instance = new KuaishouFastScript(appInfo);
                }
            }
        }
        return instance;
    }


    private int pageId = -1;//0:首页 1:个人中心  2:广告页 3:填写邀请码
    private int lastPageId = -1; //上次的页面
    private int samePageCount = 0; //同一个页面停留次数

    @Override
    protected boolean isTargetPkg() {
        if (MyApplication.getAppInstance().getAccessbilityService().isWrokFine()) {
            if (!MyApplication.getAppInstance().getAccessbilityService().containsPkg(Constant.PN_KUAI_SHOU)) {
                return false;
            }
        }
        return true;
    }

    // 是否有检查"我知道了"

    public KuaishouFastScript(AppInfo appInfo) {
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

            doPageId0Things();

        } else if (pageId == 1) {

            doPageId1Things();

        } else if (pageId == 2) {

            doPageId2Things();

        } else if (pageId == 3) {

            doPageId3Things();

        } else {
            Utils.sleep(1500);
            clickBack();
        }
    }


    /**
     * 处理返回解决不了的弹出框，但是能找到资源的
     *
     * @return
     */
    private boolean dealNoResponse2() {
        if (clickId("看视频最多赚")) return true;
        if (clickId("close")) return true;
        if (clickContent("查看收益")) return true;
        if (clickContent("知道")) return true;
        if (clickContent("立即签到")) return true;
        if (clickContent("签到立得")) return true;
        if (clickTotalMatchContent("以后再说")) return true;
        if (clickContent("补签再得")) return true;
        if (clickContent("允许")) return true;
        if (clickContent("立即添加")) return true;
        if (clickContent("关闭")) return true;
        if (clickContent("重试")) return true;
        if (clickContent("取消")) return true;
        if (clickContent("确认")) return true;
        if (clickContent("邀请好友赚更多")) return true;
        return false;
    }


    private void doPageId3Things() {
        if (autoInvite()) {
            clickBack();
            clickBack();
        }
    }

    private void doPageId0Things() {
        if (samePageCount > 3) {
            if (clickId("red_packet_anim")) return;
        }

        scrollUp();
    }


    private void doPageId1Things() {
        if (samePageCount > 2) {
//            if(clickContent(""))return;
            if (clickContent("明天继续领现金")) return;
            if (clickContent("立即领取今日现金")) return;
            if (clickContent("都领完了，继续赚钱")) return;
//            if (clickContent("立即签到")) return;
        }

        if (clickContent("填邀请码必得1元")) {
            return;
        }

        if (!findContent("看广告")) {
            scrollUpSlow();
            return;
        }

        if (clickContent("开宝箱得金币")) return;

        if (clickContent("观看广告单日最高")) return;

        if (clickTotalMatchContent("立即签到")) return;
        if (findContent("明天再来") && findContent("明日再来")) {
            setTodayDone(true);
            CrashReport.postCatchedException(new Exception("快手极速版今日任务完成"));
        }
        clickBack();

    }

    private void doPageId2Things() {
        if (clickContent("继续观看")) return;
    }

    @Override
    protected int getMinSleepTime() {
        if (pageId == -1) {
            return 1000;
        } else if (pageId == 0) {
            return 2000;
        } else if (pageId == 1) {
            return 2000;
        }
        return 3000;
    }

    @Override
    protected int getMaxSleepTime() {
        if (pageId == -1) {
            return 1000;
        } else if (pageId == 0) {
            return 4000;
        } else if (pageId == 1) {
            return 2000;
        }
        return 3000;
    }


    public boolean isCurrentScipte() {
        return getAppInfo().getPkgName().equals(Constant.PN_KUAI_SHOU) ? true : false;
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
                LogUtils.d(TAG, "自动恢复到快手极速版");
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
                CrashReport.postCatchedException(new Throwable("快手极速版无响应"));

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
     * 弹出框里点击看广告
     */
    private boolean clickAdvert() {
        if (clickContent("视频再赚")) return true;
        if (clickContent("视频就赚")) return true;
        if (clickContent("看视频最高得")) return true;
        if (clickContent("看精彩视频赚更多")) return true;
        if (clickContent("视频再领")) return true;
        if (clickContent("看广告再得")) return true;
        if (clickContent("继续观看")) return true;
        return false;
    }

    /**
     * 检查是在那个页面
     *
     * @return //0:首页 1:个人中心  2:阅读页  3:广告页
     */
    private int checkPageId() {
        if (findContent("限时奖励Lv")) {
            return 1;
        }
        if (findContent("任务中心") || findContent("开宝箱得金币") || findContent("明日再来")) {
            return 1;
        }

        if (findId("left_btn") && findContent("直播广场")) {
            return 0;
        }


        if (findContent("s后可领取奖励")) {
            return 2;
        }

        if (findContent("填写邀请码") && findContent("返回")) {
            return 3;
        }
        return -1;
    }


    private boolean autoInvite() {

        if (true) {
            return true;
        }
        getRecognitionResult();
//        ClipboardUtils.copyText("446859698");
        if (null == point_XiangHaoYouXunWenYaoQingMa) {
            SPUtils.getInstance().put(Constant.KUAISHOU_ZHANTIE, "");
        }

        if (null != point_ZhanTie) {
            clickXY(point_ZhanTie.x, point_ZhanTie.y);
            Utils.sleep(1000);

            clickContent("提交领现金");
            Utils.sleep(2000);
            CrashReport.postCatchedException(new Throwable("快手自动填写邀请码成功"));
            return true;
        }

        if (null != point_XiangHaoYouXunWenYaoQingMa) {
            ActionUtils.longPress(point_XiangHaoYouXunWenYaoQingMa.x, point_XiangHaoYouXunWenYaoQingMa.y);
            Utils.sleep(1500);
            EventBus.getDefault().post(new ScreenShootEvet(Constant.PN_KUAI_SHOU, Constant.PAGE_INVITE));
        } else {
            EventBus.getDefault().post(new ScreenShootEvet(Constant.PN_KUAI_SHOU, Constant.PAGE_INVITE));
            return false;
        }
        return false;
    }

    @Override
    protected void getRecognitionResult() {
        String sp_xianghaoyouxunwenyaoqingma = SPUtils.getInstance().getString(Constant.KUAISHOU_XIANGHAOYOUXUNWENYAOQINGMA, "");
        if (!TextUtils.isEmpty(sp_xianghaoyouxunwenyaoqingma)) {
            point_XiangHaoYouXunWenYaoQingMa = new Gson().fromJson(sp_xianghaoyouxunwenyaoqingma, Point.class);
        }

        String sp_zhantie = SPUtils.getInstance().getString(Constant.KUAISHOU_ZHANTIE, "");
        if (!TextUtils.isEmpty(sp_zhantie)) {
            point_ZhanTie = new Gson().fromJson(sp_zhantie, Point.class);
        }
    }

    @Override
    protected void doSamePageDeal() {
        if (samePageCount > 10 && samePageCount < 13) {
            Utils.sleep(1500);
            dealNoResponse2();
        }

        if (samePageCount > 12) {
            Utils.sleep(1000);
            clickBack();
        }
        if (samePageCount > 14) {
            if (BuildConfig.DEBUG) {
                MyApplication.getAppInstance().getAccessbilityService().performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT);
                Utils.sleep(1500);
            }
            doRandomClick();
        }
        if (samePageCount > 30) {
            if (BuildConfig.DEBUG) {
//                MyApplication.getAppInstance().getAccessbilityService().performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT);
//                Utils.sleep(1500);
                Utils.sleep(1000);
                clickBack();
                clickBack();
            }
        }
    }

}
