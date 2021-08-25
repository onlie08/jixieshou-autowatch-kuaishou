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

        if(count >20 ){
            dealNoResponse();
            count = 0;
        }

        if (clickAdvert()) return;

        pageId = checkPageId();
        LogUtils.d(TAG, "pageId:" + pageId +" count:"+count);

        if (pageId == 0) {

            doPageId0Things();

        } else if (pageId == 1) {
            if(clickxy){
                LogUtils.d(TAG,"count"+count);
                clickXY(540, 1500);
                clickxy = false;
            }
            doPageId1Things();

        } else if (pageId == 2) {

            doPageId2Things();

        } else if (pageId == 3) {

            doPageId3Things();

        } else {
//            if(findContent("邀请码")){
////                2021-08-11 17:06:43.988 31249-31399/com.ch.jiandou D/Utils: patternStr:k2 text:ru
////                2021-08-11 17:06:43.989 31249-31399/com.ch.jiandou D/Utils: patternStr:k2 text:content
////                2021-08-11 17:06:43.989 31249-31399/com.ch.jiandou D/Utils: patternStr:k2 text:bb
////                2021-08-11 17:06:43.990 31249-31399/com.ch.jiandou D/Utils: patternStr:k2 text:dv
////                2021-08-11 17:06:43.990 31249-31399/com.ch.jiandou D/Utils: patternStr:k2 text:
////                2021-08-11 17:06:43.990 31249-31399/com.ch.jiandou D/Utils: patternStr:k2 text:
////                2021-08-11 17:06:43.991 31249-31399/com.ch.jiandou D/Utils: patternStr:k2 text:a90
////                clickId("content");
//                clickId("a90");
//                return;
//            }

            if(clickxy){
                LogUtils.d(TAG,"count"+count);
                clickXY(540, 1500);
                clickxy = false;
            }

            clickBack();
            count++;
        }

    }

    private void doPageId0Things() {
        count++;

        LogUtils.d(TAG, "doPageId0Things");

        if (clickContent("领金币")) return;

        if (clickId("at5")) return;

        scrollUp();

        Utils.sleep(2000);

        if (clickContent("0评论")) return;

        if (clickContent("继续阅读")) return;


    }

    boolean clickxy = false;

    private void doPageId1Things() {
        count++;
        LogUtils.d(TAG, "doPageId1Things");

        if (clickContent("好的")) return;
        if (clickContent("知道了")) return;
        if (clickContent("去看看")) return;

        if (!findContent("看广告赚金币")) {
            scrollUp();
            return;
        }

        if (!findContent("已完成 10/10 次")) {
            if (clickContent("领福利")) return;
        }

//        if(clickContent("填写邀请码")) return;

        if(findContent("分") && findContent("秒")){
            if (clickId("k2")) return;
        }else {
            //[750,1890][1038,2181]
            clickXY(850, 2000);
            clickxy = true;
        }
    }

    private void doPageId2Things() {
        count++;
        LogUtils.d(TAG, "doPageId2Things");

        if (clickContent("领金币")) return;

        scrollUp();

        if (findId("bz")) {
            clickBack();
        }
        if (findContent("已显示全部评论") || findContent("暂无评论，点击抢沙发")) {
            clickBack();
        }

    }

    private void doPageId3Things() {
        count++;
        LogUtils.d(TAG, "doPageId3Things");
        if (clickContent("继续观看")) return;
        boolean isAdvert = isAdverting();
        if (isAdvert) {
            adverting = isAdvert;
            count = 0;
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
//        if (clickContent("关闭试玩")) return;
    }

    private boolean isAdverting() {
//        if (findContent("s") && findContent("关闭")) {
        if (findContent("s关闭") || findContent("进入试玩")) {
            return true;
        }
        return false;
    }

    @Override
    protected int getMinSleepTime() {
        if(pageId == 2){
            return 1000;
        }else if(pageId == 1){
            return 3000;
        }else {
            return 3000;
        }

    }

    @Override
    protected int getMaxSleepTime() {
        if(pageId == 2){
            return 1000;
        }else if(pageId == 1){
            return 3000;
        }else {
            return 3000;
        }
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

        if (clickContent("继续阅读")) return true;
        if (clickContent("去赚钱")) return true;
        if (clickContent("重试")) return true;
        if (clickContent("继续观看")) return true;

//        if (clickContent("新人金币礼包")) ;


        clickBack();

        Utils.sleep(1000);

        clickXY(540, 1500);
        return false;
    }
}
