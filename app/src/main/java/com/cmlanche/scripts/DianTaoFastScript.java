package com.cmlanche.scripts;

import com.blankj.utilcode.util.LogUtils;
import com.cmlanche.core.search.node.NodeInfo;
import com.cmlanche.core.utils.Utils;
import com.cmlanche.model.AppInfo;

/**
 * 点淘急速版脚本
 */
public class DianTaoFastScript extends BaseScript {
    private String TAG = this.getClass().getSimpleName();

    private int pageId = -1;//0:首页 1:个人中心  2:直播页
    private int Count = 0;

    public DianTaoFastScript(AppInfo appInfo) {
        super(appInfo);
    }

    @Override
    protected void executeScript() {
        if (!isTargetPkg()) {
            return;
        }

        pageId = checkPageId();
        LogUtils.d(TAG, "pageId:" + pageId);

        if (pageId == 0) {

            doPageId0Things();

        } else if (pageId == 1) {

            doPageId1Things();

        } else if (pageId == 2) {

            doPageId2Things();

        } else {
            dealNoResponse();
        }
        dealNoResponse();
    }


    private void doPageId0Things() {
        scrollUp();

        Utils.sleep(1000);

        if (clickId("gold_common_image")) return;

        if (clickContent("观看")) return;


    }

    //[837,318][1080,465]
    int x = 900;
    int y = 320;
    private void doPageId1Things() {
        if(findContent("邀请好友 再赚")){
            clickBack();
        }

        if(findContent("去看直播赚")){
            clickBack();
        }
        if(findContent("走路赚元宝 每日")){
            clickBack();
        }

        if(clickContent("立即签到"))return;

        NodeInfo nodeInfo = findByText("领取");

        if(nodeInfo != null){
            x = nodeInfo.getRect().centerX();
            y = nodeInfo.getRect().centerY();
            if (clickContent("领取")) return;
        }else {
            if(x != 0 && y != 0){
//                clickXY(x,y);
//                Utils.sleep(10);
//                clickXY(x,y);
//                Utils.sleep(10);
//                clickXY(x,y);
            }

        }





    }

    int timeCount = 0;
    private void doPageId2Things() {
        if (findContent("6/6")) {
            timeCount++;
            if (timeCount > 5) {
                if (clickId("gold_turns_container")) return;
            }
        } else {
            timeCount = 0;
        }
    }

    private void dealNoResponse() {
        if(clickContent("知道了"))return;
    }


    /**
     * 检查是在那个页面
     *
     * @return //0:首页 1:个人中心  2:直播
     */
    private int checkPageId() {
        if (findId("homepage_container") && findId("gold_common_image")) {
            return 0;
        }

        if (findContent("元宝中心") && findContent("规则")) {
            return 1;
        }

        if (findId("taolive_room_watermark_text")) {
            return 2;
        }

        return -1;
    }


    @Override
    protected int getMinSleepTime() {
        return 5000;
    }

    @Override
    protected int getMaxSleepTime() {
        return 5000;
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
}
