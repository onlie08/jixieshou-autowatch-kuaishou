package com.cmlanche.scripts;

import com.blankj.utilcode.util.LogUtils;
import com.cmlanche.model.AppInfo;

public class TouTiaoAdvertScript extends BaseScript {

    private String TAG = this.getClass().getSimpleName();
    private boolean adverting = false;

    public TouTiaoAdvertScript(AppInfo appInfo) {
        super(appInfo);
    }

    @Override
    protected void executeScript() {
        if (!isTargetPkg()) {
            return;
        }
        LogUtils.d(TAG,"count:"+count);

        if(!findContent("金币")){
            if (goPersonPage()){
                return;
            }else {
                if(findContent("频道管理")){
                    count = 0 ;
                    scrollUp();
                    return;
                }
            }
        }

        boolean isAdvert = isAdverting();
        if (isAdvert) {
            count = 0 ;
            closeAdvert3();
            adverting = isAdvert;
            return;
        }
        if (adverting && !isAdvert) {
            clickBack();
            adverting = isAdvert;
            return;
        }

        if (clickContent("领福利"));

        if (clickContent("再看一个获得")) return;

        if (clickContent("视频再领")) return;

        if (clickContent("看完视频再领")) return;


        if(count>1){
            if(dealNoResponse()) return;
        }

        if(count>3){
            //[201,1395][879,1584]
            count++;
            if(!findContent("看广告赚金币")){
                clickXY(540,1500); return;
            }
        }

        if(count>5){
            if(clickId("k2")) return;
        }

        count++;

        if(findContent("金币")){

            if(!findContent("看广告赚金币")){
                scrollUp();
                return;
            }

            //[750,1890][1038,2181]
            clickXY(850,2000);

        }else {
            if(findContent("频道管理")){
                count = 0 ;
                scrollUp();
                return;
            }
        }



    }

    //广告页面弹出框关闭
    private void closeAdvert3() {
        //关闭该页面各种弹出框
        if(clickContent("继续观看")) return ;
    }

    private boolean isAdverting() {
        if(findContent("s") && findContent("关闭")){
            return true;
        }
        return false;
    }

    //跳转个人中心
    private boolean goPersonPage() {
        if(clickId("as_")){
            return true;
        }
        return false;
    }

    @Override
    protected int getMinSleepTime() {
        return 4000;
    }

    @Override
    protected int getMaxSleepTime() {
        return 6000;
    }

    @Override
    public boolean isDestinationPage() {
        // 检查当前包名是否有本年应用
        if(!isTargetPkg()) {
            return false;
        }
        return true;
    }

    @Override
    public void destory() {

    }

    private boolean dealNoResponse(){
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
