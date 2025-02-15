package com.ch.scripts;

import android.graphics.Point;

import com.blankj.utilcode.util.LogUtils;
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
import com.ch.model.AppInfo;

import java.util.Random;

public abstract class TimeBaseScript implements IScript {
    private String BASETAG = this.getClass().getSimpleName();

    private AppInfo appInfo;
    private long startTime;
    public boolean stop = false;

    public TimeBaseScript(AppInfo appInfo) {
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
                executeScript();
            } catch (Exception e) {
                Logger.e("执行异常，脚本: " + appInfo.getTaskName(), e);
            } finally {
                if (findContent("启动应用")) {
                    clickId("button1");
                }
                int t = getRandomSleepTime(getMinSleepTime(), getMaxSleepTime());
                Logger.i("休眠：" + t);
                Utils.sleep(t);
            }
        }
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
        return false;
    }

    /**
     * 点击 x,y
     *
     * @return
     */
    public boolean longPressXY(int x, int y) {
        ActionUtils.click(x, y);
        LogUtils.dTag(BASETAG, "click x: " + x + " y:" + y);
        return false;
    }


    /**
     * 点击 content
     *
     * @return
     */
    public boolean clickContent(String content) {
        NodeInfo nodeInfo = findByText(content);
        if (nodeInfo != null) {
            LogUtils.dTag(BASETAG, "clickContent: " + content);
            ActionUtils.click(nodeInfo);
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
}
