package com.ch.scripts;

import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.blankj.utilcode.util.AppUtils;
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

import java.util.ArrayList;
import java.util.List;

import static android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_TAKE_SCREENSHOT;
import static com.ch.core.utils.ActionUtils.click;
import static com.ch.core.utils.ActionUtils.pressHome;

public class MeiTianZhuanDianScript extends BaseScript {

    private String TAG = this.getClass().getSimpleName();
    private Point point_ShouYe;
    private Point point_WoDe;
    private List<String> wrongTaskList = new ArrayList<>();//记录执行不了的任务
    private String taskName = "";

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
        getRecognitionResult();
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
            case 5:
                doPageId5Things();
                break;
            case 6:
                doPageId6Things();
                break;
            case 7:
                doPageId7Things();
                break;
            case 8:
                doPageId8Things();
                break;
            case -1:
                clickTotalMatchContent("收下金币");
                clickContent("奖励翻倍");
                clickTotalMatchContent("看视频领取");

                if (samePageCount > 8) {
                    scrollDown();
                }
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
            scrollUp();
            if (clickContent("填写邀请码")) {
                if (findContent("我的师傅")) {
                    SPUtils.getInstance().put("invite_success", true);
                    Utils.sleep(2000);
                    clickBack();
                    clickXY(point_ShouYe.x, point_ShouYe.y);
                    return;
                }
                return;
            }
        }

        if (clickTotalMatchContent("截图任务"));
        if (clickTotalMatchContent("继续赚"));

    }

    private void reInstallApp() {
        LogUtils.d(TAG,"卸载每天赚点App");
        uninstallPackage(Constant.PN_MEI_TIAN_ZHUAN_DIAN);
        installPackage(Constant.PN_MEI_TIAN_ZHUAN_DIAN);
//        AppUtils.uninstallApp(Constant.PN_MEI_TIAN_ZHUAN_DIAN);
        Utils.sleep(2000);
    }

    /**
     * 任务列表页面逻辑 查找小红书关注、抖音点赞、哔哩哔哩关注等任务
     */
    private void doPageId1Things() {
        LogUtils.d(TAG, "doPageId1Things");
        if (samePageCount > 5) {
            clickBack();
//            skipTask();
            clickXY(point_WoDe.x,point_WoDe.y);
            clickTotalMatchContent("看新闻赚钱");
            return;
        }
        if (clickTotalMatchContent("继续赚"));
        if (PackageUtils.checkApkExist(MyApplication.getAppInstance(), Constant.PN_HUO_SHAN)) {
            if(!wrongTaskList.contains("抖音火山版领火苗")){
                if(clickTotalMatchContent("抖音火山版领火苗")){
                    return;
                }
            }
        }
        if (PackageUtils.checkApkExist(MyApplication.getAppInstance(), Constant.PN_TAO_TE)) {
            if(!wrongTaskList.contains("淘特摇一摇")){
                if(clickTotalMatchContent("淘特摇一摇")){
                    return;
                }
            }
        }
        if (PackageUtils.checkApkExist(MyApplication.getAppInstance(), Constant.PN_XIAO_HONG_SHU)) {
            List<NodeInfo> nodeInfoList = findNodeInfosByText("小红");
            if(nodeInfoList != null){
                for(int i = 0;i<nodeInfoList.size();i++){
                    if(!wrongTaskList.contains(nodeInfoList.get(i).getText())  && nodeInfoList.get(i).getRect().bottom < MyApplication.getScreenHeight()){
                        clickXY(nodeInfoList.get(i).getRect().centerX(),nodeInfoList.get(i).getRect().bottom-20);
                        return;
                    }
                }
            }
        }
        scrollUpPx(SizeUtils.dp2px(240));
        return;

    }

    /**
     * 步数换钱
     */
    private void doPageId6Things() {
        if(findTotalMatchContent("还未到金币领取时间,请继续走路哦~")){
            clickBack();
            clickXY(point_ShouYe.x,point_ShouYe.y);
            skipTask();
            return;
        }
        clickTotalMatchContent("好的");
        clickTotalMatchContent("领取金币");
    }

    private void doPageId8Things() {
        if(samePageCount >2){
            clickXY(point_ShouYe.x,point_ShouYe.y);
        }
        clickTotalMatchContent("看新闻赚钱");
    }

    /**
     * 步数换钱
     */
    private void doPageId7Things() {
        LogUtils.d(TAG, "doPageId7Things");

        if (clickContent("继续观看"));
        if(clickContent("跳过"));

        if (samePageCount >= 7) {
            NodeInfo nodeInfo = findByText("反馈");
            if (null != nodeInfo) {
                clickXY(MyApplication.getScreenWidth() - SizeUtils.dp2px(40), nodeInfo.getRect().centerY());
            }
        }
        Utils.sleep(5000);
        refreshNodeinfo();
    }

    /**
     * 看新闻赚钱
     */
    private void doPageId5Things() {
        if(clickTotalMatchContent("1000金币")){
            clickTotalMatchContent("点击观看视频");
            return;
        }
        clickBack();
        clickTotalMatchContent("步数换钱");
    }

    /**
     * 邀请码填写页
     */
    private void doPageId4Things() {
        if (clickContent("输入好友邀请码即可成为Ta的好友")) {
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
       if(taskHasDone())return;
        int picCount = checkPicCount();
        LogUtils.d(TAG, "doPageId2Things：" + taskName);
        getTaskName();
        if(taskName.equals("抖音火山版领火苗")){
            doHuoShanHuoMiao();
            return;
        }

        if(taskName.equals("淘特摇一摇")){
            doTaoTeYaoYiYao();
            return;
        }

        if(taskName.contains("小红")){
            if(!findTotalMatchContent("打开链接") && !findTotalMatchContent("点击保存二维码")&& !findTotalMatchContent("复制口令")){
                wrongRunDeal();
                return;
            }

            if(!dealTaskType()){
                wrongRunDeal();
                return;
            }
            if (recieveTask()) {
                if(findTotalMatchContent("简单关注")){
                    if(clickTotalMatchContent("打开链接")){
                        doXHStype1(picCount);
                    }else if(clickTotalMatchContent("复制口令")){
                        doXHStype2(picCount);
                    }else if(clickTotalMatchContent("点击保存二维码")){
                        doXHStype3(picCount);
                    }
                    return;
                }

                if(findTotalMatchContent("视频点赞")){
                    if(clickTotalMatchContent("打开链接")){
                        Utils.sleep(2000);
                        doXHStype4(picCount);
                    }
//                    else if(clickTotalMatchContent("复制口令")){
//                        Utils.sleep(2000);
//                        doXHStype5(picCount);
//                    }else if(clickTotalMatchContent("点击保存二维码")){
//                        Utils.sleep(2000);
//                        doXHStype6(picCount);
//                    }
                    else {
                        wrongRunDeal();
                    }
                }
            }
        }
        if (clickContent("关闭")) return;

    }

    /**
     * 需要拍几张照片
     */
    private int checkPicCount() {
        List<NodeInfo> nodeInfoList = findPageByContent("示例图",true);
        if(null != nodeInfoList){
            LogUtils.d(TAG,"示例图个数："+nodeInfoList.size());
            return nodeInfoList.size();
        }
        return 0;
    }

    /**
     * 判断任务类型 关注还是点赞
     */
    private boolean dealTaskType() {
        if(findTotalMatchContent("简单关注")){
            return true;
        }else if(findTotalMatchContent("视频点赞")){
            return true;
        }
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
        if (findTotalMatchContent("简单赚钱") && findTotalMatchContent("奖励规则")) {
            return 5;
        }
        if (findTotalMatchContent("走路赚金币")) {
            return 6;
        }
        if (findTotalMatchContent("| 跳过") || (findTotalMatchContent("反馈") && findContent("s"))) {
            return 7;
        }
        if (findTotalMatchContent("商务合作") && findContent("反馈与帮助")) {
            return 8;
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

        point_ShouYe = new Point(MyApplication.getScreenWidth()/4-SizeUtils.dp2px(20),MyApplication.getScreenHeight()-SizeUtils.dp2px(10));
        point_WoDe = new Point(MyApplication.getScreenWidth()-SizeUtils.dp2px(20),MyApplication.getScreenHeight()-SizeUtils.dp2px(10));
        LogUtils.d(TAG,"point_ShouYe:"+point_ShouYe.toString() + " point_WoDe:"+ point_WoDe.toString() + " Height:"+MyApplication.getScreenHeight());

//        String sp_shouye = SPUtils.getInstance().getString(Constant.MEITIANZHUANDIAN_SHOUYE, "");
//        if (!TextUtils.isEmpty(sp_shouye)) {
//            point_ShouYe = new Gson().fromJson(sp_shouye, Point.class);
//        }
//        String sp_wode = SPUtils.getInstance().getString(Constant.MEITIANZHUANDIAN_WODE, "");
//        if (!TextUtils.isEmpty(sp_wode)) {
//            point_WoDe = new Gson().fromJson(sp_wode, Point.class);
//        }

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
            if (resumeCount > 40) {
                LogUtils.d(TAG, "自动恢复到每天赚点");
                startApp();
            }
            if (resumeCount > 45) {
                if (BuildConfig.DEBUG) {
                    MyApplication.getAppInstance().getAccessbilityService().performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT);
                }
                LogUtils.d(TAG, "每天赚点是不是anr了?");
                dealNoResponse2();
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

    /**
     * 领取任务
     *
     * @return
     */
    private boolean recieveTask() {
        LogUtils.d(TAG, "recieveTask()");
        if(findTotalMatchContent("提交审核")){
            return true;
        }
        if (clickContent("立即赚钱")) {
            if (clickTotalMatchContent("确认领取")) {
                return true;
            }
            if (findContent("任务已经被领取完了！")) {
                wrongRunDeal();
                return false;
            }
            if (findContent("该任务申请次数过多，不能再次申请！")) {
                wrongRunDeal();
                return false;
            }
            if (findContent("操作频繁，")) {
                wrongRunDeal();
                return false;
            }
            if (findContent("任务已过期，请换一个任务")) {
                wrongRunDeal();
                return false;
            }

            return true;
        }
        return false;
    }

    /**
     * 单张照片上传
     * @return
     */
    private boolean uploadOnePicTask(){
        if (findContent("提交审核")) {
            scrollUp();
            if (clickContent("选择文件")) {
                Utils.sleep(2000);
                AccessibilityNodeInfo accessibilityNodeInfo = findAccessibilityNodeById("com.android.documentsui:id/dir_list");
                if (null == accessibilityNodeInfo) {
                    accessibilityNodeInfo = findAccessibilityNodeById("com.google.android.documentsui:id/dir_list");
                }
                if (null != accessibilityNodeInfo) {
                    AccessibilityNodeInfo accessibilityNodeInfo3 = accessibilityNodeInfo.getChild(0);
                    if (null != accessibilityNodeInfo3) {
                        Rect rect = new Rect();
                        accessibilityNodeInfo3.getBoundsInScreen(rect);
                        clickXY(rect.centerX(),rect.centerY());

//                        accessibilityNodeInfo3.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        Utils.sleep(2000);
                        if (clickTotalMatchContent("+ 关注")) {
                            Utils.sleep(2000);
                        }
                        if (clickContent("提交审核")) {
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
     * 两张照片上传
     * @return
     */
    private boolean uploadTwoPicTask(boolean twoFirst){
        Utils.sleep(1000);
        scrollUp();
        scrollUp();
        scrollUp();
        List<NodeInfo> nodeInfoList = findNodeInfosByText("选择文件");
        if(nodeInfoList.size() == 2){
            clickXY(nodeInfoList.get(0).getRect().centerX(),nodeInfoList.get(0).getRect().centerY());
            Utils.sleep(1500);
            AccessibilityNodeInfo accessibilityNodeInfo = findAccessibilityNodeById("com.android.documentsui:id/dir_list");
            if (null == accessibilityNodeInfo) {
                accessibilityNodeInfo = findAccessibilityNodeById("com.google.android.documentsui:id/dir_list");
            }
            if (null != accessibilityNodeInfo) {
                AccessibilityNodeInfo accessibilityNodeInfo3 = accessibilityNodeInfo.getChild(twoFirst ? 1 : 0);
                if (null != accessibilityNodeInfo3) {
                    Rect rect = new Rect();
                    accessibilityNodeInfo3.getBoundsInScreen(rect);
                    clickXY(rect.centerX(),rect.centerY());

//                    accessibilityNodeInfo3.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
            }

            clickXY(nodeInfoList.get(1).getRect().centerX(),nodeInfoList.get(1).getRect().centerY());
            accessibilityNodeInfo = findAccessibilityNodeById("com.android.documentsui:id/dir_list");
            if (null == accessibilityNodeInfo) {
                accessibilityNodeInfo = findAccessibilityNodeById("com.google.android.documentsui:id/dir_list");
            }
            if (null != accessibilityNodeInfo) {
                AccessibilityNodeInfo accessibilityNodeInfo3 = accessibilityNodeInfo.getChild(twoFirst ? 0 : 1);
                if (null != accessibilityNodeInfo3) {
                    Rect rect = new Rect();
                    accessibilityNodeInfo3.getBoundsInScreen(rect);
                    clickXY(rect.centerX(),rect.centerY());

//                    accessibilityNodeInfo3.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
                if (clickTotalMatchContent("+ 关注")) {
                    Utils.sleep(2000);
                }
                if (clickContent("提交审核")) {
                    Utils.sleep(3000);
                    clickBack();
                    return true;
                }
            }
        } else {
            wrongRunDeal();
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
                    wrongRunDeal();
                }
            }
        }
    }

    /**
     * 网页中跳转App
     */
    private void requestOpenApp() {
        if (clickTotalMatchContent("打开App") || clickTotalMatchContent("APP内打开")) {
            clickTotalMatchContent("打开");
            clickTotalMatchContent("确定");
            return;
        }
        if (clickTotalMatchContent("打开")) {
        }
        if (findContent("打开方式") || findContent("方式打开")) {
            if (clickTotalMatchContent("小红书")) ;
            if (clickTotalMatchContent("抖音极速版")) ;
            if (clickTotalMatchContent("哔哩哔哩")) ;
            if (clickTotalMatchContent("总是")) ;
            if (clickTotalMatchContent("确定")) ;
            if (clickTotalMatchContent("始终")) ;
        }
    }

    /**
     * 处理不了异常任务处理
     */
    private void wrongRunDeal(){
        LogUtils.d(TAG,"wrongRunDeal()");
        if(!isTargetPkg()){
            startApp();
        }
        wrongTaskList.add(taskName);
        clickBack();
    }

    /**
     * 淘宝浏览产品任务
     */
//    private void doTask2() {
//
//        if (clickTotalMatchContent("复制口令")) {
//            Utils.sleep(2000);
//            openTaoBao();
//            Utils.sleep(5000);
//            if (clickContent("查看详情")) {
//                Utils.sleep(3000);
//                shootAndBack();
//                return;
//            }
//
//        }
//    }

    /**
     * 淘宝农场助力
     */
//    private void doTask4() {
//        if (clickTotalMatchContent("复制口令")) {
//            Utils.sleep(1000);
//            openTaoBao();
//            Utils.sleep(5000);
//            if (clickContent("查看详情")) {
//                Utils.sleep(2000);
//                if (clickContent("为他助力")) {
//                    shootAndBack();
//                    return;
//                }
//            }
//        }
//    }

    /**
     * 小红书点赞
     */
//    private void doTask5() {
//        if (clickTotalMatchContent("打开链接")) {
//            Utils.sleep(2000);
//            requestOpenApp();
//            xiaoHongShuLike();
//            return;
//        }
//
//    }

    /**
     * 抖音点赞
     */
//    private void doTask6() {
//        if (clickTotalMatchContent("打开链接")) {
//            Utils.sleep(5000);
//            requestOpenApp();
//            if (clickContent("未选中，")) {
//                shootAndBack();
//                return;
//            }
//
//            if (findContent("已选中，")) {
//                shootAndBack();
//                return;
//            }
//            return;
//        } else {
//            wrongRunDeal();
//        }
//    }

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
                    Rect rect = new Rect();
                    accessibilityNodeInfo4.getBoundsInScreen(rect);
                    clickXY(rect.centerX(),rect.centerY());

//                    accessibilityNodeInfo4.performAction(AccessibilityNodeInfo.ACTION_CLICK);
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

                Rect rect = new Rect();
                accessibilityNodeInfo6.getBoundsInScreen(rect);
                clickXY(rect.centerX(),rect.centerY());

//                accessibilityNodeInfo6.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                Utils.sleep(2000);

                if (clickContent("帮好友助力")) {
                    Utils.sleep(2000);
                    MyApplication.getAppInstance().getAccessbilityService().performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT);
                    Utils.sleep(2000);
                    clickBack();
//                    screemSuccess = true;
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
//    private void doTask8() {
//        if (clickTotalMatchContent("打开链接")) {
//            Utils.sleep(5000);
//            requestOpenApp();
//
//            if (!findTotalMatchContent("已关注")) {
//                if (clickId("follow")) {
//                    shootAndBack();
//                    return;
//                }
//            } else {
//                shootAndBack();
//                return;
//            }
//            return;
//        }
//    }

    /**
     * 打开淘宝
     */
    private void openTaoBao() {
        PackageUtils.startApp("com.taobao.taobao");
    }
    /**
     * 打开淘特
     */
    private void openTaoTe() {
        PackageUtils.startApp(Constant.PN_TAO_TE);
        if(clickContent("跳过"));
    }

    /**
     * 小红书点赞
     */
//    private void xiaoHongShuLike() {
//        requestOpenApp();
//        NodeInfo nodeInfo1 = findByText("说点什么");
//        if (null != nodeInfo1) {
//            ActionUtils.zuohua();
//            Utils.sleep(2000);
//            clickXY(MyApplication.getScreenWidth() / 2, nodeInfo1.getRect().centerY());
//            shootAndBack();
//            return;
//        }
//        Utils.sleep(1000 * 20);
//        NodeInfo nodeInfo = findByText("发弹幕");
//        clickXY(MyApplication.getScreenWidth() / 2, nodeInfo.getRect().centerY());
//        shootAndBack();
//        return;
//    }

    /**
     * 截屏加返回逻辑
     */
//    boolean screemSuccess = false;

    private void shootAndBack() {
        LogUtils.d(TAG,"shootAndBack()");
        Utils.sleep(2000);
        MyApplication.getAppInstance().getAccessbilityService().performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT);
        Utils.sleep(4000);
        startApp();
    }

    /**
     * 获取任务名称
     * @return
     */
    private String getTaskName(){
        LogUtils.d(TAG,"getTaskName()");
        AccessibilityNodeInfo accessibilityNodeInfo = getWebNodeInfo();
        try {
            if (null != accessibilityNodeInfo) {
                AccessibilityNodeInfo taskNameNode = accessibilityNodeInfo.getChild(0).getChild(1).getChild(0).getChild(0).getChild(1);
//                AccessibilityNodeInfo taskNameNode = accessibilityNodeInfo.getChild(0).getChild(2).getChild(0).getChild(0).getChild(1);
                taskName = taskNameNode.getText().toString();
                LogUtils.d(TAG,"taskName:"+taskName);
                return taskName;
            }
        }catch (Exception e){
            AccessibilityNodeInfo taskNameNode = accessibilityNodeInfo.getChild(0).getChild(1).getChild(0).getChild(0).getChild(1);
            taskName = taskNameNode.getText().toString();
            LogUtils.d(TAG,"taskName:"+taskName);
            return taskName;
        }
        return "";
    }

    /**
     * 标识进任务的时候已经结束
     * @return
     */
    private boolean taskHasDone(){
        if (findContent("任务太火爆，已经结束")) {
            doubleClickBack();
            return true;
        }
        if (findContent("开启悬浮助手")) {
            if (clickContent("不再提醒")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 淘特摇一摇
     */
    private void doTaoTeYaoYiYao() {
        if(taskHasDone())return;
        if(recieveTask()){
            Utils.sleep(2000);
            if(clickTotalMatchContent("复制口令")){
                Utils.sleep(1000);
                openTaoTe();
                refreshNodeinfo();
                if(clickTotalMatchContent("帮好友助力")){
                    MyApplication.getAppInstance().getAccessbilityService().performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT);
                    Utils.sleep(4000);
                    startApp();
                    if(uploadOnePicTask()){
                        wrongTaskList.add(taskName);
                    }else {
                        wrongRunDeal();
                    }

                }else {
                    wrongRunDeal();
                }
            }else {
                wrongRunDeal();
            }
        }
    }

    /**
     * 火山火苗
     */
    private void doHuoShanHuoMiao() {
        if(taskHasDone())return;
        if(recieveTask()){
            Utils.sleep(2000);
            if(clickTotalMatchContent("复制口令")){
                Utils.sleep(1000);
                PackageUtils.startApp(Constant.PN_HUO_SHAN);
                if(clickContent("跳过"));
                Utils.sleep(5000);
                refreshNodeinfo();
                if(clickTotalMatchContent("开启红包")){
                    shootAndBack();
                    if(uploadOnePicTask()){
//                        wrongTaskList.add(taskName);
                    }else {
                        wrongRunDeal();
                    }

                }else {
                    wrongRunDeal();
                }
            }else {
                wrongRunDeal();
            }
        }
    }

    /**
     * 截图
     */
    private void takeShoot(){
        Utils.sleep(2000);
        MyApplication.getAppInstance().getAccessbilityService().performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT);
        Utils.sleep(4000);
        if(findContent("把截屏分享到")){
            clickBack();
            Utils.sleep(2000);
        }
    }


    /**
     * 小红书关注1-打开链接
     */
    private void doXHStype1(int picCount){
        LogUtils.d(TAG,"doXHStype1:"+picCount);
        Utils.sleep(2000);
        refreshNodeinfo();
        if(clickContent("跳过"))
        requestOpenApp();

        if(clickId("nickNameTV"));
        if(clickId("matrixNickNameView"));


//        if(findTotalMatchContent("说点什么...")){
//        clickId("nickNameTV");
//        }
//        if(findTotalMatchContent("发弹幕")){
//        clickId("matrixNickNameView");
//        }

        refreshNodeinfo();
        if(picCount == 1){
            if (!findTotalMatchContent("发消息")) {
                NodeInfo nodeInfo = findByText("获赞与收藏");
                if (null != nodeInfo) {
                    clickXY(MyApplication.getScreenWidth() - SizeUtils.dp2px(100), nodeInfo.getRect().centerY() - SizeUtils.dp2px(15));
                    shootAndBack();
                }else {
                    wrongRunDeal();
                }
            }else {
                shootAndBack();
            }
            if (findContent("微信扫一扫")) {
                clickXY(500, 500);
                Utils.sleep(2000);
            }
            if(!uploadOnePicTask()){
                wrongRunDeal();
            }
            return;
        }else if(picCount == 2) {
            if (!findTotalMatchContent("发消息")) {
                NodeInfo nodeInfo = findByText("获赞与收藏");
                if (null != nodeInfo) {
                    clickXY(MyApplication.getScreenWidth() - SizeUtils.dp2px(100), nodeInfo.getRect().centerY() - SizeUtils.dp2px(15));
                }
            }
            takeShoot();
            scrollUpSlow();
            if (!clickTotalMatchContent("置顶")) {
                clickXY(SizeUtils.dp2px(100), MyApplication.getScreenHeight() - SizeUtils.dp2px(100));
            }
            shootAndBack();
            if (findContent("微信扫一扫")) {
                clickXY(500, 500);
                Utils.sleep(2000);
            }
            uploadTwoPicTask(false);
            return;
        }else {
            wrongRunDeal();
        }

        return;
    }

    /**
     * 小红书关注2-复制口令
     */
    private void doXHStype2(int picCount){
        LogUtils.d(TAG,"doXHStype2:"+picCount);
        PackageUtils.startApp(Constant.PN_XIAO_HONG_SHU);
        if(clickContent("跳过"));
        Utils.sleep(2000);
        refreshNodeinfo();
        if(clickTotalMatchContent("立即查看")){
            Utils.sleep(2000);
            if(picCount > 2 || picCount == 0){
                wrongRunDeal();
                return;
            }
            if(picCount == 2){
                takeShoot();
            }
            if(clickId("nickNameTV")){
                Utils.sleep(2000);
                if (!findTotalMatchContent("发消息")) {
                    NodeInfo nodeInfo = findByText("获赞与收藏");
                    if (null != nodeInfo) {
                        clickXY(MyApplication.getScreenWidth() - SizeUtils.dp2px(100), nodeInfo.getRect().centerY() - SizeUtils.dp2px(15));
                    }
                }
                shootAndBack();

            }else if(clickId("matrixNickNameView")){
                Utils.sleep(2000);
                if (!findTotalMatchContent("发消息")) {
                    NodeInfo nodeInfo = findByText("获赞与收藏");
                    if (null != nodeInfo) {
                        clickXY(MyApplication.getScreenWidth() - SizeUtils.dp2px(100), nodeInfo.getRect().centerY() - SizeUtils.dp2px(15));
                    }
                }
                shootAndBack();
            }else {
                wrongRunDeal();
            }
            if(picCount == 2){
                uploadTwoPicTask(true);
            }else if(picCount == 1){
                uploadOnePicTask();
            }
        }else {
            wrongRunDeal();
        }
    }

    /**
     * 小红书关注3-长按二维码
     */
    private void doXHStype3(int picCount){
        LogUtils.d(TAG,"doXHStype3:"+picCount);
        NodeInfo nodeInfo = findByText("点击保存二维码");
        longPressXY(MyApplication.getScreenWidth() / 2, nodeInfo.getRect().centerY() - SizeUtils.dp2px(80));
        if (clickTotalMatchContent("识别二维码")) {
            if (findTotalMatchContent("识别二维码")) {
                clickXY(500, 500);
                wrongRunDeal();
                return;
            }
            doXHStype1(picCount);
        }
    }

    /**
     * 小红书点赞4-打开链接
     */
    private void doXHStype4(int picCount){
        LogUtils.d(TAG,"doXHStype4:"+picCount);
        requestOpenApp();
        NodeInfo nodeInfo1 = findByText("说点什么");
        if (null != nodeInfo1) {
            ActionUtils.zuohua();
            clickXY(MyApplication.getScreenWidth() / 2, nodeInfo1.getRect().centerY());
            shootAndBack();
            return;
        }
        Utils.sleep(1000 * 10);
        NodeInfo nodeInfo = findByText("发弹幕");
        clickXY(MyApplication.getScreenWidth() / 2, nodeInfo.getRect().centerY());
        shootAndBack();
        if(picCount == 1){
            if(!uploadOnePicTask()){
                wrongRunDeal();
            }
        }else {
            wrongRunDeal();
        }
        return;
    }

    /**
     * 小红书点赞5-复制口令
     */
    private void doXHStype5(int picCount){

    }

    /**
     * 小红书点赞6-长按二维码
     */
    private void doXHStype6(int picCount){

    }

}

