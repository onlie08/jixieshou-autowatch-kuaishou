package com.cmlanche.scripts;

import android.graphics.Point;
import android.util.Log;

import com.cmlanche.application.MyApplication;
import com.cmlanche.core.executor.builder.SwipStepBuilder;
import com.cmlanche.core.search.node.NodeInfo;
import com.cmlanche.core.utils.ActionUtils;
import com.cmlanche.model.AppInfo;

import java.util.Random;

/**
 * 快手急速版脚本
 */
public class KuaishouFastScript extends BaseScript {
    private String TAG = this.getClass().getSimpleName();
    private boolean fasting = false;
    private boolean adverting = false;

    // 是否有检查"我知道了"

    public KuaishouFastScript(AppInfo appInfo) {
        super(appInfo);
    }

    @Override
    protected void executeScript() {
        if(count > 20){
            fasting = false;
            count = 0;
            return;
        }
        if(fasting){
            count++;
            scrollUp();
            return;
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

        if(clickId("red_packet_anim")){
            return;
        }

        if (clickContent("看精彩视频赚更多")) return;

        if (clickContent("开宝箱得金币")) return;

        if (clickContent("观看广告单日最高")) return;

        if(findContent("明天再来")){
            clickBack();
            fasting = true;
        }

        if(!findContent("看广告")){
            scrollUp();
            return;
        }

        if(count>5){
            clickBack();
        }

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

    //广告页面弹出框关闭
    private void closeAdvert3() {
        //关闭该页面各种弹出框
        if(clickContent("继续观看")) return ;
    }


    private boolean isAdverting() {
        if(findContent("s后可领取奖励")){
            return true;
        }
        return false;
    }

}
