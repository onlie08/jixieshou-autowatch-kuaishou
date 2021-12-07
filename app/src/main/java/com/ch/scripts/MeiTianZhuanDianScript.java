package com.ch.scripts;

import android.drm.DrmStore;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Size;
import android.view.accessibility.AccessibilityNodeInfo;

import com.blankj.utilcode.util.ClipboardUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.ch.application.MyApplication;
import com.ch.common.PackageUtils;
import com.ch.common.RecommendCodeManage;
import com.ch.core.search.node.NodeInfo;
import com.ch.core.utils.ActionUtils;
import com.ch.core.utils.Constant;
import com.ch.core.utils.Utils;
import com.ch.jixieshou.BuildConfig;
import com.ch.model.AppInfo;
import com.ch.model.ScreenShootEvet;
import com.ch.model.SearchAuthorBean;
import com.google.gson.Gson;
import com.tencent.bugly.crashreport.CrashReport;


import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import static android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_TAKE_SCREENSHOT;
import static android.content.Context.RECEIVER_VISIBLE_TO_INSTANT_APPS;
import static android.content.Context.SEARCH_SERVICE;
import static com.ch.core.utils.ActionUtils.click;
import static com.ch.core.utils.ActionUtils.longPress;
import static com.ch.core.utils.ActionUtils.pressHome;

public class MeiTianZhuanDianScript extends BaseScript {

    private String TAG = this.getClass().getSimpleName();
    private Point point_ShouYe;
    private Point point_WoDe;

    private volatile static MeiTianZhuanDianScript instance; //声明成 volatile

    public static MeiTianZhuanDianScript getSingleton(AppInfo appInfo) {
        if (instance == null) {
            synchronized (MeiTianZhuanDianScript.class) {
                if (instance == null) {
                    instance = new MeiTianZhuanDianScript(appInfo);
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
            if (!MyApplication.getAppInstance().getAccessbilityService().containsPkg(Constant.PN_MEI_TIAN_ZHUAN_DIAN)) {
                return false;
            }
        }
        return true;
    }

    public MeiTianZhuanDianScript(AppInfo appInfo) {
        super(appInfo);
        addData();
    }

    @Override
    protected void executeScript() {
        LogUtils.d(TAG,"executeScript()");
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

        if(samePageCount >10){
            doSamePageDeal();
            return;
        }

        if (pageId == 0) {
            if (point_ShouYe == null) {
                getRecognitionResult();
                if (point_ShouYe == null) {
                    EventBus.getDefault().post(new ScreenShootEvet(Constant.PN_MEI_TIAN_ZHUAN_DIAN, Constant.PAGE_MAIN));
                }
                return;
            }
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
        else if (pageId == 4) {

            doPageId4Things();

        }
        else {
            if(samePageCount > 8){
                scrollDown();
            }
            Utils.sleep(1500);
            clickBack();
        }

    }

    private void doPageId4Things() {
        if(clickContent("输入好友邀请码即可成为Ta的好友")){
           Utils.sleep(2000);
            AccessibilityNodeInfo accessibilityNodeInfo = findAccessibilityNodeById("com.yongloveru.hjw:id/findt_edit");
            if(null != accessibilityNodeInfo){
                Bundle arguments = new Bundle();
                arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, RecommendCodeManage.getSingleton().getRecommendBean().getCode_meitianzhuandian());
                accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
                accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
                Utils.sleep(1000);
                if(clickContent("确定")){
                    SPUtils.getInstance().put("invite_success",true);
                    Utils.sleep(2000);
                    return;
                }

            }
        }

    }

    private void doPageId0Things() {
        LogUtils.d(TAG, "doPageId0Things");
        boolean invite = SPUtils.getInstance().getBoolean("invite_success");
        if(!invite){
            clickXY(point_WoDe.x,point_WoDe.y);
            Utils.sleep(1000);
            scrollUp();
            Utils.sleep(2000);
            if(clickContent("填写邀请码")){
                Utils.sleep(2000);
                if(findContent("我的师傅")){
                    SPUtils.getInstance().put("invite_success",true);
                    Utils.sleep(2000);
                    clickBack();
                    Utils.sleep(2000);
                    clickXY(point_ShouYe.x,point_ShouYe.y);
                    return;
                }
                return;
            }
        }

        if(clickContent("截图任务"))return;

    }

    int curTaskType = -1;
    private void doPageId1Things() {
        LogUtils.d(TAG, "doPageId1Things");
        if(samePageCount > 4){
            clickBack();
            return;
        }

        if(PackageUtils.checkApkExist(MyApplication.getAppInstance(),Constant.PN_XIAO_HONG_SHU)){
            if(clickContent("小红书关注") || clickContent("小红书简单关注")|| clickContent("小红书点关注")|| clickContent("小红书粉丝")){
                curTaskType = 0;
                return;
            }
            if(clickContent("简单关注拒绝秒点")){
                curTaskType = 1;
                return;
            }
//            if(clickContent("小红书点赞")){
//                curTaskType = 5;
//                return;
//            }

        }
        if(PackageUtils.checkApkExist(MyApplication.getAppInstance(),Constant.PN_DOU_YIN)){
            if(clickContent("抖音点赞") ){
                curTaskType = 6;
                return;
            }
        }
        if(PackageUtils.checkApkExist(MyApplication.getAppInstance(),Constant.PN_TAO_TE)){
            if(clickTotalMatchContent("+0.68元") ){
                curTaskType = 7;
                return;
            }
        }
        if(PackageUtils.checkApkExist(MyApplication.getAppInstance(),Constant.PN_BI_LI_BI_LI)){
            if(clickContent("哔哩哔哩关注") ){
                curTaskType = 8;
                return;
            }
        }

//        if(clickContent("淘宝产品浏览") || clickContent("淘宝浏览产品")){
//            curTaskType = 2;
//            return;
//        }

//        if(clickContent("淘宝喵糖总动员邀")){
//            curTaskType = 3;
//            return;
//        }
//        if(clickContent("淘宝农场助力")){
//            curTaskType = 4;
//            return;
//        }

//        if(clickContent("小红书点赞")){
//            curTaskType = 5;
//            return;
//        }
//        if(clickContent("简单搜索浏览") || clickContent("简单浏览任务")){
//            curTaskType = 99;
//            return;
//        }

//        if(clickContent("简单浏览，")){
//            curTaskType = 98;
//            return;
//        }
        scrollUpSlow();
        return;

    }

    boolean screemSuccess =false;
    private void doPageId2Things() {
        LogUtils.d(TAG, "doPageId2Things");
        if(findContent("任务太火爆，已经结束")){
            clickBack();
            Utils.sleep(1000);
            clickBack();
            return;
        }
        if(findContent("开启悬浮助手")){
            if(clickContent("不再提醒")){
                return;
            }
        }

//        if(samePageCount > 3){
//            cancelTask();
//            return;
//        }

        if(screemSuccess){
            screemSuccess = false;
            if(findContent("微信扫一扫")){
                clickXY(500,500);
                Utils.sleep(2000);
            }
            if(uploadTask())return;

            return;
        }

        if(recieveTask())return;

        if(curTaskType == 0){
            doTask0();
            return;
        }

        if(curTaskType == 1){
            doTask1();
            return;
        }

        if(curTaskType == 2){
            doTask2();
            return;
        }

        if(curTaskType == 3){
            doTask3();
            return;
        }

        if(curTaskType == 4){
            doTask4();
            return;
        }

        if(curTaskType == 5){
            doTask5();
            return;
        }

        if(curTaskType == 6){
            doTask6();
            return;
        }

        if(curTaskType == 7){
            doTask7();
            return;
        }
        if(curTaskType == 8){
            doTask8();
            return;
        }

        if(curTaskType == 97){
            doZhiHuTask(curSearchAuthorBean);
            return;
        }

        if(curTaskType == 99){
            boolean result = getKeyAndAuthor();
            if(result){
                doZhiHuTask(curSearchAuthorBean);
            }else {
                cancelTask();
                return;
            }
            return;
        }

//        if(curTaskType == 98){
//            boolean result = getKeyAndAuthor98();
//            if(result){
//                doTask99();
//            }else {
//                cancelTask();
//                return;
//            }
//            return;
//        }



        if(clickContent("关闭"))return;

    }


    private void cancelTask(){
        if(clickTotalMatchContent("取消")){
            Utils.sleep(2000);
            if(clickContent("其他原因。")){
                Utils.sleep(2000);
                if(clickContent("狠心取消")){
                    Utils.sleep(2000);
                    clickBack();
                }
            }
        }
    }

    /**
     * 淘宝农场助力
     */
    private void doTask4() {
        if(clickTotalMatchContent("复制口令")){
            Utils.sleep(1000);
            openTaoBao();
            Utils.sleep(5000);
            if(clickContent("查看详情")){
                Utils.sleep(2000);
                if(clickContent("为他助力")){
                    Utils.sleep(2000);
                    MyApplication.getAppInstance().getAccessbilityService().performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT);
                    screemSuccess = true;
                    Utils.sleep(3000);
                    clickBack();
                    Utils.sleep(1000);
                    clickBack();
                    startApp();
                }
            }
        }
    }

    /**
     * 淘宝喵糖助力
     */
    private void doTask3() {
        if(clickTotalMatchContent("复制口令")){
            Utils.sleep(1000);
            openTaoBao();
            Utils.sleep(5000);
            if(clickContent("查看详情")){
                Utils.sleep(2000);
                if(clickContent("立即助力")){
                    Utils.sleep(2000);
                    MyApplication.getAppInstance().getAccessbilityService().performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT);
                    screemSuccess = true;
                    Utils.sleep(3000);
                    clickBack();
                    Utils.sleep(1000);
                    clickBack();
                    startApp();
                }
            }
        }
    }

    /**
     * 跳转链接小红书关注
     */
    private void doTask0() {
        if(clickTotalMatchContent("打开链接")){
            Utils.sleep(5000);
            requestOpenApp();
            if(findContent("打开方式")){
                if(clickTotalMatchContent("小红书")){
                    Utils.sleep(5000);
                }

            }
            openXiaoHongShu();
            return;
        }else if(findTotalMatchContent("点击保存二维码")){
            doTask1();
        }else {
            cancelTask();
        }
    }

    /**
     * 扫码跳转小红书关注
     */
    private void doTask1() {
        NodeInfo nodeInfo = findByText("点击保存二维码");
        longPressXY(MyApplication.getScreenWidth()/2,nodeInfo.getRect().centerY()-SizeUtils.dp2px(80));
        Utils.sleep(3000);
        if(clickTotalMatchContent("识别二维码")){
            Utils.sleep(5000);
            if(findTotalMatchContent("识别二维码")){
                clickBack();
                Utils.sleep(2000);
                cancelTask();
                return;
            }
            openXiaoHongShu();
        }
    }

    /**
     * 淘宝浏览产品任务
     */
    private void doTask2() {

        if(clickTotalMatchContent("复制口令")){
            Utils.sleep(2000);
            openTaoBao();
            Utils.sleep(5000);
            if(clickContent("查看详情")){
                Utils.sleep(5000);
                MyApplication.getAppInstance().getAccessbilityService().performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT);
                screemSuccess = true;
                Utils.sleep(3000);
                clickBack();
                Utils.sleep(1000);
                clickBack();
                startApp();
            }

        }
    }



    private void openTaoBao() {
        PackageUtils.startApp("com.taobao.taobao");
    }

    private boolean recieveTask(){
        if(clickContent("立即赚钱")){
            Utils.sleep(2000);
            if(clickTotalMatchContent("确认领取")){
                return true;
            }
            return true;
        }
        return false;
    }

    /**
     * 上传截图提交任务
     */
    private boolean uploadTask(){
        if(findContent("提交审核")){
            scrollUp();
            Utils.sleep(2000);
            if (clickContent("选择文件")){
                Utils.sleep(2000);
                AccessibilityNodeInfo accessibilityNodeInfo = findAccessibilityNodeById("com.android.documentsui:id/dir_list");
                if(null == accessibilityNodeInfo){
                    accessibilityNodeInfo = findAccessibilityNodeById("com.google.android.documentsui:id/dir_list");
                }
                if(null != accessibilityNodeInfo){
                    AccessibilityNodeInfo accessibilityNodeInfo3 = accessibilityNodeInfo.getChild(0);
                    if(null != accessibilityNodeInfo3){
                        accessibilityNodeInfo3.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        Utils.sleep(2000);
                        if(clickContent("提交审核")){
                            Utils.sleep(5000);
                            clickBack();
                            return true;
                        }
                    }
                }

            }
        }
        return false;
    }

    /**
     * 小红书关注
     */
    private void openXiaoHongShu(){
        if(findTotalMatchContent("发消息")){
            Utils.sleep(2000);
            MyApplication.getAppInstance().getAccessbilityService().performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT);
            screemSuccess = true;
            Utils.sleep(4000);
            clickBack();
            Utils.sleep(2000);
            clickBack();
            Utils.sleep(2000);
            startApp();
            return;
        }

        NodeInfo nodeInfo = findByText("获赞与收藏");
        if(null != nodeInfo){
            clickXY(MyApplication.getScreenWidth() - SizeUtils.dp2px(100),nodeInfo.getRect().centerY()-SizeUtils.dp2px(15));
//        if(clickId("f_s")){
            Utils.sleep(2000);
            MyApplication.getAppInstance().getAccessbilityService().performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT);
            screemSuccess = true;
            Utils.sleep(4000);
            clickBack();
            Utils.sleep(2000);
            clickBack();
            Utils.sleep(2000);
            startApp();
            return;
        }
    }

    /**
     * 检查是在那个页面
     *
     * @return //0:首页 1:个人中心  2:阅读页  3:广告页
     */
    private int checkPageId() {
        if (findContent("输入好友邀请码")) {
            return 4;
        }
        if(findContent("截图任务") && findContent("游戏任务")){
            return 0;
        }
        if (findContent("我的任务")&& findContent("推荐")) {
            return 1;
        }
        if (findTotalMatchContent("做任务赚钱") ) {
//        if (findTotalMatchContent("做任务赚钱") || (findContent("立即赚钱") || findContent("提交审核"))) {
            return 2;
        }
        if (findContent("商户详情") || findContent("剩余数量：")) {
            return 3;
        }


        return -1;
    }


    @Override
    protected int getMinSleepTime() {
        if (pageId == 2) {
            return 1000;
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
            return 1000;
        } else if (pageId == 1) {
            return 2000;
        } else if (pageId == 3) {
            return 3000;
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
        String sp_shouye = SPUtils.getInstance().getString(Constant.MEITIANZHUANDIAN_SHOUYE,"");
        if(!TextUtils.isEmpty(sp_shouye)){
            point_ShouYe = new Gson().fromJson(sp_shouye,Point.class);
        }
        String sp_wode = SPUtils.getInstance().getString(Constant.MEITIANZHUANDIAN_WODE,"");
        if(!TextUtils.isEmpty(sp_wode)){
            point_WoDe = new Gson().fromJson(sp_wode,Point.class);
        }

    }


    public boolean isCurrentScipte() {
        return getAppInfo().getPkgName().equals(Constant.PN_MEI_TIAN_ZHUAN_DIAN) ? true : false;
    }

    int resumeCount = 0;

    @Override
    public boolean isDestinationPage() {
        // 检查当前包名是否有本年应用
//        LogUtils.d(TAG,"isDestinationPage()");
        if (!isTargetPkg() && isCurrentScipte()) {
            if (!NetworkUtils.isAvailable()) {
                return false;
            }
            if (ScreenUtils.isScreenLock()) {
                return false;
            }

            resumeCount++;
            if (resumeCount > 36) {
                LogUtils.d(TAG, "自动恢复到每天赚点");
                CrashReport.postCatchedException(new Throwable("自动恢复到每天赚点"));
                startApp();
            }
            if (resumeCount > 40) {
                if (BuildConfig.DEBUG) {
                    MyApplication.getAppInstance().getAccessbilityService().performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT);
                }
                LogUtils.d(TAG, "每天赚点是不是anr了?");
                dealNoResponse();
                Utils.sleep(1000);
                clickBack();
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
        if (clickTotalMatchContent("以后再说")) return true;
        if (clickContent("去赚钱")) return true;
        if (clickContent("允许")) return true;
        if (clickContent("立即添加")) return true;
        if (clickContent("关闭")) return true;
        if (clickContent("重试")) return true;
        if (clickContent("取消")) return true;
        clickXY(point_ShouYe.x,point_ShouYe.y);
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
//
//        if(null != point_ZhanTie){
//            clickXY(point_ZhanTie.x, point_ZhanTie.y);
//            Utils.sleep(1000);
//            clickXY(point_TianXieYaoQingMa3.x, point_TianXieYaoQingMa3.y);
//            Utils.sleep(2000);
//            CrashReport.postCatchedException(new Throwable("京东自动填写邀请码成功"));
//        }

//        if(null == point_TianXieYaoQingMa1){
//            EventBus.getDefault().post(new ScreenShootEvet(Constant.PN_MEI_TIAN_ZHUAN_DIAN,Constant.PAGE_INVITE));
//            return false;
//        }
//
//        if(null != point_TianXieYaoQingMa1 && point_TianXieYaoQingMa2 == null){
//            clickXY(point_TianXieYaoQingMa1.x, point_TianXieYaoQingMa1.y);
//            Utils.sleep(1000);
//            EventBus.getDefault().post(new ScreenShootEvet(Constant.PN_MEI_TIAN_ZHUAN_DIAN,Constant.PAGE_INVITE));
//        }
//
//        if(null != point_TianXieYaoQingMa2 && point_TianXieYaoQingMa3 != null){
//            ActionUtils.longPress(point_TianXieYaoQingMa2.x, (point_TianXieYaoQingMa3.y + point_TianXieYaoQingMa2.y)/2);
//            Utils.sleep(1500);
//            EventBus.getDefault().post(new ScreenShootEvet(Constant.PN_MEI_TIAN_ZHUAN_DIAN,Constant.PAGE_INVITE));
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

    /**
     * 小红书点赞
     */
    private void doTask5() {
        if(clickTotalMatchContent("打开链接")){
            Utils.sleep(2000);
            xiaoHongShuLike();
            return;
        }

    }


    /**
     * 哔哩哔哩关注
     */
    private void doTask8() {
        if(clickTotalMatchContent("打开链接")){
            Utils.sleep(5000);
            requestOpenApp();

            if(!findTotalMatchContent("已关注")){
                if(clickId("follow")){
                    Utils.sleep(2000);
                    MyApplication.getAppInstance().getAccessbilityService().performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT);
                    Utils.sleep(2000);
                    clickBack();
                    screemSuccess = true;
                    Utils.sleep(2000);
                    startApp();
                    return;
                }
            }else {
                Utils.sleep(2000);
                MyApplication.getAppInstance().getAccessbilityService().performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT);
                Utils.sleep(2000);
                clickBack();
                screemSuccess = true;
                Utils.sleep(2000);
                startApp();
                return;
            }
            return;
        }
    }


    /**
     * 淘特每日拆红包
     */
    private void doTask7() {
        if(clickTotalMatchContent("复制到app打开")){
            Utils.sleep(5000);
            requestOpenApp();

            AccessibilityNodeInfo accessibilityNodeInfo = findAccessibilityNodeById("com.taobao.litetao:id/search_bar_container");
            if(null != accessibilityNodeInfo){
                AccessibilityNodeInfo accessibilityNodeInfo1 = accessibilityNodeInfo.getChild(0);
                AccessibilityNodeInfo accessibilityNodeInfo2 = accessibilityNodeInfo1.getChild(0);
                AccessibilityNodeInfo accessibilityNodeInfo3 = accessibilityNodeInfo2.getChild(2);
                AccessibilityNodeInfo accessibilityNodeInfo4 = accessibilityNodeInfo3.getChild(0);
                if(null != accessibilityNodeInfo4){
                    accessibilityNodeInfo4.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    Utils.sleep(5000);
                }

            }

//            NodeInfo nodeInfo = findByText("猜你要比价");
            NodeInfo nodeInfo = findByText("扫描二维码或者拍照");
            if(null != nodeInfo){
                Point point = new Point(SizeUtils.dp2px(60),nodeInfo.getRect().centerY()+SizeUtils.dp2px(80));
//                Point point = new Point(nodeInfo.getRect().centerX(),nodeInfo.getRect().centerY()+SizeUtils.dp2px(30));
                clickXY(point.x,point.y);
                Utils.sleep(5000);
            }
//            AccessibilityNodeInfo accessibilityNodeInfo7 = findAccessibilityNodeByText("扫描二维码或者拍照");
//            AccessibilityNodeInfo accessibilityNodeInfo8 = accessibilityNodeInfo7.getParent();
//            int count = accessibilityNodeInfo8.getChildCount();
//
//            AccessibilityNodeInfo accessibilityNodeInfo9 = accessibilityNodeInfo8.getChild(6);
//
//
//            if(null != accessibilityNodeInfo9) {
//                accessibilityNodeInfo9.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                Utils.sleep(2000);
//            }
            AccessibilityNodeInfo accessibilityNodeInfo5 = findAccessibilityNodeById("com.taobao.litetao:id/gridview");
            if(null != accessibilityNodeInfo5){
                AccessibilityNodeInfo accessibilityNodeInfo6 = accessibilityNodeInfo5.getChild(0);
                accessibilityNodeInfo6.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                Utils.sleep(2000);

                if(clickContent("帮好友助力")){
                    Utils.sleep(2000);
                    MyApplication.getAppInstance().getAccessbilityService().performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT);
                    Utils.sleep(2000);
                    clickBack();
                    screemSuccess = true;
                    Utils.sleep(2000);
                    startApp();
                    return;
                }
            }

            return;
        }
    }


    private void requestOpenApp(){
        if(clickTotalMatchContent("打开App") || clickTotalMatchContent("APP内打开")){
            Utils.sleep(2000);
            clickTotalMatchContent("打开");
            Utils.sleep(2000);
            clickTotalMatchContent("确定");
            Utils.sleep(2000);
            return;
        }
        if(clickTotalMatchContent("打开")){
            Utils.sleep(2000);
        }
    }

    /**
     * 抖音点赞
     */
    private void doTask6() {
        if(clickTotalMatchContent("打开链接")){
            Utils.sleep(5000);
            requestOpenApp();
            if(clickContent("未选中，")){
                Utils.sleep(2000);
                MyApplication.getAppInstance().getAccessbilityService().performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT);
                Utils.sleep(2000);
                clickBack();
                screemSuccess = true;
                Utils.sleep(2000);
                startApp();
                return;
            }

            if(findContent("已选中，")){
                Utils.sleep(2000);
                MyApplication.getAppInstance().getAccessbilityService().performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT);
                Utils.sleep(2000);
                clickBack();
                screemSuccess = true;
                Utils.sleep(2000);
                startApp();
                return;
            }
            return;
        }else {
            cancelTask();
        }
    }

    /**
     * 小红书点赞
     */
    private void xiaoHongShuLike() {
        requestOpenApp();
        NodeInfo nodeInfo1 = findByText("说点什么");
        if(null != nodeInfo1){
            ActionUtils.zuohua();
            Utils.sleep(2000);
            clickXY(MyApplication.getScreenWidth()/2,nodeInfo1.getRect().centerY());
            Utils.sleep(2000);
            MyApplication.getAppInstance().getAccessbilityService().performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT);
            Utils.sleep(2000);
            clickBack();
            screemSuccess = true;
            Utils.sleep(2000);
            startApp();
            return;
        }
        Utils.sleep(1000*20);
        NodeInfo nodeInfo = findByText("发弹幕");
        clickXY(MyApplication.getScreenWidth()/2,nodeInfo.getRect().centerY());
        Utils.sleep(2000);
        MyApplication.getAppInstance().getAccessbilityService().performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT);
        Utils.sleep(2000);
        clickBack();
        screemSuccess = true;
        Utils.sleep(2000);
        startApp();

    }

    SearchAuthorBean curSearchAuthorBean;
    private void doPageId3Things() {
        LogUtils.d(TAG, "doPageId3Things");

        for(SearchAuthorBean searchAuthorBean : searchAuthorBeanList){
            if(clickContent(searchAuthorBean.getTaskName())){
                curTaskType = 97;
                curSearchAuthorBean = searchAuthorBean;
                return;
            }
        }
//        if(clickContent("简单搜索浏览")){
//            taskName = "简单搜索浏览";
//            curTaskType = 99;
//            return;
//        }
//        if(clickContent("简单浏览任务")){
//            taskName = "简单搜索浏览";
//            curTaskType = 99;
//            return;
//        }
    }

    /**
     * 知乎浏览任务
     */
    private void doZhiHuTask(SearchAuthorBean searchAuthorBean) {
        PackageUtils.startApp("com.zhihu.android");
        Utils.sleep(8000);
        if(clickId("input")){
            Utils.sleep(2000);
            AccessibilityNodeInfo textInfo = findAccessibilityNodeById("com.zhihu.android:id/input");
            if (textInfo != null) {
                Bundle arguments = new Bundle();
                arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, searchAuthorBean.getSearchKey());
                textInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
                Utils.sleep(2000);
                if(clickContent("的搜索结果")){
                    Utils.sleep(2000);
                    while (!findContent(searchAuthorBean.getAuthor()) && !isTargetPkg()){
                        scrollUp();
                        Utils.sleep(2000);
                    }
                    MyApplication.getAppInstance().getAccessbilityService().performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT);
                    Utils.sleep(5000);
                    if(clickContent(searchAuthorBean.getAuthor())){
                        Utils.sleep(2000);
                        MyApplication.getAppInstance().getAccessbilityService().performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT);
                        while (!findContent("著作权归作者所有") && !findContent("来自专栏")  && !isTargetPkg()){
                            scrollUp();
                            Utils.sleep(2000);
                        }
                        MyApplication.getAppInstance().getAccessbilityService().performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT);
                        Utils.sleep(2000);
                        clickBack();
                        Utils.sleep(2000);
                        clickBack();
                        Utils.sleep(2000);
                        screemSuccess = true;
                        startApp();

                    }

                }else {
                    clickBack();
                    Utils.sleep(2000);
                    startApp();
                }
            }else {
                clickBack();
                Utils.sleep(2000);
                startApp();
            }
        }else {
            clickBack();
            Utils.sleep(2000);
            startApp();
        }

    }

    private boolean getKeyAndAuthor() {
        String content = getContent("知乎搜索：");
        if(TextUtils.isEmpty(content)){
            return false;
        }
        int index_searchkey = content.indexOf("搜索：");
        int index_author = content.indexOf("账号：");
        String searchKey = content.substring(index_searchkey+3,content.indexOf("b.")).trim();
        String author = content.substring(index_author+3,index_author+6).trim();
        LogUtils.d(TAG,"searchKey:"+searchKey + " author:"+author);
        if(TextUtils.isEmpty(searchKey) || TextUtils.isEmpty(author)){
            return false;
        }
        curSearchAuthorBean = new SearchAuthorBean();
        curSearchAuthorBean.setSearchKey(searchKey);
        curSearchAuthorBean.setAuthor(author);
        curSearchAuthorBean.setType(2);
        return true;
    }

    private boolean getKeyAndAuthor98() {
        return false;
    }

    List<SearchAuthorBean> searchAuthorBeanList;
    private void addData(){
        searchAuthorBeanList = new ArrayList<>();
//        searchAuthorBeanList.add(new SearchAuthorBean(1,"浏览aa15","空气净化器推荐","刘万程"));
//        searchAuthorBeanList.add(new SearchAuthorBean(1,"浏览m30","踢脚线取暖器","刘万程"));
        searchAuthorBeanList.add(new SearchAuthorBean(1,"浏览l36","泡脚桶","享受生活的K叔"));
        searchAuthorBeanList.add(new SearchAuthorBean(1,"浏览m23","智能马桶","工程师小张"));
//        searchAuthorBeanList.add(new SearchAuthorBean("浏览aa15","",""));
//        searchAuthorBeanList.add(new SearchAuthorBean("浏览aa15","",""));
//        searchAuthorBeanList.add(new SearchAuthorBean("浏览aa15","",""));
    }

}

