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
import com.ch.common.PackageUtils;
import com.ch.common.RecommendCodeManage;
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

    private final int EVENT_XHS_FOLLOW = 10;//小红书关注
    private final int EVENT_XHS_THUMB = 11;//小红书点赞

    private final int EVENT_DY_FOLLOW = 20;//抖音关注
    private final int EVENT_DY_THUMB = 21;//抖音点赞

    private final int EVENT_KS_FOLLOW = 30;//快手关注
    private final int EVENT_KS_THUMB = 31;//快手点赞

    private final int EVENT_BLBL_FOLLOW = 40;//哔哩哔哩关注
    private final int EVENT_BLBL_THUMB = 41;//哔哩哔哩点赞

    private final int EVENT_TB_SCAN = 50;//淘宝产品浏览
    private final int EVENT_TB_HELP = 51;//淘宝助力
    private final int EVENT_TB_FARM_HELP = 52;//淘宝农场助力

    private final int EVENT_TT_HELP = 90;//淘特助力


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
    }

    @Override
    protected void executeScript() {
        LogUtils.d(TAG, "executeScript()");
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

        if (samePageCount > 10) {
            doSamePageDeal();
            return;
        }

        switch (pageId) {
            case 0:
                doPageId0Things();
                break;
            case 1:
                doPageId1Things();
                break;
            case 2:
                doPageId2Things();
                break;
            case 3:
//                doPageId3Things();
                break;
            case 4:
                doPageId4Things();
                break;
            case -1:
                if (samePageCount > 8) {
                    scrollDown();
                }
                Utils.sleep(1500);
                clickBack();
                break;
        }
    }

    /**
     * 首页页面逻辑 判断坐标 判断邀请 跳转截图任务
     */
    private void doPageId0Things() {
        LogUtils.d(TAG, "doPageId0Things");
        if (point_ShouYe == null) {
            getRecognitionResult();
            if (point_ShouYe == null) {
                EventBus.getDefault().post(new ScreenShootEvet(Constant.PN_MEI_TIAN_ZHUAN_DIAN, Constant.PAGE_MAIN));
            }
            return;
        }

        boolean invite = SPUtils.getInstance().getBoolean("invite_success");
        if (!invite) {
            clickXY(point_WoDe.x, point_WoDe.y);
            Utils.sleep(1000);
            scrollUp();
            Utils.sleep(2000);
            if (clickContent("填写邀请码")) {
                Utils.sleep(2000);
                if (findContent("我的师傅")) {
                    SPUtils.getInstance().put("invite_success", true);
                    Utils.sleep(2000);
                    clickBack();
                    Utils.sleep(2000);
                    clickXY(point_ShouYe.x, point_ShouYe.y);
                    return;
                }
                return;
            }
        }

        if (clickContent("截图任务")) return;

    }

    /**
     * 任务列表页面逻辑 查找小红书关注、抖音点赞、哔哩哔哩关注等任务
     */
    int curTaskType = -1;

    private void doPageId1Things() {
        LogUtils.d(TAG, "doPageId1Things");
        if (samePageCount == 3) {
            if (clickTotalMatchContent("关注")) return;
        }
        if (samePageCount > 4) {
            clickBack();
            return;
        }

        if (PackageUtils.checkApkExist(MyApplication.getAppInstance(), Constant.PN_XIAO_HONG_SHU)) {
            if (clickContent("小红书关注") || clickContent("小红书简单关注") || clickContent("小红书点关注") || clickContent("小红书粉丝")) {
                curTaskType = EVENT_XHS_FOLLOW;
                return;
            }
            if (clickContent("简单关注拒绝秒点")) {
                curTaskType = EVENT_XHS_FOLLOW;
                return;
            }
            if (clickContent("小红书点赞")) {
                curTaskType = EVENT_XHS_THUMB;
                return;
            }
        }

        if (PackageUtils.checkApkExist(MyApplication.getAppInstance(), Constant.PN_DOU_YIN)) {
            if (clickContent("抖音点赞")) {
                curTaskType = EVENT_DY_THUMB;
                return;
            }
        }

//        if(PackageUtils.checkApkExist(MyApplication.getAppInstance(),Constant.PN_TAO_TE)){
//            if(clickTotalMatchContent("+0.68元") ){
//                curTaskType = EVENT_TT_HELP;
//                return;
//            }
//        }

        if (PackageUtils.checkApkExist(MyApplication.getAppInstance(), Constant.PN_BI_LI_BI_LI)) {
            if (clickContent("哔哩哔哩关注")) {
                curTaskType = EVENT_BLBL_FOLLOW;
                return;
            }
        }

        if (PackageUtils.checkApkExist(MyApplication.getAppInstance(), Constant.PN_TAO_BAO)) {
            if (clickContent("淘宝产品浏览") || clickContent("淘宝浏览产品")) {
                curTaskType = EVENT_TB_SCAN;
                return;
            }
            if (clickContent("淘宝农场助力")) {
                curTaskType = EVENT_TB_FARM_HELP;
                return;
            }
        }

        scrollUpSlow();
        return;

    }

    /**
     * 邀请码填写页
     */
    private void doPageId4Things() {
        if (clickContent("输入好友邀请码即可成为Ta的好友")) {
            Utils.sleep(2000);
            AccessibilityNodeInfo accessibilityNodeInfo = findAccessibilityNodeById("com.yongloveru.hjw:id/findt_edit");
            if (null != accessibilityNodeInfo) {
                Bundle arguments = new Bundle();
                arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, RecommendCodeManage.getSingleton().getRecommendBean().getCode_meitianzhuandian());
                accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
                accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
                Utils.sleep(1000);
                if (clickContent("确定")) {
                    SPUtils.getInstance().put("invite_success", true);
                    Utils.sleep(2000);
                    CrashReport.postCatchedException(new Throwable("每天赚点自动填写邀请码成功"));
                    return;
                }

            }
        }

    }


    /**
     * 做任务页面逻辑
     */
    private void doPageId2Things() {
        LogUtils.d(TAG, "doPageId2Things：" + curTaskType);
        if (findContent("任务太火爆，已经结束")) {
            clickBack();
            Utils.sleep(1000);
            clickBack();
            return;
        }
        if (findContent("开启悬浮助手")) {
            if (clickContent("不再提醒")) {
                return;
            }
        }

        if (screemSuccess) {
            screemSuccess = false;
            if (findContent("微信扫一扫")) {
                clickXY(500, 500);
                Utils.sleep(2000);
            }
            if (uploadTask()) return;

            return;
        }

        if (recieveTask()) return;

        switch (curTaskType) {
            case EVENT_XHS_FOLLOW:
                doTask0();
                break;
            case EVENT_TB_SCAN:
                doTask2();
                break;
            case EVENT_TB_FARM_HELP:
                doTask4();
                break;
            case EVENT_XHS_THUMB:
                doTask5();
                break;
            case EVENT_DY_THUMB:
                doTask6();
                break;
            case EVENT_TT_HELP:
                doTask7();
                break;
            case EVENT_BLBL_FOLLOW:
                doTask8();
                break;
        }

        if (clickContent("关闭")) return;

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
        if (findTotalMatchContent("做任务赚钱")) {
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
        String sp_shouye = SPUtils.getInstance().getString(Constant.MEITIANZHUANDIAN_SHOUYE, "");
        if (!TextUtils.isEmpty(sp_shouye)) {
            point_ShouYe = new Gson().fromJson(sp_shouye, Point.class);
        }
        String sp_wode = SPUtils.getInstance().getString(Constant.MEITIANZHUANDIAN_WODE, "");
        if (!TextUtils.isEmpty(sp_wode)) {
            point_WoDe = new Gson().fromJson(sp_wode, Point.class);
        }

    }


    public boolean isCurrentScipte() {
        return getAppInfo().getPkgName().equals(Constant.PN_MEI_TIAN_ZHUAN_DIAN) ? true : false;
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
            if (resumeCount > 36) {
                LogUtils.d(TAG, "自动恢复到每天赚点");
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
     * 领取任务
     *
     * @return
     */
    private boolean recieveTask() {
        if (clickContent("立即赚钱")) {
            Utils.sleep(2000);
            if (findContent("任务已经被领取完了！")) {
                clickBack();
                Utils.sleep(2000);
                clickBack();
                return true;
            }
            if (findContent("该任务申请次数过多，不能再次申请！")) {
                clickBack();
                Utils.sleep(2000);
                clickBack();
                return true;
            }

            if (clickTotalMatchContent("确认领取")) {
                return true;
            }
            return true;
        }
        return false;
    }

    /**
     * 上传截图提交任务
     */
    private boolean uploadTask() {
        if (findContent("提交审核")) {
            scrollUp();
            Utils.sleep(2000);
            if (clickContent("选择文件")) {
                Utils.sleep(2000);
                AccessibilityNodeInfo accessibilityNodeInfo = findAccessibilityNodeById("com.android.documentsui:id/dir_list");
                if (null == accessibilityNodeInfo) {
                    accessibilityNodeInfo = findAccessibilityNodeById("com.google.android.documentsui:id/dir_list");
                }
                if (null != accessibilityNodeInfo) {
                    AccessibilityNodeInfo accessibilityNodeInfo3 = accessibilityNodeInfo.getChild(0);
                    if (null != accessibilityNodeInfo3) {
                        accessibilityNodeInfo3.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        Utils.sleep(2000);
                        if (clickTotalMatchContent("+ 关注")) {
                            Utils.sleep(2000);
                        }
                        if (clickContent("提交审核")) {
                            Utils.sleep(5000);
                            clickBack();
                            curTaskType = -1;
                            return true;
                        }
                    }
                }

            }
        }
        return false;
    }

    /**
     * 取消任务
     */
    private void cancelTask() {
        if (clickTotalMatchContent("取消")) {
            Utils.sleep(2000);
            if (clickContent("其他原因。")) {
                Utils.sleep(2000);
                if (clickContent("狠心取消")) {
                    Utils.sleep(2000);
                    clickBack();
                }
            }
        }
    }

    /**
     * 网页中跳转App
     */
    private void requestOpenApp() {
        if (clickTotalMatchContent("打开App") || clickTotalMatchContent("APP内打开")) {
            Utils.sleep(2000);
            clickTotalMatchContent("打开");
            Utils.sleep(2000);
            clickTotalMatchContent("确定");
            Utils.sleep(2000);
            return;
        }
        if (clickTotalMatchContent("打开")) {
            Utils.sleep(2000);
        }
        if (findTotalMatchContent("打开方式")) {
            if (clickTotalMatchContent("小红书")) Utils.sleep(2000);
            if (clickTotalMatchContent("抖音极速版")) Utils.sleep(2000);
            if (clickTotalMatchContent("哔哩哔哩")) Utils.sleep(2000);
            if (clickTotalMatchContent("总是")) Utils.sleep(2000);
            if (clickTotalMatchContent("确定")) Utils.sleep(2000);
        }
    }

    /**
     * 跳转链接小红书关注
     */
    private void doTask0() {
        if (clickTotalMatchContent("打开链接")) {
            Utils.sleep(5000);
            requestOpenApp();
            openXiaoHongShu();
            return;
        } else if (findTotalMatchContent("点击保存二维码")) {
            doTask1();
        } else {
            cancelTask();
        }
    }

    /**
     * 扫码跳转小红书关注
     */
    private void doTask1() {
        NodeInfo nodeInfo = findByText("点击保存二维码");
        longPressXY(MyApplication.getScreenWidth() / 2, nodeInfo.getRect().centerY() - SizeUtils.dp2px(80));
        Utils.sleep(3000);
        if (clickTotalMatchContent("识别二维码")) {
            Utils.sleep(5000);
            if (findTotalMatchContent("识别二维码")) {
                clickXY(500, 500);
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

        if (clickTotalMatchContent("复制口令")) {
            Utils.sleep(2000);
            openTaoBao();
            Utils.sleep(5000);
            if (clickContent("查看详情")) {
                Utils.sleep(3000);
                shootAndBack();
                return;
            }

        }
    }

    /**
     * 淘宝喵糖助力
     */
//    private void doTask3() {
//        if(clickTotalMatchContent("复制口令")){
//            Utils.sleep(1000);
//            openTaoBao();
//            Utils.sleep(5000);
//            if(clickContent("查看详情")){
//                Utils.sleep(2000);
//                if(clickContent("立即助力")){
//                    Utils.sleep(2000);
//                    MyApplication.getAppInstance().getAccessbilityService().performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT);
//                    screemSuccess = true;
//                    Utils.sleep(3000);
//                    clickBack();
//                    Utils.sleep(1000);
//                    clickBack();
//                    startApp();
//                }
//            }
//        }
//    }

    /**
     * 淘宝农场助力
     */
    private void doTask4() {
        if (clickTotalMatchContent("复制口令")) {
            Utils.sleep(1000);
            openTaoBao();
            Utils.sleep(5000);
            if (clickContent("查看详情")) {
                Utils.sleep(2000);
                if (clickContent("为他助力")) {
                    shootAndBack();
                    return;
                }
            }
        }
    }

    /**
     * 小红书点赞
     */
    private void doTask5() {
        if (clickTotalMatchContent("打开链接")) {
            Utils.sleep(2000);
            requestOpenApp();
            xiaoHongShuLike();
            return;
        }

    }

    /**
     * 抖音点赞
     */
    private void doTask6() {
        if (clickTotalMatchContent("打开链接")) {
            Utils.sleep(5000);
            requestOpenApp();
            if (clickContent("未选中，")) {
                shootAndBack();
                return;
            }

            if (findContent("已选中，")) {
                shootAndBack();
                return;
            }
            return;
        } else {
            cancelTask();
        }
    }

    /**
     * 淘特每日拆红包
     */
    private void doTask7() {
        if (clickTotalMatchContent("复制到app打开")) {
            Utils.sleep(5000);
            requestOpenApp();

            AccessibilityNodeInfo accessibilityNodeInfo = findAccessibilityNodeById("com.taobao.litetao:id/search_bar_container");
            if (null != accessibilityNodeInfo) {
                AccessibilityNodeInfo accessibilityNodeInfo1 = accessibilityNodeInfo.getChild(0);
                AccessibilityNodeInfo accessibilityNodeInfo2 = accessibilityNodeInfo1.getChild(0);
                AccessibilityNodeInfo accessibilityNodeInfo3 = accessibilityNodeInfo2.getChild(2);
                AccessibilityNodeInfo accessibilityNodeInfo4 = accessibilityNodeInfo3.getChild(0);
                if (null != accessibilityNodeInfo4) {
                    accessibilityNodeInfo4.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    Utils.sleep(5000);
                }

            }

//            NodeInfo nodeInfo = findByText("猜你要比价");
            NodeInfo nodeInfo = findByText("扫描二维码或者拍照");
            if (null != nodeInfo) {
                Point point = new Point(SizeUtils.dp2px(60), nodeInfo.getRect().centerY() + SizeUtils.dp2px(80));
//                Point point = new Point(nodeInfo.getRect().centerX(),nodeInfo.getRect().centerY()+SizeUtils.dp2px(30));
                clickXY(point.x, point.y);
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
            if (null != accessibilityNodeInfo5) {
                AccessibilityNodeInfo accessibilityNodeInfo6 = accessibilityNodeInfo5.getChild(0);
                accessibilityNodeInfo6.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                Utils.sleep(2000);

                if (clickContent("帮好友助力")) {
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

    /**
     * 哔哩哔哩关注
     */
    private void doTask8() {
        if (clickTotalMatchContent("打开链接")) {
            Utils.sleep(5000);
            requestOpenApp();

            if (!findTotalMatchContent("已关注")) {
                if (clickId("follow")) {
                    shootAndBack();
                    return;
                }
            } else {
                shootAndBack();
                return;
            }
            return;
        }
    }

    /**
     * 打开淘宝
     */
    private void openTaoBao() {
        PackageUtils.startApp("com.taobao.taobao");
    }

    /**
     * 小红书关注
     */
    private void openXiaoHongShu() {
        if (findTotalMatchContent("发消息")) {
            shootAndBack();
            return;
        }

        NodeInfo nodeInfo = findByText("获赞与收藏");
        if (null != nodeInfo) {
            clickXY(MyApplication.getScreenWidth() - SizeUtils.dp2px(100), nodeInfo.getRect().centerY() - SizeUtils.dp2px(15));
            shootAndBack();
            return;
        }
    }

    /**
     * 小红书点赞
     */
    private void xiaoHongShuLike() {
        requestOpenApp();
        NodeInfo nodeInfo1 = findByText("说点什么");
        if (null != nodeInfo1) {
            ActionUtils.zuohua();
            Utils.sleep(2000);
            clickXY(MyApplication.getScreenWidth() / 2, nodeInfo1.getRect().centerY());
            shootAndBack();
            return;
        }
        Utils.sleep(1000 * 20);
        NodeInfo nodeInfo = findByText("发弹幕");
        clickXY(MyApplication.getScreenWidth() / 2, nodeInfo.getRect().centerY());
        shootAndBack();
        return;
    }

    /**
     * 截屏加返回逻辑
     */
    boolean screemSuccess = false;

    private void shootAndBack() {
        Utils.sleep(2000);
        MyApplication.getAppInstance().getAccessbilityService().performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT);
        screemSuccess = true;
        Utils.sleep(4000);
        clickBack();
        Utils.sleep(1000);
        clickBack();
        Utils.sleep(1000);
        startApp();
    }

}

