package com.ch.scripts;

import static android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_TAKE_SCREENSHOT;

import static com.ch.core.utils.ActionUtils.pressHome;

import android.graphics.Point;
import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.ch.application.MyApplication;
import com.ch.core.utils.Constant;
import com.ch.core.utils.Utils;
import com.ch.jixieshou.BuildConfig;
import com.ch.model.AppInfo;
import com.google.gson.Gson;
import com.tencent.bugly.crashreport.CrashReport;

public class MTZDScript extends BaseScript {

    private String TAG = this.getClass().getSimpleName();
    private Point point_ShouYe;
    private Point point_WoDe;
    private int pageId = -1;//0:首页 1:个人中心  2:广告页 3：幸运大转盘 4:看广告赚金币
    private int lastPageId = -1; //上次的页面
    private int samePageCount = 0; //同一个页面停留次数

    private volatile static MTZDScript instance; //声明成 volatile

    public static MTZDScript getSingleton(AppInfo appInfo) {
        if (instance == null) {
            synchronized (MTZDScript.class) {
                if (instance == null) {
                    instance = new MTZDScript(appInfo);
                }
            }
        }
        return instance;
    }

    public MTZDScript(AppInfo appInfo) {
        super(appInfo);
    }

    @Override
    protected int getMaxSleepTime() {
        return 2000;
    }

    @Override
    protected int getMinSleepTime() {
        return 2000;
    }

    @Override
    protected void getRecognitionResult() {
        String sp_shouye = SPUtils.getInstance().getString(Constant.MEITIANZHUANDIAN_SHOUYE, "");
        if (!TextUtils.isEmpty(sp_shouye)) {
            point_ShouYe = new Gson().fromJson(sp_shouye, Point.class);
        }
        String sp_wode = SPUtils.getInstance().getString(Constant.MEITIANZHUANDIAN_WODE, "");
        if (!TextUtils.isEmpty(sp_wode)) {
            point_WoDe = new Gson().fromJson(sp_wode, Point.class);
        }
    }

    @Override
    protected boolean isTargetPkg() {
        if (MyApplication.getAppInstance().getAccessbilityService().isWrokFine()) {
            if (!MyApplication.getAppInstance().getAccessbilityService().containsPkg(Constant.PN_MEI_TIAN_ZHUAN_DIAN)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断当前页面是不是小红书界面
     * @return
     */
    private boolean isXHSPkg(){
        if (MyApplication.getAppInstance().getAccessbilityService().isWrokFine()) {
            if (!MyApplication.getAppInstance().getAccessbilityService().containsPkg(Constant.PN_XIAO_HONG_SHU)) {
                return false;
            }
        }
        return true;
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
        if (samePageCount > 30) {
            clickBack();
            clickBack();
            samePageCount = 0;
        }

    }

    public boolean isCurrentScipte() {
        return getAppInfo().getPkgName().equals(Constant.PN_MEI_TIAN_ZHUAN_DIAN) ? true : false;
    }

    int resumeCount = 0;
    @Override
    public boolean isDestinationPage() {
        if (!isTargetPkg() && isCurrentScipte() && !isXHSPkg()) {
            if (!NetworkUtils.isAvailable()) {
                return false;
            }
            if (ScreenUtils.isScreenLock()) {
                return false;
            }

            resumeCount++;
            if (resumeCount > 20) {
                LogUtils.d(TAG, "自动恢复到每天赚点");
                startApp();
            }
            if (resumeCount > 22) {
                if (BuildConfig.DEBUG) {
                    MyApplication.getAppInstance().getAccessbilityService().performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT);
                }
                LogUtils.d(TAG, "每天赚点是不是anr了?");
                dealNoResponse();
                Utils.sleep(1000);
                clickBack();
                resumeCount = 0;
                CrashReport.postCatchedException(new Throwable("每天赚点无响应"));

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
        clickXY(point_ShouYe.x, point_ShouYe.y);
        return false;
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
        if (findContent("截图任务")) {
            return 0;
        }
        if (findContent("我的任务")) {
            return 1;
        }
        if (findTotalMatchContent("做任务赚钱") || findTotalMatchContent("提交审核")) {
            return 2;
        }
        if (findContent("商户详情") || findContent("剩余数量：")) {
            return 3;
        }
        return -1;
    }

    @Override
    protected void executeScript() {
        LogUtils.d(TAG, "executeScript()");
        if (!isTargetPkg() && !isXHSPkg()) {
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

        if (samePageCount > 10) {
            doSamePageDeal();
            return;
        }

        switch (pageId) {
//            case 0:
//                doPageId0Things();
//                break;
//            case 1:
//                doPageId1Things();
//                break;
//            case 2:
//                doPageId2Things();
//                break;
//            case 3:
////                doPageId3Things();
//                break;
//            case 4:
//                doPageId4Things();
//                break;
            case -1:
                if (samePageCount > 8) {
                    scrollDown();
                }
                Utils.sleep(1500);
                clickBack();
                break;
        }
    }

}
