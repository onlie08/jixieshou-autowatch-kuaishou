package com.ch.scripts;

import android.graphics.Point;
import android.os.Bundle;
import android.view.accessibility.AccessibilityNodeInfo;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.ch.application.MyApplication;
import com.ch.core.executor.builder.SwipStepBuilder;
import com.ch.core.search.node.NodeInfo;
import com.ch.core.utils.Constant;
import com.ch.core.utils.Utils;
import com.ch.jixieshou.BuildConfig;
import com.ch.model.AppInfo;
import com.tencent.bugly.crashreport.CrashReport;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_TAKE_SCREENSHOT;
import static com.ch.core.utils.ActionUtils.pressHome;

public class FengShengFastScript extends BaseScript {
    private String TAG = this.getClass().getSimpleName();

    private volatile static FengShengFastScript instance; //声明成 volatile

    public static FengShengFastScript getSingleton(AppInfo appInfo) {
        if (instance == null) {
            synchronized (FengShengFastScript.class) {
                if (instance == null) {
                    instance = new FengShengFastScript(appInfo);
                }
            }
        }
        return instance;
    }


    private int pageId = -1;//0:首页 1:个人中心  2:阅读页  3:广告页
    private int lastPageId = -1; //上次的页面
    private int samePageCount = 0; //同一个页面停留次数

    private int type = 0; //任务执行类型 1：早上打卡 2：加班餐申请 3：晚上打卡 4：申请加班时长
    private int mHour = 0;//当前小时
    private boolean task1 = false; //早上打卡
    private boolean task2 = false; //加班餐申请
    private boolean task3 = false; //晚上打卡
    private boolean task4 = false; //申请加班时长

    public FengShengFastScript(AppInfo appInfo) {
        super(appInfo);
    }

    @Override
    protected boolean isTargetPkg() {
        if (MyApplication.getAppInstance().getAccessbilityService().isWrokFine()) {
            if (!MyApplication.getAppInstance().getAccessbilityService().containsPkg(Constant.PN_FENG_SHENG)) {
                return false;
            }
        }
        return true;
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

        if (isWeekDay()) {
            setTodayDone(true);
            skipTask();
            return;
        }
        getCurHour();

        if (mHour != 9 && mHour != 16 && mHour != 21 && mHour != 22) {
            skipTask();
            task1 = false;
            task2 = false;
            task3 = false;
            task4 = false;
            type = 0;
            return;
        }

        switch (mHour) {
            case 9:
                if (task1) {
                    type = 0;
                    skipTask();
                } else {
                    type = 1;
                }
                break;
            case 16:
                if (task2) {
                    type = 0;
                    skipTask();
                } else {
                    type = 2;
                }
                break;
            case 21:
                if (task3) {
                    type = 0;
                    skipTask();
                } else {
                    type = 3;
                }
                break;
            case 22:
                if (task4) {
                    type = 0;
                    skipTask();
                } else {
                    type = 4;
                }
                break;
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

        switch (pageId) {
            case 0:
                doLoginPage();
                break;
            case 1:
                doMainPage();
                break;
            case 2:
                doJumpPage();
                break;
            case 3:
                doCardPage();
                break;
            case 4:
                doTask3();
                break;
            case 5:
                doShenQin();
                break;
            case 6:
                doShenQin1();
                break;
            case 7:
                doShenQin2();
                break;
            default:
                Utils.sleep(1500);
                clickBack();
                break;
        }
    }

    private boolean isWeekDay() {
        Calendar c = Calendar.getInstance();
        int week = c.get(Calendar.DAY_OF_WEEK);
        if (week == 1 || week == 7) {
            return true;
        }
        return false;
    }

    private void doShenQin2() {
        AccessibilityNodeInfo root = MyApplication.getAppInstance().getAccessbilityService().getRootInActiveWindow();
        if (root == null) return;
        AccessibilityNodeInfo root1 = root.findFocus(AccessibilityNodeInfo.FOCUS_INPUT);
        AccessibilityNodeInfo root2 = root1.getChild(0).getChild(0).getChild(3).getChild(1).getChild(0).getChild(0).getChild(1).getChild(0).getChild(1).getChild(0);
        AccessibilityNodeInfo root3 = root1.getChild(0).getChild(0).getChild(3).getChild(1).getChild(0).getChild(1).getChild(1).getChild(0).getChild(1).getChild(0);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timeURL = TimeUtils.getNowString(formatter).substring(0, 10);
        if (root2 != null) {
            Bundle arguments = new Bundle();

//            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, "2022-02-10 18:30");
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, timeURL + " 18:30");
            root2.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
            Utils.sleep(1000);
        }

        if (root3 != null) {
            Bundle arguments = new Bundle();
//            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, "2022-02-10 21:00");
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, timeURL + " 21:00");
            root3.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
            Utils.sleep(2000);
        }

        root = MyApplication.getAppInstance().getAccessbilityService().getRootInActiveWindow();
        if (root == null) return;
        root1 = root.findFocus(AccessibilityNodeInfo.FOCUS_INPUT);
        AccessibilityNodeInfo root4 = root1.getChild(0).getChild(0).getChild(3).getChild(1).getChild(0).getChild(3).getChild(2).getChild(0);
        AccessibilityNodeInfo root5 = root1.getChild(0).getChild(0).getChild(3).getChild(1).getChild(0).getChild(4).getChild(1).getChild(0);

        if (root4 != null) {

            NodeInfo nodeInfo = findByText("加班类型");
            clickXY(MyApplication.getScreenWidth() / 2, nodeInfo.getRect().centerY());
            Utils.sleep(2000);

            NodeInfo nodeInfo1 = findByText("延时加班");
            NodeInfo nodeInfo2 = findByText("公息日加班");

            int x = MyApplication.getAppInstance().getScreenWidth() / 2;
            int fromY = nodeInfo1.getRect().centerY();
            int toY = nodeInfo2.getRect().centerY();
            new SwipStepBuilder().setPoints(new Point(x, fromY), new Point(x, toY)).get().execute();
            Utils.sleep(2000);
            clickTotalMatchContent("完成");
            Utils.sleep(2000);
        }

        if (root5 != null) {
            Bundle arguments = new Bundle();
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, "工作进度需要");
            root5.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
            Utils.sleep(1000);
        }

        if (clickTotalMatchContent("提交申请")) {
            Utils.sleep(1000);
            if (clickTotalMatchContent("确定")) {
                Utils.sleep(4000);
                task4 = true;
                setTodayDone(true);
                skipTask();
            }
        }
    }

    private void doShenQin1() {
        if (clickTotalMatchContent("加班")) {
            return;
        }
    }

    //加班餐申请
    private void doShenQin() {
        AccessibilityNodeInfo webNodeInfo = getWebNodeInfo();
        AccessibilityNodeInfo edit = webNodeInfo.getChild(0).getChild(0).getChild(0).getChild(3).getChild(1);
        if(null != edit){
            LogUtils.d(TAG,"FIND EDIT");
            Bundle arguments = new Bundle();
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, "工作进度需要");
            edit.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
            Utils.sleep(2000);

            if (clickTotalMatchContent("提交")) {
                Utils.sleep(2000);
                NodeInfo nodeInfo2 = findByText("领取餐补");
                if (null != nodeInfo2) {
                    clickXY(nodeInfo2.getRect().centerX(), nodeInfo2.getRect().centerY() - SizeUtils.dp2px(50));
                    Utils.sleep(2000);
                    task2 = true;
                    clickBack();
                    type = 0;
                    skipTask();
                }
            }
        }
    }

    private void doTask3() {
        if (clickTotalMatchContent("加班餐")) {
            Utils.sleep(1500);
            if (clickTotalMatchContent("已知悉，继续点餐")) {
                return;
            }
            clickXY(MyApplication.getScreenWidth() / 2, 2250);
        }

    }


    private void getCurHour() {
        Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);//时
        LogUtils.d(TAG, "mHour:" + mHour);
    }

    //打卡操作
    private void doCardPage() {
        if (clickTotalMatchContent("我知道了")) {
            Utils.sleep(2000);
        }
        NodeInfo nodeInfo = findByText("当前时间");
        clickXY(MyApplication.getScreenWidth() / 2, nodeInfo.getRect().centerY() - SizeUtils.dp2px(100));
        Utils.sleep(3000);
        if (findContent("打卡成功")) {
            clickContent("知道了");
            Utils.sleep(2000);
            clickBack();
            skipTask();
            if (type == 1) {
                task1 = true;
            } else if (type == 3) {
                task3 = true;
            }

        }
    }

    private void doJumpPage() {
        switch (type) {
            case 1:
                clickTotalMatchContent("打卡");
                break;
            case 2:
                clickTotalMatchContent("餐饮");
                break;
            case 3:
                clickTotalMatchContent("打卡");
                break;
            case 4:
                clickTotalMatchContent("考勤");
                break;
        }
    }

    private void doMainPage() {
        if (samePageCount > 2) {
            tryClickDialog();
        }
        clickXY(MyApplication.getScreenWidth() * 3 / 4 - SizeUtils.dp2px(40), MyApplication.getScreenHeight() - SizeUtils.dp2px(20));
        Utils.sleep(3000);
        return;

    }

    private void doLoginPage() {
        clickTotalMatchContent("登录");
        Utils.sleep(3000);

        if (findContent("使用该账号的密码")) {
            clickBack();
            Utils.sleep(2000);
//            clickTotalMatchContent("记住密码");
//            Utils.sleep(2000);
        }

        AccessibilityNodeInfo root = MyApplication.getAppInstance().getAccessbilityService().getRootInActiveWindow();
        if (root == null) return;
        AccessibilityNodeInfo editPassword = root.findFocus(AccessibilityNodeInfo.FOCUS_INPUT);
        if (editPassword != null) {
            Bundle arguments = new Bundle();
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, "Onlie-chcn1314");
            editPassword.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
            Utils.sleep(1000);
            clickTotalMatchContent("登录");
            Utils.sleep(3000);
        }
    }

    @Override
    protected int getMinSleepTime() {
        return 3000;
    }

    @Override
    protected int getMaxSleepTime() {
        return 3000;
    }

    @Override
    protected void getRecognitionResult() {

    }


    public boolean isCurrentScipte() {
        return getAppInfo().getPkgName().equals(Constant.PN_FENG_SHENG) ? true : false;
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
                LogUtils.d(TAG, "自动恢复到丰声极速版");
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
                CrashReport.postCatchedException(new Throwable("丰声极速版无响应"));

            }
            return false;
        }
        resumeCount = 0;
        return true;
    }

    @Override
    public void destory() {
        clickBack();
        Utils.sleep(100);
        clickBack();
        Utils.sleep(1000);
        pressHome();
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

    /**
     * 检查是在那个页面
     *
     * @return //0:首页 1:个人中心  2:阅读页  3:广告页
     */
    private int checkPageId() {
        if (findContent("工号密码登录")) {
            return 0;
        }
        if (findTotalMatchContent("全部") && findContent("@我")) {
            return 1;
        }
        if (findTotalMatchContent("智能审批") && findContent("打卡")) {
            return 2;
        }
        if (findTotalMatchContent("已进入打卡范围")) {
            return 3;
        }
        if (findTotalMatchContent("便捷扫码支付")) {
            return 4;
        }
        if (findTotalMatchContent("丰味平台") && findTotalMatchContent("加班原因 *")) {
            return 5;
        }
        if (findTotalMatchContent("正常出勤") && findTotalMatchContent("销假")) {
            return 6;
        }
        if (findTotalMatchContent("开始加班时间")) {
            return 7;
        }

        return -1;
    }


}
