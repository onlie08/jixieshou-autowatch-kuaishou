package com.ch.scripts;

import android.graphics.Point;
import android.graphics.Rect;
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
import com.ch.jixieshou.BuildConfig;
import com.ch.model.AppInfo;
import com.ch.model.ScreenShootEvet;
import com.google.gson.Gson;
import com.tencent.bugly.crashreport.CrashReport;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import static android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_TAKE_SCREENSHOT;
import static com.ch.core.utils.ActionUtils.pressHome;

public class TouTiaoAdvertScript extends BaseScript {

    private String TAG = this.getClass().getSimpleName();
    private Point point_ShouYe;
    private Point point_RenWu;

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

        if (pageId == 0) {
//            if (clickAdvert()) return;
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
                return;
            }

            doPageId0Things();

        } else if (pageId == 1) {

            doPageId1Things();

        } else if (pageId == 2) {
//            if (clickAdvert()) return;
            doPageId2Things();

        } else if (pageId == 3) {
            if (clickAdvert()) return;
            baoXiangClickCount = 0;
            doPageId3Things();

        }else if (pageId == 4) {
            doPageId4Things();

        } else {
            if (clickAdvert()) Utils.sleep(2500);
            if (samePageCount > 5) {
                if (clickContent("坚持退出")) return;
            }
            if (clickContent("看视频得金币")) Utils.sleep(1500);
            if (clickContent("视频再领")) return;

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
            tryClickDialog();
        }

        if (samePageCount > 35) {
            clickXY(point_ShouYe.x, point_ShouYe.y);
        }
    }

    private int gotoPersonCount = 0;

    private void doPageId0Things() {
//        if(!findTotalMatchContent("历史")){
//            if(clickTotalMatchContent("频道管理")){
//                Utils.sleep(1500);
//                clickTotalMatchContent("热点");
//                Utils.sleep(1500);
//            }
//        }
        gotoPersonCount++;
        if (gotoPersonCount > 5) {
            gotoPersonCount = 0;
            clickXY(point_RenWu.x, point_RenWu.y);
            return;
        }
        LogUtils.d(TAG, "doPageId0Things");

        if (clickContent("领金币")) {
            Utils.sleep(2000);
            clickAdvert();
            return;
        }

        scrollUp();

        Utils.sleep(3000);
        List<AccessibilityNodeInfo> accessibilityNodeInfos = findAccessibilityNodeListById("com.ss.android.article.lite:id/ajb");
        if (null == accessibilityNodeInfos) {
            accessibilityNodeInfos = findAccessibilityNodeListById("com.ss.android.article.lite:id/afk");
        }
        if (null == accessibilityNodeInfos) {
            accessibilityNodeInfos = findAccessibilityNodeListById("com.ss.android.article.lite:id/agp");
        }

        if (null != accessibilityNodeInfos) {
            for (int i = 0; i < accessibilityNodeInfos.size(); i++) {
                LogUtils.d(TAG, "nodeInfo.getChildCount():" + accessibilityNodeInfos.get(i).getChildCount() + " postion:" + i);
                if (accessibilityNodeInfos.get(i).getChildCount() > 2) {
                    continue;
                } else {
                    AccessibilityNodeInfo accessibilityNodeInfo = accessibilityNodeInfos.get(i);
                    Rect nodeRect = new Rect();
                    accessibilityNodeInfo.getBoundsInScreen(nodeRect);
                    clickXY(nodeRect.centerX(), nodeRect.centerY());

//                    accessibilityNodeInfos.get(i).performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                    Utils.sleep(2000);
                    return;
                }
            }
        }else {
            clickContent("0评论");
        }

        if (clickContent("继续阅读")) return;

    }

    int baoXiangClickCount = 0;

    private void doPageId1Things() {
        LogUtils.d(TAG, "doPageId1Things");
        if(samePageCount > 1){
            //签到弹出窗口
           tryClickDialog();
//
        }
        if (samePageCount > 2) {
            scrollDown();
            Utils.sleep(2000);
            clickXY(point_ShouYe.x, point_ShouYe.y);
        }


        //阅读翻倍
//        if (clickLastTotalMatchByText("点击翻倍")) {
//            Utils.sleep(1500);
//            if (clickContent("我知道了")) Utils.sleep(1500);
//        }

        //看广告赚金币
//        if (clickContent("看广告赚金币")) Utils.sleep(2000);

//        if (clickContent("填写邀请码")) Utils.sleep(2000);

        if(clickEveryTotalMatchByText("直接领取")) Utils.sleep(1500);

        if(clickEveryTotalMatchByText("开宝箱得金币")){
            Utils.sleep(2000);
            clickAdvert();
            return;
        }

        //限时专享
        if(clickTotalMatchContent("领取")){
            Utils.sleep(1500);
            clickAdvert();
        }
        if(clickEveryTotalMatchByText("立即领取")){
            Utils.sleep(1500);
        }
        if(clickEveryTotalMatchByText("领福利")){
            Utils.sleep(1500);
            clickAdvert();
        }

        clickXY(point_ShouYe.x, point_ShouYe.y);
//        scrollUpSlow();
    }

    /**
     * 文章阅读找金币
     */
    private void doPageId2Things() {
        LogUtils.d(TAG, "doPageId2Things");
        //todo 有的手机找不到mc-footer无法领取金币
        NodeInfo nodeInfo = findById("mc-footer");
        if (null != nodeInfo) {
            LogUtils.d(TAG, "nodeInfo.getChildCount():" + nodeInfo.getChildCount());
            if (nodeInfo.getChildCount() > 3) {
                clickXY(SizeUtils.dp2px(80), nodeInfo.getRect().top + SizeUtils.dp2px(100));
                return;
            } else {
                clickBack();
                return;
            }
        }

        if (findContent("已显示全部评论") || findContent("暂无评论，点击抢沙发")) {
            clickBack();
        }

        scrollUp();
        Utils.sleep(1500);

    }

    private void doPageId3Things() {
        LogUtils.d(TAG, "doPageId3Things");
        if (clickContent("继续观看")) return;
    }

    /**
     * 弹出框里点击看广告
     */
    private boolean clickAdvert() {
        if (clickEveryNodeInfosByText("视频再领")) return true;
        if (clickEveryNodeInfosByText("再看一个获得")) return true;
//        if (clickEveryNodeInfosByText("继续观看")) return true;
        if (clickEveryNodeInfosByText("看视频领")) return true;
        return false;
    }

    /**
     * 检查是在那个页面
     *
     * @return //0:首页 1:个人中心  2:阅读页  3:广告页
     */
    private int checkPageId() {
        if(findContent("每日凌晨，金币自动兑换成现金")){
            return 1;
        }
        if(findContent("我的现金：")){
            return 1;
        }
        if (findContent("频道管理") && findContent("发布")) {
            return 0;
        }
        if ((findContent("每日挑战") ||findContent("新人福利") ||findContent("日常任务") || findContent("看广告赚金币")) && findContent("金币")) {
            return 1;
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
        if (findContent("邀请码") && findContent("马上提交")) {
            return 4;
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
        } else if (pageId == 0) {
            return 100;
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
        } else if (pageId == 0) {
            return 100;
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

//        String sp_kaibaoxiangdejinbi = SPUtils.getInstance().getString(Constant.TOUTIAO_KAIBAOXIANGDEJINBI, "");
//        if (!TextUtils.isEmpty(sp_kaibaoxiangdejinbi)) {
//            point_KaiBaoXiangDeJinBi = new Gson().fromJson(sp_kaibaoxiangdejinbi, Point.class);
//        }
//
//        String sp_shuruhaoyouyaoqingma = SPUtils.getInstance().getString(Constant.TOUTIAO_SHURUHAOYOUYAOQINGMA, "");
//        if (!TextUtils.isEmpty(sp_shuruhaoyouyaoqingma)) {
//            point_ShuRuHaoYouYaoQingMa = new Gson().fromJson(sp_shuruhaoyouyaoqingma, Point.class);
//        }
//
//        String sp_zhantie = SPUtils.getInstance().getString(Constant.TOUTIAO_ZHANTIE, "");
//        if (!TextUtils.isEmpty(sp_zhantie)) {
//            point_ZhanTie = new Gson().fromJson(sp_zhantie, Point.class);
//        }
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
            if (resumeCount > 5) {
                LogUtils.d(TAG, "自动恢复到头条极速版");
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
                CrashReport.postCatchedException(new Throwable("头条极速版无响应"));

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

//    private boolean autoInvite() {
//        if (true) {
//            return true;
//        }
//        getRecognitionResult();
//        if (null == point_ShuRuHaoYouYaoQingMa) {
//            SPUtils.getInstance().put(Constant.TOUTIAO_ZHANTIE, "");
//        }
//
//        if (null != point_ZhanTie) {
//            clickXY(point_ZhanTie.x, point_ZhanTie.y);
//            Utils.sleep(1000);
//
//            clickContent("马上提交");
//            Utils.sleep(2000);
//            CrashReport.postCatchedException(new Throwable("头条自动填写邀请码成功"));
//            return true;
//        }
//
//        if (null != point_ShuRuHaoYouYaoQingMa) {
//            ActionUtils.longPress(point_ShuRuHaoYouYaoQingMa.x, point_ShuRuHaoYouYaoQingMa.y);
//            Utils.sleep(1500);
//            EventBus.getDefault().post(new ScreenShootEvet(Constant.PN_TOU_TIAO, Constant.PAGE_INVITE));
//        } else {
//            EventBus.getDefault().post(new ScreenShootEvet(Constant.PN_TOU_TIAO, Constant.PAGE_INVITE));
//            return false;
//        }
//        return false;
//    }


    private void doPageId4Things() {
        AccessibilityNodeInfo textInfo = findEditText();
        if (textInfo != null) {
            Bundle arguments = new Bundle();
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, MyApplication.recommendBean.getCode_toutiao());
            textInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
            Utils.sleep(2000);
//            clickBack();
//            Utils.sleep(2000);
            if (clickContent("马上提交")) {
                Utils.sleep(2000);
                SPUtils.getInstance().put("invite_toutiao", true);
                CrashReport.postCatchedException(new Exception("头条邀请码自动填写成功"));
                return;
            }
        }
    }

    public AccessibilityNodeInfo findEditText() {
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


}

