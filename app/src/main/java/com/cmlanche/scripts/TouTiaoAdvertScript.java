package com.cmlanche.scripts;

import com.blankj.utilcode.util.LogUtils;
import com.cmlanche.core.utils.Utils;
import com.cmlanche.model.AppInfo;

public class TouTiaoAdvertScript extends BaseScript {

    private String TAG = this.getClass().getSimpleName();

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

    private boolean adverting = false;

    private int pageId = -1;//0:首页 1:个人中心  2:阅读页  3:广告页

    public TouTiaoAdvertScript(AppInfo appInfo) {
        super(appInfo);
    }

    @Override
    protected void executeScript() {
        if (!isTargetPkg()) {
            return;
        }

        if (clickAdvert()) return;

        pageId = checkPageId();
        LogUtils.d(TAG, "pageId:" + pageId);

        if (pageId == 0) {

            doPageId0Things();

        } else if (pageId == 1) {

            doPageId1Things();

        } else if (pageId == 2) {

            doPageId2Things();

        } else if (pageId == 3) {

            doPageId3Things();

        } else {
            dealNoResponse();
        }

    }

    private void doPageId0Things() {
        LogUtils.d(TAG, "doPageId0Things");

        if (clickContent("领金币")) return;

        if (clickId("at2")) return;

        scrollUp();

        Utils.sleep(1000);

        if (clickContent("分钟前")) return;

        if (clickContent("小时前")) return;
    }

    int xyCount = 0;

    private void doPageId1Things() {
        LogUtils.d(TAG, "doPageId1Things");

        if (!findContent("看广告赚金币")) {
            scrollUp();
            return;
        }

        if (!findContent("已完成 10/10 次")) {
            if (clickContent("领福利")) return;
        }

        if (clickContent("好的")) return;

        if (xyCount > 2 && findContent("分") && findContent("秒")) {
            if (clickId("k2")) {
                xyCount = 0;
                return;
            }
        }

        //[750,1890][1038,2181]
        clickXY(850, 2000);

        xyCount++;


    }

    private void doPageId2Things() {
        LogUtils.d(TAG, "doPageId2Things");

        if (clickContent("领金币")) return;

        if (clickContent("继续阅读")) return;

        scrollUp();

        if (findId("bz")) {
            clickBack();
        }
        if (findContent("已显示全部评论") || findContent("暂无评论，点击抢沙发")) {
            clickBack();
        }

    }

    private void doPageId3Things() {
        LogUtils.d(TAG, "doPageId3Things");

        boolean isAdvert = isAdverting();
        if (isAdvert) {
            count = 0;
            closeAdvert3();
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

    /**
     * 弹出框里点击看广告
     */
    private boolean clickAdvert() {
        if (clickContent("再看一个获得")) return true;

        if (clickContent("视频再领")) return true;

        return false;
    }

    /**
     * 检查是在那个页面
     *
     * @return //0:首页 1:个人中心  2:阅读页  3:广告页
     */
    private int checkPageId() {
        if (findId("k2") && findContent("频道管理")) {
            return 0;
        }

        if (findContent("任务页") && findContent("金币")) {
            return 1;
        }

        if (findContent("搜索") || findContent("更多操作")) {
            return 2;
        }

        if (findContent("s") && findContent("关闭")) {
            return 3;
        }
        return -1;
    }

    //广告页面弹出框关闭
    private void closeAdvert3() {
        //关闭该页面各种弹出框
        if (clickContent("继续观看")) return;
    }

    private boolean isAdverting() {
        if (findContent("s") && findContent("关闭")) {
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
        return 3000;
    }

    @Override
    public boolean isDestinationPage() {
        // 检查当前包名是否有本年应用
        if (!isTargetPkg()) {
            return false;
        }
        return true;
    }

    @Override
    public void destory() {
        clickBack();
        clickBack();
    }

    private boolean dealNoResponse() {
//        if (clickContent("打开签到提醒")) return true;

        if (clickContent("开心收下")) return true;

        if (clickId("permission_allow_button")) return true;

        if (clickContent("仅使用期间允许")) return true;

        if (clickContent("立即预约")) return true;

        if (clickContent("去拿奖励")) return true;

//        if (clickContent("新人金币礼包")) ;

        clickBack();
        return false;
    }
}
