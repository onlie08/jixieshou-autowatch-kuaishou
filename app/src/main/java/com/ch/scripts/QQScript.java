package com.ch.scripts;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.ch.application.MyApplication;
import com.ch.core.utils.ActionUtils;
import com.ch.core.utils.Constant;
import com.ch.core.utils.Utils;
import com.ch.model.AppInfo;

public class QQScript extends BaseTimingScript{
    private String TAG = this.getClass().getSimpleName();

    private int pageId = -1;

    private volatile static QQScript instance; //声明成 volatile

    public static QQScript getSingleton(AppInfo appInfo) {
        if (instance == null) {
            synchronized (QQScript.class) {
                if (instance == null) {
                    instance = new QQScript(appInfo);
                }
            }
        }
        return instance;
    }

    public QQScript(AppInfo appInfo) {
        super(appInfo);
    }

    @Override
    protected void getRecognitionResult() {

    }

    @Override
    protected void executeScript() {
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
        LogUtils.d(TAG,"pageId:"+pageId);
        switch (pageId){
            case 1:
                if(clickContent("[微信红包]"))return;
                break;
            case 2:
                if(clickContent("微信红包"))return;
                break;
            case 3:
                if(clickId("f4f"))return;
                //[404,1376][674,1646]
                clickXY(MyApplication.getAppInstance().getScreenWidth() / 2,1500);
                break;
            case 4:
                clickBack();
                Utils.sleep(1000);
                ActionUtils.pressHome();
                break;
            default:
//                clickBack();
                break;
        }

    }

    private int checkPageId() {
        if(findContent("已存入零钱，可")){
        return 4;
        }
        if(findContent("微信(") && findContent("更多功能按钮")){
            return 1;
        }
        if(findContent("更多功能按钮，已折叠") && findContent("切换到按住说话") && !findContent("已领取")){
            return 2;
        }
        if(findContent("的红包") && findContent("开")&& findContent("关闭")){
            return 3;
        }

        return -1;
    }

    @Override
    protected boolean isTargetPkg() {
        if (MyApplication.getAppInstance().getAccessbilityService().isWrokFine()) {
            if (!MyApplication.getAppInstance().getAccessbilityService().containsPkg(Constant.PN_WEI_XIN)) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void doSamePageDeal() {

    }

    public boolean isCurrentScipte() {
        return getAppInfo().getPkgName().equals(Constant.PN_BAI_DU) ? true : false;
    }

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
            return false;
        }
        return true;
    }

    @Override
    public void destory() {

    }
}
