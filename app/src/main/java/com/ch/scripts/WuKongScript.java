package com.ch.scripts;

import static android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_TAKE_SCREENSHOT;
import static com.ch.core.utils.ActionUtils.pressHome;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.ch.application.MyApplication;
import com.ch.core.search.node.NodeInfo;
import com.ch.core.utils.ActionUtils;
import com.ch.core.utils.Constant;
import com.ch.core.utils.Utils;
import com.ch.jixieshou.BuildConfig;
import com.ch.model.AppInfo;
import com.tencent.bugly.crashreport.CrashReport;

public class WuKongScript extends BaseScript {

    private String TAG = this.getClass().getSimpleName();

    private volatile static WuKongScript instance; //声明成 volatile

    public static WuKongScript getSingleton(AppInfo appInfo) {
        if (instance == null) {
            synchronized (WuKongScript.class) {
                if (instance == null) {
                    instance = new WuKongScript(appInfo);
                }
            }
        }
        return instance;
    }

    private int pageId = -1;//0:首页 1:个人中心  2:广告页 3：幸运大转盘 4:看广告赚金币
    private int lastPageId = -1; //上次的页面


    @Override
    protected boolean isTargetPkg() {
        if (MyApplication.getAppInstance().getAccessbilityService().isWrokFine()) {
            if (!MyApplication.getAppInstance().getAccessbilityService().containsPkg(Constant.PN_WU_KONG)) {
                return false;
            }
        }
        return true;
    }

    public WuKongScript(AppInfo appInfo) {
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

        }else if (pageId == 5) {

            doPageId5Things();

        } else {

            if (clickTotalMatchContent("继续观看")) ;

            if (samePageCount >= 4) {
                if (clickTotalMatchContent("坚持退出")) ;
            }
            Utils.sleep(1500);
            clickBack();
        }

    }

    private void doPageId5Things() {
        if(samePageCount > 8){
            clickXY(MyApplication.getScreenWidth()/2,MyApplication.getScreenHeight()-SizeUtils.dp2px(10));
            return;
        }
        scrollUp();
    }
    private void doPageId4Things() {
        LogUtils.d(TAG,"doPageId4Things()");
        clickXY(MyApplication.getScreenWidth()/4,MyApplication.getScreenHeight()*3/5);
        if(clickTotalMatchContent("加入书架"));
        if(clickContent("继续阅读"));
        for(int i=0;i<30;i++){
            if(clickContent("看视频免")) return;
            ActionUtils.zuohua();
            LogUtils.d(TAG,"ActionUtils.zuohua()");
        }
        clickBack();
    }

    //    private int gotoPersonCount = 0;
    private void doPageId0Things() {
        LogUtils.d(TAG, "doPageId0Things");
        if(samePageCount > 8){
            clickXY(MyApplication.getScreenWidth()/2,MyApplication.getScreenHeight()-SizeUtils.dp2px(10));
            scrollDown();
            return;
        }

        if(clickTotalMatchContent("领金币"));

        scrollUp();
//        scrollUpPx(SizeUtils.dp2px(450));
//        NodeInfo nodeInfo = findByText("听过");
//        clickXY(MyApplication.getScreenWidth() - nodeInfo.getRect().centerX(), nodeInfo.getRect().centerY());
        return;

    }

    private void doPageId1Things() {
        LogUtils.d(TAG, "doPageId1Things");
        if (samePageCount > 5) {
            scrollDown();
            skipTask();
            return;
        }

        if (clickEveryTotalMatchByText("填写邀请码"));

        if(clickEveryTotalMatchByText("开宝箱得金币"));

        if(findTotalMatchContent("新人大红包")){
            if(clickTotalMatchContent("新人大红包"));
            if(clickTotalMatchContent("今天"));

        }

        if(clickTotalMatchContent("待领取"));

        if(clickTotalMatchContent("看广告视频1次"));

        if(clickTotalMatchContent("看小视频30分钟"));

//        if(clickTotalMatchContent("看小说30分钟"));

        if(clickTotalMatchContent("点击领奖"));

        if(clickTotalMatchContent("领取金币"));

        if(clickTotalMatchContent("去阅读"));



//        if(clickTotalMatchContent("去签到")){
//            if(clickTotalMatchContent("打开签到提醒")) {
//                if(clickEveryNodeInfosByText("运行"));
//            }
//        };


        scrollUpPx(SizeUtils.dp2px(300));
        return;
    }

    private void doScan(int second) {
        for (int i = 0; i < second; i++) {
            if (isCurrentScipte()) {
                scrollUp();
            }

        }
    }
    private void doPageId2Things() {
        LogUtils.d(TAG, "doPageId2Things");

        if (clickTotalMatchContent("继续观看"));
        if(findTotalMatchContent("重试")){
            clickBack();
            if (clickTotalMatchContent("坚持退出")) ;
            scrollUpSlow();
        }
        Utils.sleep(2000);

    }

    private void doPageId3Things() {
        LogUtils.d(TAG, "doPageId3Things");
        AccessibilityNodeInfo webViewRoot =  getWebViewRoot();
        AccessibilityNodeInfo editView= webViewRoot.getChild(0).getChild(0).getChild(0).getChild(3);
        if (editView != null) {
            Rect rect= new Rect();
            editView.getBoundsInScreen(rect);
            clickXY(rect.centerX(),rect.centerY());
            Bundle arguments = new Bundle();
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, MyApplication.recommendBean.getCode_wukong());
            editView.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
            Utils.sleep(2000);
//            clickBack();
            if (clickTotalMatchContent("绑定好友")) {
                if(clickTotalMatchContent("开心收下"));
                clickBack();
                return;
            }
        }

    }


    public AccessibilityNodeInfo findAccessibilityNode() {
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

    /**
     * 弹出框里点击看广告
     */
    private boolean clickAdvert() {
        if (clickContent("看视频领取")) return true;
        if (clickContent("视频再")) return true;
        if (clickContent("再看一条视频领")) return true;
        if (clickContent("再看一个")) return false;
        if (clickContent("看广告再得")) return true;
        if (clickContent("看广告再得")) return true;
        return false;
    }

    /**
     * 检查是在那个页面
     *
     * @return //0:首页 1:个人中心  2:阅读页  3:广告页
     */
    private int checkPageId() {
        if (findTotalMatchContent("今日热搜") && findTotalMatchContent("答题赚钱")) {
            return 0;
        }
        if (findAllPageByContent("连续签到，奖励更高",true)) {
            return 1;
        }

        if (findContent("后可领取奖励") || findContent("s关闭")|| findContent("s后再领")) {
            return 2;
        }

        if (findTotalMatchContent("1.下载软件后的10天内可输入邀请码，超过时间不能输入。")) {
            return 3;
        }
        if (findAllPageByContent("我的书架",true) && findAllPageByContent("阅读偏好",true)) {
            return 4;
        }
        if ( findTotalMatchContent("评论") && findTotalMatchContent("分享") ) {
            return 5;
        }

        return -1;
    }


    @Override
    protected int getMinSleepTime() {
        return 1000;
    }

    @Override
    protected int getMaxSleepTime() {
       return 1000;
    }

    @Override
    protected void getRecognitionResult() {
    }


    public boolean isCurrentScipte() {
        return getAppInfo().getPkgName().equals(Constant.PN_WU_KONG) ? true : false;
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
                startApp();
                samePageCount = 0;
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
                dealNoResponse2();
                Utils.sleep(2000);
                resumeCount = 0;
                CrashReport.postCatchedException(new Throwable("悟空浏览器无响应"));

            }
            return false;
        }
        resumeCount = 0;
        return true;
    }

    @Override
    public void destory() {
        if (isTargetPkg()) {
            doubleClickBack();
        }
        pressHome();
        stop = true;
    }

    @Override
    protected void doSamePageDeal() {
        if (samePageCount > 3) {
            refreshNodeinfo();
        }
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

