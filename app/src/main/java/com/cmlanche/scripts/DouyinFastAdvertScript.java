package com.cmlanche.scripts;

import com.blankj.utilcode.util.LogUtils;
import com.cmlanche.application.MyApplication;
import com.cmlanche.core.executor.builder.SFStepBuilder;
import com.cmlanche.core.search.node.NodeInfo;
import com.cmlanche.core.utils.Constant;
import com.cmlanche.model.AppInfo;

public class DouyinFastAdvertScript extends BaseScript {
    private String TAG = this.getClass().getSimpleName();

    private volatile static DouyinFastAdvertScript instance; //声明成 volatile

    public static DouyinFastAdvertScript getSingleton(AppInfo appInfo) {
        if (instance == null) {
            synchronized (DouyinFastAdvertScript.class) {
                if (instance == null) {
                    instance = new DouyinFastAdvertScript(appInfo);
                }
            }
        }
        return instance;
    }

    private boolean adverting = false;
    private boolean fasting = false;

    @Override
    protected boolean isTargetPkg() {
        if(MyApplication.getAppInstance().getAccessbilityService().isWrokFine()) {
            if(!MyApplication.getAppInstance().getAccessbilityService().containsPkg(Constant.PN_DOU_YIN)) {
                return false;
            }
        }
        return true;
    }

    public DouyinFastAdvertScript(AppInfo appInfo) {
        super(appInfo);
    }

    @Override
    protected void executeScript() {
        if (!isTargetPkg()) {
            return;
        }

        LogUtils.d(TAG, "count:" + count);
        if (count > 50) {
            fasting = false;
            count = 0;
            return;
        }
        if (fasting) {
            count++;
            scrollUp();
            return;
        }

        boolean isAdvert = isAdverting();
        if (isAdvert) {
            closeAdvert3();
            adverting = isAdvert;
            fasting = false;
            return;
        }
        if (adverting && !isAdvert) {
            clickBack();
            adverting = isAdvert;
            return;
        }

        if (goPersonPage()) return;

        //todo 关闭弹框
        if (closeAdvert()) return;

//        if (!isPersonPage()) {
//            LogUtils.d(TAG, "DEBUG1 !isPersonPage()");
//            clickBack();
//            return;
//        }

        if (isPersonPage() && !findContent("看广告赚金币")) {
            LogUtils.d(TAG, "DEBUG2 scrollUp()");
            scrollUp();
            return;
        }

        //todo 看广告
        if (clickAdvert()) return;

        if (isPersonPage() && !findContent("开宝箱得金币")) {
            LogUtils.d(TAG, "DEBUG3  clickBack()");
            fasting = true;
            clickBack();
            return;
        }

        clickBack();
        return;

    }

    private boolean isPersonPage() {
        //找到页面左上角返回按键
        if (findId("a97")){
            return true;
        }
        return false;
    }

    private boolean isAdverting() {
        NodeInfo nodeInfo1 = findByText("后可领取");
        if (nodeInfo1 != null) {
            LogUtils.dTag(TAG, "找到后可领取");
            return true;
        }
        return false;
    }


    @Override
    protected int getMinSleepTime() {
        if (fasting) {
            return 1000;
        } else {
            return 2000;
        }
    }

    @Override
    protected int getMaxSleepTime() {
        if (fasting) {
            return 10000;
        } else {
            return 4000;
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

    //浏览短视频页面弹出框关闭
    private boolean closeAdvert() {
        LogUtils.dTag(TAG, "closeAdvert()");

        if (clickContent("知道了")) return true;

        //[288,1488][792,1605]
        if (clickContent("好的")) return true;

        if (clickContent("再看一个获取")) return true;

        if (clickContent("立即签到")) return true;

        return false;
    }

    //广告页面弹出框关闭
    private void closeAdvert3() {
        //关闭该页面各种弹出框
        if (clickContent("继续观看")) return;
    }

    //跳转个人中心
    private boolean goPersonPage() {
        NodeInfo nodeInfo1 = findById("cqt");//红包浮动框
        if (nodeInfo1 != null) {
            new SFStepBuilder().addStep(nodeInfo1).get().execute();
            LogUtils.dTag(TAG, "click goPersonPage");
            return true;
        }
        return false;
    }

    //看广告
    private boolean clickAdvert() {
        LogUtils.d(TAG, "DEBUG4  clickAdvert()");

        if (clickContent("看广告视频再赚")) return true;

        if(countDown()){
            if (clickContent("看广告赚金币")) return true;
        }

        if (clickContent("开宝箱得金币")) return true;

        if (clickContent("再看一个获取")) return true;

        return false;
    }

    private boolean countDown(){
        if(findContent("09:") || findContent("08:") || findContent("07:") || findContent("06:")
                || findContent("05:")|| findContent("04:")|| findContent("03:")|| findContent("02:")
                || findContent("01:") || findContent("00:")){
            return false;
        }
        return true;
    }

}
