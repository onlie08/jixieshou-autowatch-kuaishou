package com.ch.scripts;

import static android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_TAKE_SCREENSHOT;
import static com.ch.core.utils.ActionUtils.pressHome;

import android.graphics.Point;
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
import com.ch.core.utils.Constant;
import com.ch.core.utils.Utils;
import com.ch.jixieshou.BuildConfig;
import com.ch.model.AppInfo;
import com.ch.model.ScreenShootEvet;
import com.tencent.bugly.crashreport.CrashReport;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

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

    private int pageId = -1;//0:首页 1:个人中心  2:阅读页  3:广告页
    private int lastPageId = -1; //上次的页面

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
            doPageId2Things();

        } else if (pageId == 3) {
            if (clickAdvert()) return;
            doPageId3Things();

        } else if (pageId == 4) {
            doPageId4Things();

        }else if (pageId == 5) {
            doPageId5Things();

        } else {
            if (clickAdvert()) return;
            if (samePageCount > 5) {
                if (clickContent("坚持退出")) return;
            }
            if (clickContent("看视频得金币")) ;
            if (clickContent("视频再领")) return;
            if (clickEveryTotalMatchByText("好的")) ;

            clickBack();
        }

    }


    @Override
    protected void doSamePageDeal() {
        if (samePageCount > 3) {
            refreshNodeinfo();
        }
        if (samePageCount > 10 && samePageCount < 13) {
            clickBack();
        }

        if (samePageCount > 12 && samePageCount < 16) {
            if(dealNoResponse2()) samePageCount = 0;
        }
//        if (samePageCount > 15) {
//            tryClickDialog();
//        }

        if (samePageCount > 25) {
            clickXY(point_ShouYe.x, point_ShouYe.y);
        }
    }

    private int gotoPersonCount = 0;

    private void doPageId0Things() {
        gotoPersonCount++;
        if (gotoPersonCount > 8) {
            gotoPersonCount = 0;
            clickXY(point_RenWu.x, point_RenWu.y);
            return;
        }
        LogUtils.d(TAG, "doPageId0Things");

        if (clickContent("领金币")) {
            clickAdvert();
            return;
        }

        scrollUp();

//        List<AccessibilityNodeInfo> accessibilityNodeInfos = findAccessibilityNodeListById("com.ss.android.article.lite:id/ajb");
//        if (null == accessibilityNodeInfos) {
//            accessibilityNodeInfos = findAccessibilityNodeListById("com.ss.android.article.lite:id/afk");
//        }
//        if (null == accessibilityNodeInfos) {
//            accessibilityNodeInfos = findAccessibilityNodeListById("com.ss.android.article.lite:id/agp");
//        }
//
//        if (null != accessibilityNodeInfos) {
//            for (int i = 0; i < accessibilityNodeInfos.size(); i++) {
//                LogUtils.d(TAG, "nodeInfo.getChildCount():" + accessibilityNodeInfos.get(i).getChildCount() + " postion:" + i);
//                if (accessibilityNodeInfos.get(i).getChildCount() > 2) {
//                    continue;
//                } else {
//                    AccessibilityNodeInfo accessibilityNodeInfo = accessibilityNodeInfos.get(i);
//                    Rect nodeRect = new Rect();
//                    accessibilityNodeInfo.getBoundsInScreen(nodeRect);
//                    clickXY(nodeRect.centerX(), nodeRect.centerY());
//
//                    return;
//                }
//            }
//        } else {
//            clickContent("0评论");
//        }

        if (clickContent("继续阅读")) return;

    }

    private void doPageId1Things() {
        LogUtils.d(TAG, "doPageId1Things");
        if (samePageCount > 2) {
            //签到弹出窗口
            scrollDown();
            clickXY(point_ShouYe.x, point_ShouYe.y);
        }

        //阅读翻倍
        if (clickEveryTotalMatchByText("点击翻倍")) {
            if (clickContent("我知道了")) ;
        }

        //看广告赚金币
        if (clickTotalMatchContent("看广告赚金币"));
        if (clickTotalMatchContent("逛商品赚金币"));
        if (clickTotalMatchContent("好的")) ;


        if (clickContent("填写邀请码")) ;

        if (clickEveryNodeInfosByText("额外再领")) ;
        if (clickEveryTotalMatchByText("直接领取")) ;

        if (clickEveryTotalMatchByText("开宝箱得金币")) {
            clickAdvert();
            return;
        }

        //限时专享
        if (clickTotalMatchContent("领取")) {
            clickAdvert();
        }
        if(clickEveryNodeInfosByText("打开签到提醒+")){
            clickEveryNodeInfosByText("允许");
        }
        if (clickEveryTotalMatchByText("点击领取"));
        if (clickEveryTotalMatchByText("立即领取"));
        if (clickEveryTotalMatchByText("开心收下"));
        if (clickEveryTotalMatchByText("领福利")) {
            clickAdvert();
        }


//        clickXY(point_ShouYe.x, point_ShouYe.y);
        scrollUpSlow();
    }

    /**
     * 文章阅读找金币
     */
    private void doPageId2Things() {
        LogUtils.d(TAG, "doPageId2Things");
        //todo 有的手机找不到mc-footer无法领取金币
        NodeInfo nodeInfo = findById("mc-footer");
        AccessibilityNodeInfo webRoot = getWebViewRoot();
        if(null != webRoot){
            int count = webRoot.getChildCount();
            Log.d(TAG,"层级1"+count);
            for(int i=0;i<count;i++){
                int childCount = webRoot.getChild(i).getChildCount();
                Log.d(TAG,"层级2"+childCount);
                for(int j=0;j<childCount;j++){
                    int childchildCount = webRoot.getChild(i).getChild(j).getChildCount();
                    Log.d(TAG,"层级3"+childchildCount);
                }
            }
        }
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
    }

    private void doPageId3Things() {
        LogUtils.d(TAG, "doPageId3Things");
        if (clickEveryNodeInfosByText("继续观看")) return;
    }

    /**
     * 弹出框里点击看广告
     */
    private boolean clickAdvert() {
        if (clickEveryNodeInfosByText("视频再领")) return true;
        if (clickEveryNodeInfosByText("再看一个获得")) return true;
        if (clickEveryNodeInfosByText("继续观看")) return true;
        if (clickEveryNodeInfosByText("看视频领")) return true;
        return false;
    }

    /**
     * 检查是在那个页面
     *
     * @return //0:首页 1:个人中心  2:阅读页  3:广告页
     */
    private int checkPageId() {
        if (findContent("每日凌晨，金币自动兑换成现金")) {
            return 1;
        }
        if (findContent("我的现金：")) {
            return 1;
        }
        if (findContent("频道管理") && findContent("发布")) {
            return 0;
        }
        if ((findContent("每日挑战") || findContent("新人福利") || findContent("日常任务") || findContent("看广告赚金币")) && findContent("金币")) {
            return 1;
        }
        if ((findContent("s后可领取"))) {
            return 3;
        }
        if ((findContent("s") && findTotalMatchContent("关闭")) || findTotalMatchContent("试玩")) {
            return 3;
        }

        if (findTotalMatchContent("搜索") || findTotalMatchContent("更多操作")) {
            return 2;
        }


        if (findContent("邀请码") && findContent("马上提交")) {
            return 4;
        }
        if (findContent("浏览以下商品30秒")) {
            return 5;
        }


        return -1;
    }


    @Override
    protected int getMinSleepTime() {
        if (pageId == 2) {
            return 1000;
        } else if (pageId == 1) {
            return 1000;
        } else if (pageId == -1) {
            return 1000;
        } else if (pageId == 0) {
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
            return 1000;
        } else if (pageId == -1) {
            return 1000;
        } else if (pageId == 0) {
            return 1000;
        } else {
            return 4000;
        }
    }

    @Override
    protected void getRecognitionResult() {
        AccessibilityNodeInfo tabs = findAccessibilityNodeById("tabs");
        if (null != tabs && tabs.getChildCount() == 5) {
            Rect rect1 = new Rect();
            tabs.getChild(0).getBoundsInScreen(rect1);
            Rect rect2 = new Rect();
            tabs.getChild(2).getBoundsInScreen(rect2);

            point_ShouYe = new Point(rect1.centerX(), rect1.centerY());
            point_RenWu = new Point(rect2.centerX(), rect2.centerY());
            return;
        }

        point_ShouYe = new Point(MyApplication.getScreenWidth() / 5 - SizeUtils.dp2px(30), MyApplication.getScreenHeight() - SizeUtils.dp2px(15));
        point_RenWu = new Point(MyApplication.getScreenWidth() / 2, MyApplication.getScreenHeight() - SizeUtils.dp2px(15));
        LogUtils.d(TAG, "point_ShouYe:" + point_ShouYe.toString() + " point_RenWu:" + point_RenWu.toString() + " Height:" + MyApplication.getScreenHeight());

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
                clickBack();
                dealNoResponse2();
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
            doubleClickBack();
        }
        pressHome();
        stop = true;
    }

    public void doScan(int second) {
        for (int i = 0; i < second; i++) {
            if (isCurrentScipte() && pageId == 5) {
                scrollUp();
            }
        }
    }
    //逛街赚钱
    private void doPageId5Things() {
        doScan(16);
        clickBack();
    }

    private void doPageId4Things() {
        AccessibilityNodeInfo textInfo = findEditText();
        if (textInfo != null) {
            Bundle arguments = new Bundle();
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, MyApplication.recommendBean.getCode_toutiao());
            textInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
            Utils.sleep(2000);
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

