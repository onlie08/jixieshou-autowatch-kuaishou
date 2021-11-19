package com.ch.scripts;

import android.graphics.Point;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityNodeInfo;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.ch.application.MyApplication;
import com.ch.core.search.node.NodeInfo;
import com.ch.core.service.MyAccessbilityService;
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

import java.util.List;
import java.util.Random;

import static android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_TAKE_SCREENSHOT;
import static com.ch.core.utils.ActionUtils.click;
import static com.ch.core.utils.ActionUtils.pressHome;

public class BaiDuAdvertScript extends BaseScript {

    private String TAG = this.getClass().getSimpleName();
    private Point point_ShouYe;
    private Point point_RenWu;
    private Point point_TianXieYaoQingMa1;
    private Point point_TianXieYaoQingMa2;
    private Point point_TianXieYaoQingMa3;
    private Point point_ZhanTie;

    private volatile static BaiDuAdvertScript instance; //声明成 volatile

    public static BaiDuAdvertScript getSingleton(AppInfo appInfo) {
        if (instance == null) {
            synchronized (BaiDuAdvertScript.class) {
                if (instance == null) {
                    instance = new BaiDuAdvertScript(appInfo);
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
            if (!MyApplication.getAppInstance().getAccessbilityService().containsPkg(Constant.PN_BAI_DU)) {
                return false;
            }
        }
        return true;
    }

    public BaiDuAdvertScript(AppInfo appInfo) {
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
        LogUtils.d(TAG, "pageId:" + pageId + " samePageCount:" + samePageCount+ " gotoPersonCount:" + gotoPersonCount);

        doSamePageDeal();

        if (clickAdvert()) return;

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

        } else if (pageId == 5) {

            doPageId5Things();

        } else if (pageId == 6) {

            doPageId6Things();

        } else {
//            if(clickContent("重新加载"))return;
            if(samePageCount >= 2){
                if(clickContent("我知道了"))return;
                if(clickContent("开心收下"))return;
            }
            Utils.sleep(1500);
            clickBack();
        }

    }

    private void doPageId6Things() {
        clickContent("看文章赚金币");
    }

    private int gotoPersonCount = 0;
    private void doPageId0Things() {
        LogUtils.d(TAG, "doPageId0Things");

        if (point_ShouYe == null) {
            getRecognitionResult();
            if (point_ShouYe == null) {
                EventBus.getDefault().post(new ScreenShootEvet(Constant.PN_BAI_DU,Constant.PAGE_MAIN));
            }
            return;
        }

        if (point_RenWu == null) {
            getRecognitionResult();
            if (point_RenWu == null) {
                EventBus.getDefault().post(new ScreenShootEvet(Constant.PN_BAI_DU,Constant.PAGE_MAIN));
            }
            return;
        }

        gotoPersonCount++;
        if (gotoPersonCount > 10) {
            gotoPersonCount = 0;
            clickXY(point_RenWu.x,point_RenWu.y);
            return;
        }


        if (clickId("fc6")) return;

        scrollUp();

        Utils.sleep(2000);

        List<AccessibilityNodeInfo> accessibilityNodeInfos = findAccessibilityNodeListById("com.baidu.searchbox.lite:id/c8");
        if(null != accessibilityNodeInfos){
            for(int i =0;i<accessibilityNodeInfos.get(0).getChildCount();i++){
                AccessibilityNodeInfo accessibilityNodeInfo = accessibilityNodeInfos.get(0).getChild(i);
                if(accessibilityNodeInfo.getChildCount() == 2){
                    Rect rect = new Rect();
                    accessibilityNodeInfo.getBoundsInScreen(rect);
                    clickXY(rect.centerX(),rect.centerY());
                    return;
                }
            }
        }

//        clickXY(500,500);
//        if (clickContent("0评论")) return;

    }

    private void doPageId1Things() {
        LogUtils.d(TAG, "doPageId1Things");
        if(samePageCount >= 2){
            if(clickContent("我知道了"))return;
            if(clickContent("残忍退出"))return;
            if(clickContent("开心收下"))return;
            if(clickContent("立即签到")){
                Utils.sleep(1000);
                clickBack();
                return;

            }
        }

        if(clickContent("立即收下"))return;
        if(clickContent("开宝箱得金币"))return;

        if(!findContent("看广告赚钱")){
            scrollUpSlow();
            return;
        }else {
            NodeInfo nodeInfo = findByText("看广告赚钱");
            clickXY(MyApplication.getScreenWidth()-SizeUtils.dp2px(70),nodeInfo.getRect().centerY());
            Utils.sleep(2000);
            if(findContent("后得金币")){
                return;
            }
        }

        if(clickContent("去签到"))return;

        clickXY(point_ShouYe.x,point_ShouYe.y);
        return;

    }

    private void doPageId2Things() {
        LogUtils.d(TAG, "doPageId2Things");

        NodeInfo nodeInfo = findByText("不喜欢");
        if (null != nodeInfo) {
//            if(samePageCount > 3){
//                scrollUp();
//            }
            clickXY(nodeInfo.getRect().centerX(),nodeInfo.getRect().centerY()+ SizeUtils.dp2px(100));
            Utils.sleep(3000);
            if(checkPageId() == -1){
                clickBack();
                Utils.sleep(2000);
                clickBack();
                return;
            }
            return;

        }
        scrollUp();
    }

    private void doPageId3Things() {
        LogUtils.d(TAG, "doPageId3Things");
        if(samePageCount >5){
            clickBack();
        }

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
        if (findContent("频道管理") && findContent("推荐")) {
            return 0;
        }

        if (findContent("做任务赚现金") || findContent("看广告赚钱")) {
            return 1;
        }

        if (findContent("分享") && findContent("收藏")&& findContent("返回")) {
            return 2;
        }

        if (findContent("查看详情")) {
            return 3;
        }

        if (findContent("s后得金币")) {
            return 3;
        }

        if (findContent("预估总收益")) {
            return 5;
        }
        if (findContent("看文章赚金币")) {
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
            return 3000;
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
        String sp_shouye = SPUtils.getInstance().getString(Constant.BAIDU_SHOUYE,"");
        if(!TextUtils.isEmpty(sp_shouye)){
            point_ShouYe = new Gson().fromJson(sp_shouye,Point.class);
        }

        String sp_renwu = SPUtils.getInstance().getString(Constant.BAIDU_RENWU,"");
        if(!TextUtils.isEmpty(sp_renwu)){
            point_RenWu = new Gson().fromJson(sp_renwu,Point.class);
        }

        String sp_tianxieyaoqingma1 = SPUtils.getInstance().getString(Constant.BAIDU_TIANXIEYAOQINGMA1,"");
        if(!TextUtils.isEmpty(sp_tianxieyaoqingma1)){
            point_TianXieYaoQingMa1 = new Gson().fromJson(sp_tianxieyaoqingma1,Point.class);
        }

        String sp_tianxieyaoqingma2 = SPUtils.getInstance().getString(Constant.BAIDU_TIANXIEYAOQINGMA2,"");
        if(!TextUtils.isEmpty(sp_tianxieyaoqingma2)){
            point_TianXieYaoQingMa2 = new Gson().fromJson(sp_tianxieyaoqingma2,Point.class);
        }

        String sp_tianxieyaoqingma3 = SPUtils.getInstance().getString(Constant.BAIDU_TIANXIEYAOQINGMA3,"");
        if(!TextUtils.isEmpty(sp_tianxieyaoqingma3)){
            point_TianXieYaoQingMa3 = new Gson().fromJson(sp_tianxieyaoqingma3,Point.class);
        }

        String sp_zhantie = SPUtils.getInstance().getString(Constant.BAIDU_ZHANTIE,"");
        if(!TextUtils.isEmpty(sp_zhantie)){
            point_ZhanTie = new Gson().fromJson(sp_zhantie,Point.class);
        }
    }


    public boolean isCurrentScipte() {
        return getAppInfo().getPkgName().equals(Constant.PN_BAI_DU) ? true : false;
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
            }
            if (resumeCount > 10) {
                if (BuildConfig.DEBUG) {
                    MyApplication.getAppInstance().getAccessbilityService().performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT);
                }
                LogUtils.d(TAG, "头条极速版是不是anr了?");
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
        if (clickContent("去赚钱")) return true;
        if (clickContent("允许")) return true;
        if (clickContent("立即添加")) return true;
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

        if(null == point_TianXieYaoQingMa1){
            SPUtils.getInstance().put(Constant.BAIDU_TIANXIEYAOQINGMA2, "");
            SPUtils.getInstance().put(Constant.BAIDU_TIANXIEYAOQINGMA3, "");
            SPUtils.getInstance().put(Constant.BAIDU_ZHANTIE, "");
        }

        if(null != point_ZhanTie){
            clickXY(point_ZhanTie.x, point_ZhanTie.y);
            Utils.sleep(1000);
            clickXY(point_TianXieYaoQingMa3.x, point_TianXieYaoQingMa3.y);
            Utils.sleep(2000);
            CrashReport.postCatchedException(new Throwable("百度自动填写邀请码成功"));
        }

        if(null == point_TianXieYaoQingMa1){
            EventBus.getDefault().post(new ScreenShootEvet(Constant.PN_BAI_DU,Constant.PAGE_INVITE));
            return false;
        }

        if(null != point_TianXieYaoQingMa1 && point_TianXieYaoQingMa2 == null){
            clickXY(point_TianXieYaoQingMa1.x, point_TianXieYaoQingMa1.y);
            Utils.sleep(1000);
            EventBus.getDefault().post(new ScreenShootEvet(Constant.PN_BAI_DU,Constant.PAGE_INVITE));
        }

        if(null != point_TianXieYaoQingMa2 && point_TianXieYaoQingMa3 != null){
            ActionUtils.longPress(point_TianXieYaoQingMa2.x, (point_TianXieYaoQingMa3.y + point_TianXieYaoQingMa2.y)/2);
            Utils.sleep(1500);
            EventBus.getDefault().post(new ScreenShootEvet(Constant.PN_BAI_DU,Constant.PAGE_INVITE));
        }
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

