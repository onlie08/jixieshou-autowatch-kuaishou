package com.ch.scripts;

import static com.ch.core.utils.Constant.PN_MEI_TIAN_ZHUAN_DIAN;

import android.graphics.Point;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.ch.application.MyApplication;
import com.ch.common.PackageUtils;
import com.ch.core.executor.builder.SwipStepBuilder;
import com.ch.core.search.FindById;
import com.ch.core.search.FindByText;
import com.ch.core.search.node.NodeInfo;
import com.ch.core.utils.ActionUtils;
import com.ch.core.utils.BaseUtil;
import com.ch.core.utils.Logger;
import com.ch.core.utils.Utils;
import com.ch.jixieshou.BuildConfig;
import com.ch.model.AppInfo;

import java.util.List;
import java.util.Random;

public abstract class BaseScript implements IScript {
    private String BASETAG = this.getClass().getSimpleName();

    private AppInfo appInfo;
    private long startTime;
    public boolean stop = false;
    public boolean todayDone = false;
    public int samePageCount = 0; //同一个页面停留次数

    public boolean isTodayDone() {
        return todayDone;
    }

    public void setTodayDone(boolean todayDone) {
        LogUtils.d(BASETAG, "setTodayDone" + todayDone);
        this.todayDone = todayDone;
        this.appInfo.setTodayDone(todayDone);
    }

    public BaseScript(AppInfo appInfo) {
        this.appInfo = appInfo;
    }

    protected long getTimeout() {
        return appInfo.getPeriod() * 60 * 60 * 1000;
    }

    @Override
    public void execute() {
        stop = false;
        startApp();
        resetStartTime();

        // 总时间
        while ((System.currentTimeMillis() - startTime < getTimeout()) && !stop) {
            try {
                if (isPause()) {
                    if(clickTotalMatchContent("完成"))
                    Utils.sleep(2000);
                    continue;
                }
                if (!isTargetPkg()) {
                    continue;
                }
                if (!NetworkUtils.isAvailable()) {
                    continue;
                }
                if (ScreenUtils.isScreenLock()) {
                    continue;
                }
                executeScript();
            } catch (Exception e) {
                Logger.e("执行异常，脚本: " + appInfo.getTaskName(), e);
            } finally {
//                if (findContent("启动应用")) {
//                    clickId("button1");
//                }
                if(isPause()){
                    findAllPageByContent("测试",true);
                    MyApplication.getAppInstance().getAccessbilityService().setRoot();
//                    if (findId("test")) ;
//                    logcatAccessibilityNode();
                }
                int t = getRandomSleepTime(getMinSleepTime(), getMaxSleepTime());
                Logger.i("休眠：" + t);
                Utils.sleep(t);
            }
        }
    }
    private String TAG1 = "logcatAccess";
    public void logcatAccessibilityNode() {
        AccessibilityNodeInfo root = MyApplication.getAppInstance().getAccessbilityService().getRootInActiveWindow();
        if (root == null) return;
        if(null != root){
            getChildRoot(root);
        }
    }

    public AccessibilityNodeInfo getWebViewRoot() {
        findWebViewNode();
        return webViewRoot;
    }

    public void setWebViewRoot(AccessibilityNodeInfo webViewRoot) {
        this.webViewRoot = webViewRoot;
    }

    AccessibilityNodeInfo webViewRoot;
    public void findWebViewNode() {
        AccessibilityNodeInfo root = MyApplication.getAppInstance().getAccessbilityService().getRootInActiveWindow();
        if (root == null) return;
        getWebViewRoot(root);
    }

    private void getWebViewRoot(AccessibilityNodeInfo root) {
        if(null == root)return;
        if(root.getClassName().toString().contains("WebView")){
            webViewRoot = root;
            return;
        }
        int childCount = root.getChildCount();
        Log.d(TAG1,"childCount:"+childCount + " className:"+root.getClassName().toString());
//        Log.d(TAG1,"root.toString():"+root.toString());

        for(int i= 0;i<childCount;i++) {
            AccessibilityNodeInfo access1 = root.getChild(i);
            getWebViewRoot(access1);
        }
    }

    private void getChildRoot(AccessibilityNodeInfo root) {
        if(null == root)return;
        int childCount = root.getChildCount();
//        Log.d(TAG1,"ChildCount:"+childCount + " ClassName:"+root.getClassName() +" ViewIdResourceName:"+root.getViewIdResourceName()+" root.toString():"+root.toString());
//        Log.d(TAG1,"root.toString():"+root.toString());
//        Log.d(TAG1,"childCount:"+childCount + " className:"+root.getClassName().toString());
        for(int i= 0;i<childCount;i++) {
//            Log.d(TAG1,"childCount:"+childCount + " className:"+root.getChild(i).getClassName().toString() + " getChildCount:"+root.getChild(i).getChildCount());
        }
        if(childCount == 0)return;
        for(int i= 0;i<childCount;i++) {
            AccessibilityNodeInfo access1 = root.getChild(i);
            getChildRoot(access1);
        }

    }

    /**
     * 本次任务做完了，暂停并且跳到下一个app
     */
    public void skipTask() {
        stop = true;
        TaskExecutor.getInstance().setAllTime(0);
    }

    private boolean isPause() {
        return TaskExecutor.getInstance().isForcePause() ||
                TaskExecutor.getInstance().isPause();
    }

    @Override
    public AppInfo getAppInfo() {
        return appInfo;
    }

    @Override
    public void startApp() {
        LogUtils.d(BASETAG,"startApp()");
        PackageUtils.startApp(getAppInfo().getPkgName());
    }

    @Override
    public void resetStartTime() {
        this.startTime = System.currentTimeMillis();
    }

    /**
     * 获取一个随机的休眠时间
     *
     * @param min
     * @param max
     * @return
     */
    private int getRandomSleepTime(int min, int max) {
        Random random = new Random();
        return random.nextInt(max) % (max - min + 1) + min;
    }

    /**
     * 通过id来查找控件
     *
     * @param id
     * @return
     */
    protected NodeInfo findById(String id) {
        return FindById.find(id);
    }

    /**
     * 找到webview的根节点
     * @return
     */
    protected AccessibilityNodeInfo getWebNodeInfo() {
        return FindById.getWebNodeInfo();
    }

    //
    protected AccessibilityNodeInfo findAccessibilityNodeById(String id) {
        return FindById.findAccessibilityNode(id);
    }

    protected AccessibilityNodeInfo findAccessibilityNodeByText(String text) {
        return FindById.findAccessibilityNodeByText(text);
    }

    //
    protected List<AccessibilityNodeInfo> findAccessibilityNodeListById(String id) {
        return FindById.findAccessibilityNodeList(id);
    }

    /**
     * 设置edittext控件文字
     *
     * @param
     * @return
     */
    protected boolean setViewText(String viewIds, String text) {
        return FindById.setViewText(viewIds, text);
    }

    protected NodeInfo findByText(String text) {
        return FindByText.find(text);
    }

//    protected NodeInfo findAllPageByContent(String text,boolean totalMatch) {
//        return FindByText.findAllPageByContent(text,totalMatch);
//    }

    protected NodeInfo findTotalMatchByText(String text) {
        return FindByText.findTotalMatch(text);
    }

    protected List<NodeInfo> findAllTotalMatchByText(String text) {
        return FindByText.findAllTotalMatch(text);
    }

    protected List<NodeInfo> findNodeInfosByText(String text) {
        return FindByText.findNodeInfos(text);
    }

    protected boolean clickLastTotalMatchByText(String text){
        List<NodeInfo> nodeInfoList = findAllTotalMatchByText(text);
        if(null != nodeInfoList && !nodeInfoList.isEmpty()){
            clickXY(nodeInfoList.get(nodeInfoList.size()-1).getRect().centerX(),nodeInfoList.get(nodeInfoList.size()-1).getRect().centerY());
            return true;
        }
        return false;
    }

    protected boolean clickLastNodeInfosByText(String text){
        List<NodeInfo> nodeInfoList = findNodeInfosByText(text);
        if(null != nodeInfoList && !nodeInfoList.isEmpty()){
            clickXY(nodeInfoList.get(nodeInfoList.size()-1).getRect().centerX(),nodeInfoList.get(nodeInfoList.size()-1).getRect().centerY());
            return true;
        }
        return false;
    }

    protected boolean clickEveryNodeInfosByText(String text){
        List<NodeInfo> nodeInfoList = findNodeInfosByText(text);
        if(null != nodeInfoList && !nodeInfoList.isEmpty()){
            LogUtils.dTag(BASETAG, "clickEveryNodeInfosByText: " + text);
            for(int i=0;i<nodeInfoList.size();i++){
                clickXY(nodeInfoList.get(i).getRect().centerX(),nodeInfoList.get(i).getRect().centerY());
            }
            return true;
        }
        return false;
    }

    protected boolean clickEveryTotalMatchByText(String text){
        List<NodeInfo> nodeInfoList = findAllTotalMatchByText(text);
        if(null != nodeInfoList && !nodeInfoList.isEmpty()){
            LogUtils.dTag(BASETAG, "clickEveryTotalMatchByText: " + text);
            for(int i=0;i<nodeInfoList.size();i++){
                clickXY(nodeInfoList.get(i).getRect().centerX(),nodeInfoList.get(i).getRect().centerY());
            }
            return true;
        }
        return false;
    }

    protected void runOnUiThread(Runnable runnable) {
        MyApplication.getAppInstance().getMainActivity().runOnUiThread(runnable);
    }

    /**
     * 获取最大休眠时间
     *
     * @return
     */
    protected abstract int getMaxSleepTime();

    /**
     * 获取图像识别的结果
     *
     * @return
     */
    protected abstract void getRecognitionResult();

    /**
     * 获取最小休眠时间
     *
     * @return
     */
    protected abstract int getMinSleepTime();

    /**
     * 执行脚本
     */
    protected abstract void executeScript();

    protected abstract boolean isTargetPkg();

    protected abstract void doSamePageDeal();

    /**
     * 点击 x,y
     *
     * @return
     */
    public boolean clickXY(int x, int y) {
        LogUtils.dTag(BASETAG, "click x: " + x + " y:" + y);
        ActionUtils.click(x, y);
        return true;
    }

    /**
     * 点击 x,y
     *
     * @return
     */
    public boolean longPressXY(int x, int y) {
        ActionUtils.longPress(x, y);
        LogUtils.dTag(BASETAG, "click x: " + x + " y:" + y);
        Utils.sleep(2000);
        refreshNodeinfo();
        return false;
    }

    public String getContent(String s) {
        NodeInfo nodeInfo = findByText(s);
        return nodeInfo.getText();
    }

    /**
     * 点击 content
     *
     * @return
     */
    public boolean clickContent(String content) {
        NodeInfo nodeInfo = findByText(content);
        if (nodeInfo != null) {
            LogUtils.dTag(BASETAG, "clickContent: " + content + " x:" + nodeInfo.getRect().centerX() + " y:" + nodeInfo.getRect().centerY());
            ActionUtils.click(nodeInfo);
            return true;
        }
        return false;
    }

    public boolean clickTotalMatchContent(String content) {
        NodeInfo nodeInfo = findTotalMatchByText(content);
        if (nodeInfo != null) {
            LogUtils.dTag(BASETAG, "clickTotalMatchContent: " + content + " x:" + nodeInfo.getRect().centerX() + " y:" + nodeInfo.getRect().centerY());
            ActionUtils.click(nodeInfo);
            return true;
        }
        return false;
    }

    public boolean findTotalMatchContent(String content) {
        NodeInfo nodeInfo = findTotalMatchByText(content);
        if (nodeInfo != null) {
            LogUtils.dTag(BASETAG, "findTotalMatchContent: " + content);
            return true;
        }
        return false;
    }

    /**
     * findContent
     *
     * @return
     */
    public boolean findContent(String content) {
        NodeInfo nodeInfo = findByText(content);
        if (nodeInfo != null) {
            LogUtils.dTag(BASETAG, "findContent: " + content);
            return true;
        }
        return false;
    }

    public List<NodeInfo> findPageByContent(String content,boolean totalMatch) {
        return FindByText.findPageByContent(content,totalMatch);
    }

    public boolean findAllPageByContent(String content,boolean totalMatch) {
        List<NodeInfo> nodeInfoList =  FindByText.findPageByContent(content,totalMatch);
        if(null != nodeInfoList && !nodeInfoList.isEmpty()){
            return true;
        }
        return false;
    }

    /**
     * 点击 content
     *
     * @return
     */
    public boolean clickId(String id) {
        NodeInfo nodeInfo = findById(id);
        if (nodeInfo != null) {
            LogUtils.dTag(BASETAG, "clickId: " + id);
            ActionUtils.click(nodeInfo);
            return true;
        }
        return false;
    }

    /**
     * 点击 content
     *
     * @return
     */
    public boolean findId(String id) {
        NodeInfo nodeInfo = findById(id);
        if (nodeInfo != null) {
            LogUtils.dTag(BASETAG, "findId: " + id);
            return true;
        }
        return false;
    }

    //返回
    public void clickBack() {
        LogUtils.dTag(BASETAG, "clickBack");
        ActionUtils.pressBack();
        Utils.sleep(2000);
        refreshNodeinfo();
    }

    //返回
    public void doubleClickBack() {
        LogUtils.dTag(BASETAG, "clickBack");
        ActionUtils.pressBack();
        Utils.sleep(100);
        ActionUtils.pressBack();
        Utils.sleep(2000);
        refreshNodeinfo();
    }

    public void scrollUpSlow() {
        LogUtils.dTag(BASETAG, "scrollUpSlow");
        int x = MyApplication.getAppInstance().getScreenWidth() / 2 + (int) (Math.random() * 100);
        int margin = 600 + (int) (Math.random() * 100);
        int fromY = MyApplication.getAppInstance().getScreenHeight() - margin;
        int toY = margin;
        new SwipStepBuilder().setPoints(new Point(x, fromY), new Point(x, toY)).get().execute();
        Utils.sleep(2000);
        refreshNodeinfo();
    }

    public void scrollUpPx(int px) {
        LogUtils.dTag(BASETAG, "scrollUpPx"+px);
        int x = MyApplication.getAppInstance().getScreenWidth() / 2 + (int) (Math.random() * 100);
        int fromY = MyApplication.getAppInstance().getScreenHeight()/2 ;
        int toY = fromY - px;
        new SwipStepBuilder().setPoints(new Point(x, fromY), new Point(x, toY)).get().execute();
        Utils.sleep(2000);
        refreshNodeinfo();
    }

    public void scrollUp() {
        LogUtils.dTag(BASETAG, "scrollUp");
        int x = MyApplication.getAppInstance().getScreenWidth() / 2 + (int) (Math.random() * 100);
        int margin = 200 + (int) (Math.random() * 100);
        int fromY = MyApplication.getAppInstance().getScreenHeight() - margin;
        int toY = margin;
        new SwipStepBuilder().setPoints(new Point(x, fromY), new Point(x, toY)).get().execute();
        Utils.sleep(2000);
        refreshNodeinfo();
    }

    public void scrollDown() {
        LogUtils.dTag(BASETAG, "scrollDown");
        int x = MyApplication.getAppInstance().getScreenWidth() / 2 + (int) (Math.random() * 100);
        int margin = 200 + (int) (Math.random() * 100);
        int fromY = MyApplication.getAppInstance().getScreenHeight() - margin;
        int toY = margin;
        new SwipStepBuilder().setPoints(new Point(x, toY), new Point(x, fromY)).get().execute();
        Utils.sleep(2000);
        refreshNodeinfo();
    }

    public void scrollUpNormal() {
        LogUtils.dTag(BASETAG, "scrollUpNormal");
        int x = MyApplication.getAppInstance().getScreenWidth() / 2 + (int) (Math.random() * 100);
        int margin = 400 + (int) (Math.random() * 100);
        int fromY = MyApplication.getAppInstance().getScreenHeight() - margin;
        int toY = margin;
        new SwipStepBuilder().setPoints(new Point(x, fromY), new Point(x, toY)).get().execute();
    }


    /**
     * 处理返回解决不了的弹出框，而且也不能找到资源的
     *
     * @return
     */
    public boolean doRandomClick() {
        int height = ScreenUtils.getScreenHeight();
        int height1 = height / 20;
        int width = ScreenUtils.getScreenWidth();
        Random rand = new Random();
        int randHeight = 20 + rand.nextInt(height1 - 20);
        LogUtils.d(BASETAG, "x:" + (width / 2) + " y:" + (randHeight * 20));
        clickXY(width / 2, randHeight * 20);
        return false;
    }

    public void tryClickDialog() {
        LogUtils.d(BASETAG, "tryClickDialog()");
        clickXY(MyApplication.getScreenWidth()/2,MyApplication.getScreenHeight()/2);
        clickXY(MyApplication.getScreenWidth()/2,MyApplication.getScreenHeight()/2+100);
        clickXY(MyApplication.getScreenWidth()/2,MyApplication.getScreenHeight()/2+200);
        clickXY(MyApplication.getScreenWidth()/2,MyApplication.getScreenHeight()/2+300);
        clickXY(MyApplication.getScreenWidth()/2,MyApplication.getScreenHeight()/2+400);
        clickXY(MyApplication.getScreenWidth()/2,MyApplication.getScreenHeight()/2+500);
        clickXY(MyApplication.getScreenWidth()/2,MyApplication.getScreenHeight()/2+600);
        samePageCount = 0;
    }

    public boolean installPackage(String pnkName) {
        boolean isInstalled = BaseUtil.isInstallPackage(pnkName);
        if(isInstalled){
            return true;
        }
        BaseUtil.goToAppMarket(MyApplication.getAppInstance(), pnkName);
        Utils.sleep(3000);
        if(clickTotalMatchContent("下载")){
            Utils.sleep(2000);
            if(clickTotalMatchContent("立即下载")){
                Utils.sleep(20000);
                if(clickTotalMatchContent("安装")){
                    Utils.sleep(6000);
                    if(clickTotalMatchContent("打开")){
                        Utils.sleep(3000);
                        if(clickTotalMatchContent("同意")){
                            Utils.sleep(3000);
                           clickEveryNodeInfosByText("允许");
                            Utils.sleep(1000);
                           clickEveryNodeInfosByText("允许");
                            Utils.sleep(1000);
                           clickEveryNodeInfosByText("允许");
                            Utils.sleep(1000);
                        }
                    }
                }
            }
        }
        return true;
    }

    public boolean uninstallPackage(String pnkName) {
        AppUtils.uninstallApp(pnkName);
        Utils.sleep(2000);
        if(clickTotalMatchContent("确定"))Utils.sleep(5000);
        if(clickTotalMatchContent("确定"))Utils.sleep(5000);
        return true;
    }

    public boolean tryAutoLogin(String pnkName) {
        return true;
    }

    public void refreshNodeinfo(){
        MyApplication.getAppInstance().getAccessbilityService().setRoot();
    }


    /**
     * 处理返回解决不了的弹出框，但是能找到资源的
     *
     * @return
     */
    public boolean dealNoResponse2() {
        if(clickEveryNodeInfosByText("重试"));
        if(clickEveryNodeInfosByText("确定"));
        if(clickEveryNodeInfosByText("取消"));
        if(clickEveryNodeInfosByText("关闭"));
        if(clickEveryNodeInfosByText("知道"));
        if(clickEveryNodeInfosByText("允许"));
        if(clickEveryNodeInfosByText("禁止"));
        if(clickEveryNodeInfosByText("退出"));
        if(clickEveryNodeInfosByText("离开"));
        if(clickEveryNodeInfosByText("不要"));
        if(clickEveryNodeInfosByText("残忍"));
        if(clickEveryNodeInfosByText("继续"));
        if(clickEveryNodeInfosByText("立即"));
        if(clickEveryNodeInfosByText("以后"));
        if(clickEveryNodeInfosByText("体验"));
        if(clickEveryNodeInfosByText("添加"));
        if(clickEveryNodeInfosByText("立即"));
        if(clickEveryNodeInfosByText("领取"));
        if(clickEveryNodeInfosByText("开心"));
        if(clickEveryNodeInfosByText("收下"));
        if(clickEveryNodeInfosByText("视频"));
        if(clickEveryNodeInfosByText("查看"));
        if(clickEveryNodeInfosByText("签"));
        if(clickEveryNodeInfosByText("更多"));
        if(clickEveryNodeInfosByText("放弃"));
        if(clickEveryNodeInfosByText("开启"));
        if(clickEveryNodeInfosByText("去"));
        if(clickEveryNodeInfosByText("赚"));
        if(clickEveryNodeInfosByText("我的"));

        if (clickId("gold_common_image"));

        if (clickId("close"));

        clickXY(MyApplication.getScreenWidth()/2,MyApplication.getScreenHeight()- SizeUtils.dp2px(25));

        clickXY(MyApplication.getScreenWidth()/5-SizeUtils.dp2px(50),MyApplication.getScreenHeight()- SizeUtils.dp2px(25));

        clickXY(MyApplication.getScreenWidth()-SizeUtils.dp2px(40),SizeUtils.dp2px(40));

        return true;
    }

}
