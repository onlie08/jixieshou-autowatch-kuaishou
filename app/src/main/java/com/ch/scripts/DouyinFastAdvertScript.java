package com.ch.scripts;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.ch.application.MyApplication;
import com.ch.core.search.node.NodeInfo;
import com.ch.core.utils.Constant;
import com.ch.core.utils.Utils;
import com.ch.model.AppInfo;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.Random;

import static android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_TAKE_SCREENSHOT;
import static com.ch.core.utils.ActionUtils.pressHome;

public class DouyinFastAdvertScript extends BaseScript {
    private String TAG = this.getClass().getSimpleName();

    private volatile static DouyinFastAdvertScript instance; //声明成 volatile

    public static DouyinFastAdvertScript getSingleton(AppInfo appInfo) {
        if (instance == null) {
            synchronized (DouyinFastAdvertScript.class) {
                if (instance == null) {
                    instance = new DouyinFastAdvertScript(appInfo);
                }
            }
        }
        return instance;
    }

    private boolean adverting = false;
    private int samePageCount = 0; //同一个页面停留次数
    private int lastPageId = -1; //上次的页面
    private int pageId = -1;//0:首页 1:个人中心  2:阅读页  3:广告页

    @Override
    protected boolean isTargetPkg() {
        if(MyApplication.getAppInstance().getAccessbilityService().isWrokFine()) {
            if(!MyApplication.getAppInstance().getAccessbilityService().containsPkg(Constant.PN_DOU_YIN)) {
                return false;
            }
        }
        return true;
    }

    public DouyinFastAdvertScript(AppInfo appInfo) {
        super(appInfo);
    }

    @Override
    protected void executeScript() {
        LogUtils.d(TAG,"executeScript");
        if (!isTargetPkg()) {
            return;
        }
        if (!NetworkUtils.isAvailable()) {
            return;
        }
        if (ScreenUtils.isScreenLock()) {
            return;
        }

        if (samePageCount > 10 && samePageCount < 13) {
            dealNoResponse2();
        }

        if (samePageCount > 12 && samePageCount < 16) {
            Utils.sleep(1500);
            clickBack();
        }

        if (samePageCount > 15 ) {
            dealNoResponse3();
        }
//
//        if(count >30 ){
//            MyApplication.getAppInstance().getAccessbilityService().performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT);
//            Utils.sleep(1500);
//            dealNoResponse();
//            count = 0;
//            return;
//        }

        if (clickAdvert()) return;

        pageId = checkPageId();
        if (pageId == lastPageId) {
            samePageCount++;
        } else {
            samePageCount = 0;
        }
        lastPageId = pageId;
        LogUtils.d(TAG, "pageId:" + pageId + " samePageCount:" + samePageCount);
        if (pageId == 0) {

            doPageId0Things();

        } else if (pageId == 1) {

            doPageId1Things();

        } else if (pageId == 2) {

            doPageId2Things();

        } else {
            clickBack();
            count++;
        }
    }

    /**
     * 处理返回解决不了的弹出框，而且也不能找到资源的
     *
     * @return
     */
    private boolean dealNoResponse3() {
        int height = ScreenUtils.getScreenHeight();
        int height1 = height / 20;
        int width = ScreenUtils.getScreenWidth();
        Random rand = new Random();
        int randHeight = 20 + rand.nextInt(height1 - 20);
        LogUtils.d(TAG, "x:" + (width / 2) + " y:" + (randHeight * 20));
        clickXY(width / 2, randHeight * 20);
        return false;
    }

    /**
     * 处理返回解决不了的弹出框，但是能找到资源的
     * @return
     */
    private boolean dealNoResponse2() {
        if (clickContent("知道")) return true;
        if (clickContent("立即签到")) return true;
        if (clickContent("允许")) return true;
        if (clickContent("立即添加")) return true;
        if (clickContent("关闭")) return true;
        if (clickContent("重试")) return true;
        if (clickContent("取消")) return true;
        return false;
    }

    private void doPageId0Things() {
        count++;
        LogUtils.d(TAG, "doPageId0Things");

        if(count >10){
            if(clickId("byf")) return;
        }

        scrollUp();
    }


    private void doPageId1Things() {
        count++;
        LogUtils.d(TAG, "doPageId1Things");

        if (!findContent("看广告赚金币")) {
            scrollUp();
            return;
        }

        if (clickContent("开宝箱得金币")) return;

        if (clickContent("看广告赚金币")) return;

//        if(countDown()){
//        }


//        clickBack();
    }

    private void doPageId2Things() {
        count++;
        LogUtils.d(TAG, "doPageId2Things");
        if (clickContent("继续观看")) return;
        boolean isAdvert = isAdverting();
        if (isAdvert) {
            adverting = isAdvert;
            return;
        }
        if (adverting && !isAdvert) {
            clickBack();
            adverting = isAdvert;
            return;
        }
        clickBack();
    }

    private boolean isAdverting() {
        NodeInfo nodeInfo1 = findByText("后可领取");
        if (nodeInfo1 != null) {
            LogUtils.dTag(TAG, "找到后可领取");
            return true;
        }
        return false;
    }


    @Override
    protected int getMinSleepTime() {
        return 3000;
    }

    @Override
    protected int getMaxSleepTime() {
        return 4000;
    }

    @Override
    protected void getRecognitionResult() {

    }


    public boolean isCurrentScipte(){
        return getAppInfo().getPkgName().equals(Constant.PN_DOU_YIN) ? true : false;
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
            if (resumeCount > 50) {
                LogUtils.d(TAG, "自动恢复到抖音极速版");
                CrashReport.postCatchedException(new Throwable("自动恢复到抖音极速版"));
                startApp();
            }
            if (resumeCount > 100) {
                MyApplication.getAppInstance().getAccessbilityService().performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT);
                LogUtils.d(TAG, "抖音极速版是不是anr了?");
                clickContent("确定");
                clickContent("取消");
                clickBack();
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
            pressHome();
//            clickBack();
//            clickBack();
        }
        stop = true;
    }

    //看广告
    private boolean clickAdvert() {
        LogUtils.d(TAG, "clickAdvert()");

        if (clickContent("看广告视频再赚")) return true;

        if (clickContent("再看一个获取")) return true;

        return false;
    }

    private boolean countDown(){
        if(findContent("09:") || findContent("08:") || findContent("07:") || findContent("06:")
                || findContent("05:")|| findContent("04:")|| findContent("03:")|| findContent("02:")
                || findContent("01:") || findContent("00:")){
            return false;
        }
        return true;
    }

    private boolean dealNoResponse() {
        if (clickContent("重试")) return true;
        if (clickContent("继续观看")) return true;

        if (clickContent("知道了")) return true;

        if (clickContent("好的")) return true;

        if (clickContent("立即签到")) return true;

        if (clickContent("打开签到提醒")) return true;

        if (clickContent("开心收下")) return true;

        if (clickId("permission_allow_button")) return true;

        if (clickContent("允许")) return true;

        if (clickContent("立即预约")) return true;

        if (clickContent("去拿奖励")) return true;

        if (clickContent("继续阅读")) return true;
        if (clickContent("去赚钱")) return true;

//        if (clickContent("新人金币礼包")) ;


        clickBack();

        Utils.sleep(1000);

        clickXY(540, 1500);
        return false;
    }


    /**
     * 检查是在那个页面
     *
     * @return //0:首页 1:个人中心  2:阅读页  3:广告页
     */
    private int checkPageId() {

        if (findId("a96") && findContent("金币")) {
            return 1;
        }

        if ((findContent("首页") && findContent("分享，按钮"))  || findContent("点击进直播间开宝箱")) {
            return 0;
        }


        if (findContent("后可领取") || findContent("广告")) {
            return 2;
        }

        return -1;
    }

}
