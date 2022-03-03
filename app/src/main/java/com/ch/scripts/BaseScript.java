package com.ch.scripts;

import android.graphics.Point;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.ch.application.MyApplication;
import com.ch.common.PackageUtils;
import com.ch.core.executor.builder.SwipStepBuilder;
import com.ch.core.search.FindById;
import com.ch.core.search.FindByText;
import com.ch.core.search.node.NodeInfo;
import com.ch.core.utils.ActionUtils;
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
                if (findContent("启动应用")) {
                    clickId("button1");
                }
                if(BuildConfig.DEBUG){
                    if (findId("test")) ;
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
            for(int i=0;i<nodeInfoList.size();i++){
                clickXY(nodeInfoList.get(i).getRect().centerX(),nodeInfoList.get(i).getRect().centerY());
                Utils.sleep(500);
            }
            return true;
        }
        return false;
    }

    protected boolean clickEveryTotalMatchByText(String text){
        List<NodeInfo> nodeInfoList = findAllTotalMatchByText(text);
        if(null != nodeInfoList && !nodeInfoList.isEmpty()){
            for(int i=0;i<nodeInfoList.size();i++){
                clickXY(nodeInfoList.get(i).getRect().centerX(),nodeInfoList.get(i).getRect().centerY());
                Utils.sleep(1000);
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
        ActionUtils.click(x, y);
        LogUtils.dTag(BASETAG, "click x: " + x + " y:" + y);
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
    }

    public void scrollUpSlow() {
        LogUtils.dTag(BASETAG, "scrollUpSlow");
        int x = MyApplication.getAppInstance().getScreenWidth() / 2 + (int) (Math.random() * 100);
        int margin = 600 + (int) (Math.random() * 100);
        int fromY = MyApplication.getAppInstance().getScreenHeight() - margin;
        int toY = margin;
        new SwipStepBuilder().setPoints(new Point(x, fromY), new Point(x, toY)).get().execute();
    }

    public void scrollUp() {
        LogUtils.dTag(BASETAG, "scrollUp");
        int x = MyApplication.getAppInstance().getScreenWidth() / 2 + (int) (Math.random() * 100);
        int margin = 200 + (int) (Math.random() * 100);
        int fromY = MyApplication.getAppInstance().getScreenHeight() - margin;
        int toY = margin;
        new SwipStepBuilder().setPoints(new Point(x, fromY), new Point(x, toY)).get().execute();
    }

    public void scrollDown() {
        LogUtils.dTag(BASETAG, "scrollDown");
        int x = MyApplication.getAppInstance().getScreenWidth() / 2 + (int) (Math.random() * 100);
        int margin = 200 + (int) (Math.random() * 100);
        int fromY = MyApplication.getAppInstance().getScreenHeight() - margin;
        int toY = margin;
        new SwipStepBuilder().setPoints(new Point(x, toY), new Point(x, fromY)).get().execute();
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

    /**
     * 处理不在该app时的处理，比如系统弹出框
     *
     * @return
     */
    public boolean dealNoResponse() {
        LogUtils.d(BASETAG, "dealNoResponse()");
        if (clickTotalMatchContent("禁止且不再询问")) return true;
        if (clickTotalMatchContent("本次运行允许")) return true;
        if (clickTotalMatchContent("仅在使用中允许")) return true;
        if (clickTotalMatchContent("始终允许")) return true;
        if (clickTotalMatchContent("禁止")) return true;
        if (clickTotalMatchContent("允许")) return true;

        if (clickTotalMatchContent("关闭")) return true;
        if (clickContent("重试")) return true;
        if (clickTotalMatchContent("取消")) return true;
        if (clickTotalMatchContent("确定")) return true;
        if (clickContent("知道")) return true;
        return false;
    }

    public void tryClickDialog() {
        LogUtils.d(BASETAG, "tryClickDialog()");
        clickXY(MyApplication.getScreenWidth()/2,MyApplication.getScreenHeight()/2);
        Utils.sleep(500);
        clickXY(MyApplication.getScreenWidth()/2,MyApplication.getScreenHeight()/2+100);
        Utils.sleep(500);
        clickXY(MyApplication.getScreenWidth()/2,MyApplication.getScreenHeight()/2+200);
        Utils.sleep(500);
        clickXY(MyApplication.getScreenWidth()/2,MyApplication.getScreenHeight()/2+300);
        Utils.sleep(500);
        clickXY(MyApplication.getScreenWidth()/2,MyApplication.getScreenHeight()/2+400);
        Utils.sleep(500);
        clickXY(MyApplication.getScreenWidth()/2,MyApplication.getScreenHeight()/2+500);
        Utils.sleep(500);
        clickXY(MyApplication.getScreenWidth()/2,MyApplication.getScreenHeight()/2+600);
        Utils.sleep(500);
    }


}
