package com.ch.scripts;

import android.graphics.Point;
import android.text.TextUtils;

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

public class JingDongAdvertScript extends BaseScript {

    private String TAG = this.getClass().getSimpleName();
    private Point point_ZhuanJinBi;
    private Point point_YiQian;
    private boolean signToday = false;

    private volatile static JingDongAdvertScript instance; //声明成 volatile

    public static JingDongAdvertScript getSingleton(AppInfo appInfo) {
        if (instance == null) {
            synchronized (JingDongAdvertScript.class) {
                if (instance == null) {
                    instance = new JingDongAdvertScript(appInfo);
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
            if (!MyApplication.getAppInstance().getAccessbilityService().containsPkg(Constant.PN_JING_DONG)) {
                return false;
            }
        }
        return true;
    }

    public JingDongAdvertScript(AppInfo appInfo) {
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

        if (pageId == 0) {

            doPageId0Things();

        } else if (pageId == 1) {

            doPageId1Things();

        } else if (pageId == 2) {

            doPageId2Things();

        } else if (pageId == 3) {

            doPageId3Things();

        }  else {
            Utils.sleep(1500);
            clickBack();
        }

    }

    /**
     * 首页
     */
    private void doPageId0Things() {
        LogUtils.d(TAG, "doPageId0Things");
        if(!signToday){
            if(clickTotalMatchContent("签到免单")){
                Utils.sleep(3000);
                if(findTotalMatchContent("签到免单")){
                    if (null == point_YiQian) {
                        getRecognitionResult();
                        if (null == point_YiQian) {
                            EventBus.getDefault().post(new ScreenShootEvet(Constant.PN_JING_DONG, Constant.PAGE_MAIN));
                            Utils.sleep(2000);
                            clickBack();
                            return;
                        }
                    }

                    clickXY(MyApplication.getScreenWidth()- SizeUtils.dp2px(80),point_YiQian.y);
                    Utils.sleep(2000);
                    signToday = true;
                }
                clickBack();
                return;
            }
        }
        if (clickContent("赚钱")) return;

    }

    /**
     * 赚金币页
     */
    private void doPageId1Things() {
        LogUtils.d(TAG, "doPageId1Things");
        if (null == point_ZhuanJinBi) {
            getRecognitionResult();
            if (null == point_ZhuanJinBi) {
                EventBus.getDefault().post(new ScreenShootEvet(Constant.PN_JING_DONG, Constant.PAGE_TASK));
                return;
            }
        }
        clickXY(point_ZhuanJinBi.x, point_ZhuanJinBi.y);

    }

    /**
     * 赚金币任务展开页
     */
    private void doPageId2Things() {
        LogUtils.d(TAG, "doPageId2Things");
        if (samePageCount > 3) {
            List<NodeInfo> nodeInfoList = findAllTotalMatchByText("已完成");
            if(null != nodeInfoList && nodeInfoList.size() == 3){
                setTodayDone(true);
                CrashReport.postCatchedException(new Exception("京东今日任务完成"));
                skipTask();
                return;
            }
            clickBack();
            return;
        }

        if (clickContent("逛商品赚金币")) Utils.sleep(3000);
        if (clickContent("逛活动赚金币")) Utils.sleep(3000);
        if (clickContent("看视频赚金币")) Utils.sleep(3000);

//      if(clickContent("邀好友赚金币"))Utils.sleep(5000);
//      if(clickContent("东东爱消除"))Utils.sleep(5000);
    }

    /**
     * 逛商品任务页
     */
    private void doPageId3Things() {
        LogUtils.d(TAG, "doPageId3Things");
        if (findContent("今日已完成")) {
            while (checkPageId() != 2 && isTargetPkg()) {
                clickBack();
                Utils.sleep(1500);
            }
            return;
        }
        if (clickContent("点击逛下一个")) {
            samePageCount = 0;
            return;
        }
        scrollUp();

    }

    /**
     * 检查是在那个页面
     *
     * @return //0:首页 1:个人中心  2:阅读页  3:广告页
     */
    private int checkPageId() {
        if (findContent("金币大宝箱")) {
            return 3;
        }
        if (findContent("商品") && findContent("评价") && findContent("详情") && findContent("推荐")) {
            return 3;
        }
        if (findContent("搜索框") && findContent("更多")) {
            return 0;
        }

        if (findContent("逛商品赚金币") && findContent("逛活动赚金币")) {
            return 2;
        }

        if (findContent("约¥") && findContent("规则")) {
            return 1;
        }

        if (findContent("返回") && findContent("更多菜单")) {
            return 3;
        }
        if ((findContent("分享") && findContent("/5"))) {
            return 3;
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
            return 2000;
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
    protected void getRecognitionResult() {
        String sp_zhuanjinbi = SPUtils.getInstance().getString(Constant.JINGDONG_ZHUANJINBI, "");
        if (!TextUtils.isEmpty(sp_zhuanjinbi)) {
            point_ZhuanJinBi = new Gson().fromJson(sp_zhuanjinbi, Point.class);
        }
        String sp_yiqian = SPUtils.getInstance().getString(Constant.JINGDONG_YIQIAN, "");
        if (!TextUtils.isEmpty(sp_yiqian)) {
            point_YiQian = new Gson().fromJson(sp_yiqian, Point.class);
        }

    }


    public boolean isCurrentScipte() {
        return getAppInfo().getPkgName().equals(Constant.PN_JING_DONG) ? true : false;
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
                CrashReport.postCatchedException(new Throwable("京东极速版无响应"));

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
        if (clickTotalMatchContent("以后再说")) return true;
        if (clickContent("关闭")) return true;
        if (clickContent("重试")) return true;
        if (clickContent("取消")) return true;
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
            tryClickDialog();
        }
        if (samePageCount > 30) {
            clickBack();
            clickBack();
            samePageCount = 0;
        }

    }
}

