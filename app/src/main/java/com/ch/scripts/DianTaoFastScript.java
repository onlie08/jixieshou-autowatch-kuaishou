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
import com.ch.common.DeviceUtils;
import com.ch.core.search.node.NodeInfo;
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

    private Point point_DianTao;
    private Point point_LingTiLi;
    private Point point_DaGong;
    private Point point_ZhuanTiLi;
    private Point point_RenWu;
    private Point point_ShuruYaoQingMa;
    private Point point_ZhanTie;
    private Point point_LiJiChouJiang;

    public DianTaoFastScript(AppInfo appInfo) {
        super(appInfo);
        getRecognitionResult();
    }

    /**
     * 检查是在那个页面
     *
     * @return //0:首页 1:个人中心  2:直播
     */
    private int checkPageId() {

//        if (findId("homepage_container") && findId("gold_common_image")) {
        if (findId("tl_homepage2_search_entry_big")) {
            return 0;
        }
        if (findContent("行走赚元宝")) {
            return 4;
        }

        if (findContent("做任务 得抽奖机会")) {
            return 6;
        }

        if (findContent("元宝中心")) {
            return 1;
        }

        if (findId("taolive_room_watermark_text") || findId("gold_countdown_container") || findContent("后完成")) {
            return 2;
        }

        if (findContent("填邀请码 赚元宝")) {
            return 3;
        }

        if (findContent("打工赚元宝")) {
            return 7;
        }

        if (findContent("新人任务 元宝换现金")) {
            return 8;
        }
        if (findContent("滑动浏览")) {
            return 9;
        }
        if (findContent("日 获得大礼包")) {
            return 4;
        }
        return -1;
    }

    @Override
    protected void executeScript() {

        if (doTask()) return;

        pageId = checkPageId();
        if (pageId == lastPageId) {
            samePageCount++;
        } else {
            samePageCount = 0;
        }
        lastPageId = pageId;

        doSamePageDeal();

        LogUtils.d(TAG, "pageId:" + pageId + " samePageCount:" + samePageCount);

        if (pageId == 0) {
            if (point_DianTao == null) {
                getRecognitionResult();
                if (point_DianTao == null) {
                    EventBus.getDefault().post(new ScreenShootEvet(Constant.PN_DIAN_TAO, Constant.PAGE_MAIN));
                }
                return;
            }

            doPageId0Things();

        } else if (pageId == 1) {
            if (point_RenWu == null) {
                NodeInfo nodeInfo = findByText("兑红包");
                if (null != nodeInfo) {
                    point_RenWu = new Point(MyApplication.getScreenWidth() - SizeUtils.dp2px(50), nodeInfo.getRect().centerY());
                    return;
                }
                getRecognitionResult();
                if (point_RenWu == null) {
                    EventBus.getDefault().post(new ScreenShootEvet(Constant.PN_DIAN_TAO, Constant.PAGE_TASK));
                }
                return;
            }

            doPageId1Things();

        } else if (pageId == 2) {

            doPageId2Things();

        } else if (pageId == 3) {

            doPageId3Things();

        } else if (pageId == 4) {
            doPageId4Things();
        } else if (pageId == 5) {
//            doPageId5Things();
        } else if (pageId == 6) {
            doPageId6Things();
        } else if (pageId == 7) {
            doPageId7Things();
        } else if (pageId == 8) {
            doPageId8Things();
        }else if (pageId == 9) {
            doPageId9Things();
        } else {
            if (samePageCount >= 2) {
                clickXY(point_DianTao.x, point_DianTao.y);
            }
            Utils.sleep(1500);
            clickBack();
        }
    }

    private void doPageId9Things() {
        scrollUpSlow();
    }

    /**
     * 新人三天任务
     */
    private void doPageId8Things() {
        if (samePageCount > 3) {
            clickBack();
        }
        if (clickTotalMatchContent("立即提现")) {
            Utils.sleep(2000);
            clickContent("继续下一个任务");
            return;
        }
        if (clickContent("去签到")) {
            Utils.sleep(2000);
            return;
        }

        if (clickContent("去观看")) return;

        scrollUpSlow();

    }

    /**
     * 打工赚元宝
     */
    boolean ifClickNextTask = true;
    private void doPageId7Things() {
        if (samePageCount > 2) {
            if (clickContent("我知道了")) return;
            if (clickContent("去看直播")) return;
            if (clickContent("体力不足，去获得体力")) return;
        }
        if (samePageCount > 4) {
            clickBack();
            Utils.sleep(2000);
            clickContent("走路赚元宝");
            return;

        }

        if (null == point_LingTiLi) {
            NodeInfo nodeInfo1 = findByText("体力+");
            if (null != nodeInfo1) {
                point_LingTiLi = new Point(nodeInfo1.getRect().centerX(), nodeInfo1.getRect().centerY() + SizeUtils.dp2px(20));
                SPUtils.getInstance().put(Constant.DIANTAO_LINGTILI, new Gson().toJson(point_LingTiLi));

                point_ZhuanTiLi = new Point(point_LingTiLi.x + SizeUtils.dp2px(100), point_LingTiLi.y);
                SPUtils.getInstance().put(Constant.DIANTAO_ZHUANTILI, new Gson().toJson(point_ZhuanTiLi));

                point_DaGong = new Point(MyApplication.getScreenWidth() - SizeUtils.dp2px(80), point_LingTiLi.y);
                SPUtils.getInstance().put(Constant.DIANTAO_DAGONG, new Gson().toJson(point_DaGong));
            } else {
                clickBack();
                Utils.sleep(2000);
                clickContent("走路赚元宝");
                return;
            }
        }

        if(findTotalMatchContent("打工时长: 10分钟")){
            if (clickTotalMatchContent("开始打工")) {
                Utils.sleep(2000);
                return;
            }else if(clickTotalMatchContent("体力不足，去获得体力")){
                Utils.sleep(1000);
                ifClickNextTask = true;
                return;
            }

        }else if(findTotalMatchContent("去芭芭农场施肥")){
            if(ifClickNextTask){
                ifClickNextTask = false;
                if (clickTotalMatchContent("去观看")) {
                    return;
                }
                if (clickTotalMatchContent("看直播")) {
                    return;
                }
                if (clickTotalMatchContent("去浏览")) {
                    doScan(35);
                    clickBack();
                    return;
                }
                clickBack();
                Utils.sleep(2000);
                clickTotalMatchContent("走路赚元宝");
            }else {
                ifClickNextTask = true;
                clickXY(MyApplication.getScreenWidth()/2, SizeUtils.dp2px(200));
                Utils.sleep(1500);
            }
            return;
        }else {
            if(findTotalMatchContent("浏览商品30秒")){
                doScan(100);
                clickBack();
                return;
            }

            if (clickId("sign-panel-btn")) Utils.sleep(1000);

            if(clickXY(point_LingTiLi.x, point_LingTiLi.y)){
                Utils.sleep(1500);
                if (clickTotalMatchContent("查看更多任务")) {
                    Utils.sleep(1500);
                    clickXY(MyApplication.getScreenWidth()/2, SizeUtils.dp2px(200));
                    Utils.sleep(1500);
                }
            }
            if(clickXY(point_DaGong.x, point_DaGong.y)){
                Utils.sleep(1500);
                if(findTotalMatchContent("打工时长: 10分钟")){
                    return;
                }
            }
            if(clickXY(point_ZhuanTiLi.x, point_ZhuanTiLi.y)){
                Utils.sleep(1500);
                return;
            }
        }

    }

    /**
     * 元宝中心
     */
    private void doPageId1Things() {
        if (samePageCount >= 2) {
            closeDialog();
        }

        if (clickContent("去签到")) {
            return;
        }

        if (!findContent("00:")) {
            clickXY(point_RenWu.x, point_RenWu.y);
            Utils.sleep(1500);
            closeDialog();
            return;
        }

        if (!SPUtils.getInstance().getBoolean(DeviceUtils.getToday() + "dt", false)) {
            if (clickContent("今日签到")) {
                Utils.sleep(1500);
                clickBack();
                SPUtils.getInstance().put(DeviceUtils.getToday() + "dt", true);
                return;
            }
        }


        if (findContent("后当日福利过期")) {
            if (clickContent("新人填写邀请码")) {
                editPage = false;
                return;
            }
            if (clickContent("后当日福利过期")) return;

        }

        if (clickContent("打工赚元宝")) return;

        if (clickContent("走路赚元宝")) {
            return;
        }

        scrollDown();
    }


    int timeCount = 0;
    boolean findTaskCount = false;

    /**
     *浏览任务
     */
    private void doPageId2Things() {
        if (samePageCount > 6) {
            if (clickContent("重新加载")) return;
        }
        if (findContent("后完成")) {
            findTaskCount = true;
            scrollUp();
            Utils.sleep(1500);
        } else {
            if (findTaskCount) {
                findTaskCount = false;
                clickBack();
            }
        }
        if (findContent("6/6")) {
            timeCount++;
            if (timeCount > 4) {
                if (clickId("gold_turns_container")) return;
            }
        } else {
            timeCount = 0;
        }
    }


    boolean editPage = false;

    /**
     * 填写验证码
     */
    private void doPageId3Things() {
        if (findContent("抱歉 你已经抽过奖了")) {
            SPUtils.getInstance().put("invite_diantao", true);
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

        try {
            if (findContent("提交 去抽奖")) {
                AccessibilityNodeInfo root = MyApplication.getAppInstance().getAccessbilityService().getRootInActiveWindow();
                if (root == null) return;
                AccessibilityNodeInfo root1 = root.findFocus(AccessibilityNodeInfo.FOCUS_INPUT);
                AccessibilityNodeInfo accessibilityNodeInfo = root1.getChild(0).getChild(0).getChild(0).getChild(3).getChild(3).getChild(0).getChild(1).getChild(0);

                if (accessibilityNodeInfo != null) {
                    Bundle arguments = new Bundle();
                    arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, MyApplication.recommendBean.getCode_diantao());
                    accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
                    Utils.sleep(2000);

                    if (clickContent("提交 去抽奖")) {
                        Utils.sleep(2000);
                        SPUtils.getInstance().put("invite_diantao", true);
                        CrashReport.postCatchedException(new Exception("点淘自动填写邀请码成功"));
                        return;
                    }
                }

                return;
            }
        } catch (Exception e) {

        }


    }


    /**
     * 去走路逻辑
     */
    private void doPageId4Things() {
        if (samePageCount > 3) {
            if (clickContent("我知道了")) return;
        }

        if(findTotalMatchContent("做任务赚步数")){
            if (clickTotalMatchContent("去观看")) return;
            if (clickTotalMatchContent("去浏览")) {
                Utils.sleep(1500);
                doScan(70);
                clickBack();
                return;
            }
            clickBack();
        }else {
            if(findTotalMatchContent("浏览商品30秒")){
                doScan(100);
                clickBack();
                return;
            }

            if(findTotalMatchContent("明日再来")){
                if(clickTotalMatchContent("188元宝100步")){
                    Utils.sleep(1000);
                    if(doTask()){
                        return;
                    }
                }
                if(clickTotalMatchContent("288元宝500步")){
                    Utils.sleep(1000);
                    if(doTask()){
                        return;
                    }
                }
                if(clickTotalMatchContent("188元宝2500步")){
                    Utils.sleep(1000);
                    if(doTask()){
                        return;
                    }
                }
                if(clickTotalMatchContent("288元宝5000步")){
                    Utils.sleep(1000);
                    if(doTask()){
                        return;
                    }
                }
                if(clickTotalMatchContent("388元宝10000步")){
                    Utils.sleep(1000);
                    if(doTask()){
                        return;
                    }
                }
                if(clickTotalMatchContent("188元宝12500步")){
                    Utils.sleep(1000);
                    if(doTask()){
                        return;
                    }
                }
                if(clickTotalMatchContent("388元宝15000步")){
                    Utils.sleep(1000);
                    if(doTask()){
                        return;
                    }
                }
                if(clickTotalMatchContent("488元宝17500步")){
                    Utils.sleep(1000);
                    if(doTask()){
                        return;
                    }
                }
                if(clickTotalMatchContent("588元宝20000步")){
                    Utils.sleep(1000);
                    if(doTask()){
                        return;
                    }
                }

            }
            if(clickTotalMatchContent("领取")){
                Utils.sleep(2000);
            }
            if(clickTotalMatchContent("出发")){
                Utils.sleep(2000);
                if (findTotalMatchContent("邀请好友助力赚步数吧") || findTotalMatchContent("去领步数")) {
                    clickTotalMatchContent("查看更多任务");
                    return;
                }
            }
        }


    }


    private void doPageId6Things() {
        scrollDown();

        if (point_LiJiChouJiang == null) {
            getRecognitionResult();
            if (point_LiJiChouJiang == null) {
                EventBus.getDefault().post(new ScreenShootEvet(Constant.PN_DIAN_TAO, Constant.PAGE_ACTIVE));
            }
            return;
        }

        if (clickContent("继续抽奖")) return;

        if (!findContent("今日还剩0次抽奖机会")) {
            clickXY(point_LiJiChouJiang.x, point_LiJiChouJiang.y);
            return;
        }

        if (clickContent("去观看")) return;
    }

    private boolean doTask() {
        if (clickContent("继续做任务")) {
            samePageCount = 0;
            return true;
        }

        if (clickContent("去看直播赚")) return true;
        if (clickContent("秒再得")) return true;
        if (clickContent("秒直播再得")) return true;

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

        clickContent("直播");
    }

    private void closeDialog() {
        if (clickContent("邀请好友 再赚")) {
            Utils.sleep(1000);
            clickBack();
            return;
        }
        if (clickContent("走路赚元宝 每日")) return;
        if (clickContent("立即签到")) return;
        if (clickContent("残忍退出")) return;
    }


    @Override
    protected int getMinSleepTime() {
        if (pageId == 1) {
            return 1500;
        }
        if (pageId == 4) {
            return 1500;
        }
        if (pageId == 6) {
            return 1500;
        }
        if (pageId == 7) {
            return 1500;
        }
        if (pageId == -1) {
            return 1000;
        }
        if (pageId == 2) {
            return 5000;
        }
        return 2000;
    }

    @Override
    protected int getMaxSleepTime() {
        if (pageId == 1) {
            return 1500;
        }
        if (pageId == 4) {
            return 1500;
        }
        if (pageId == 6) {
            return 1500;
        }
        if (pageId == 7) {
            return 1500;
        }
        if (pageId == -1) {
            return 1000;
        }
        if (pageId == 2) {
            return 5000;
        }
        return 2000;
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
            if (resumeCount > 5) {
                LogUtils.d(TAG, "自动恢复到点淘");
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
                CrashReport.postCatchedException(new Throwable("点淘无响应"));

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
    public boolean dealNoResponse2() {
        LogUtils.d(TAG, "dealNoResponse2()");
        if (clickId("gold_common_image")) return true;
        if (clickContent("知道")) return true;
        if (clickContent("继续赚金币")) return true;
        if (clickContent("去赚钱")) return true;
        if (clickTotalMatchContent("禁止")) return true;
        if (clickTotalMatchContent("以后再说")) return true;
        if (clickContent("不允许")) return true;
        if (clickTotalMatchContent("允许")) return true;
        if (clickContent("立即添加")) return true;
        if (clickContent("关闭")) return true;
        if (clickContent("重试")) return true;
        if (clickContent("取消")) return true;
        return false;
    }

    @Override
    protected void getRecognitionResult() {
        String sp_diantao = SPUtils.getInstance().getString(Constant.DIANTAO_DIANTAO, "");
        if (!TextUtils.isEmpty(sp_diantao)) {
            point_DianTao = new Gson().fromJson(sp_diantao, Point.class);
        }

        String sp_renwu = SPUtils.getInstance().getString(Constant.DIANTAO_RENWU, "");
        if (!TextUtils.isEmpty(sp_renwu)) {
            point_RenWu = new Gson().fromJson(sp_renwu, Point.class);
        }

        String sp_shuruyaoqingma = SPUtils.getInstance().getString(Constant.DIANTAO_SHURUYAOQINGMA, "");
        if (!TextUtils.isEmpty(sp_shuruyaoqingma)) {
            point_ShuruYaoQingMa = new Gson().fromJson(sp_shuruyaoqingma, Point.class);
        }

        String sp_zhantie = SPUtils.getInstance().getString(Constant.DIANTAO_ZHANTIE, "");
        if (!TextUtils.isEmpty(sp_zhantie)) {
            point_ZhanTie = new Gson().fromJson(sp_zhantie, Point.class);
        }

        String sp_lijichoujiang = SPUtils.getInstance().getString(Constant.DIANTAO_LIJICHOUJIANG, "");
        if (!TextUtils.isEmpty(sp_lijichoujiang)) {
            point_LiJiChouJiang = new Gson().fromJson(sp_lijichoujiang, Point.class);
        }

        String sp_lingtili = SPUtils.getInstance().getString(Constant.DIANTAO_LINGTILI, "");
        if (!TextUtils.isEmpty(sp_lingtili)) {
            point_LingTiLi = new Gson().fromJson(sp_lingtili, Point.class);
        }

        String sp_zhuantili = SPUtils.getInstance().getString(Constant.DIANTAO_ZHUANTILI, "");
        if (!TextUtils.isEmpty(sp_zhuantili)) {
            point_ZhuanTiLi = new Gson().fromJson(sp_zhuantili, Point.class);
        }

        String sp_dagong = SPUtils.getInstance().getString(Constant.DIANTAO_DAGONG, "");
        if (!TextUtils.isEmpty(sp_dagong)) {
            point_DaGong = new Gson().fromJson(sp_dagong, Point.class);
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
            tryClickDialog();
        }
    }

    private void doScan(int second) {
        for (int i = 0; i < second; i++) {
            if (isCurrentScipte()) {
                Utils.sleep(1000);
                scrollUp();
            }
        }
    }
}
