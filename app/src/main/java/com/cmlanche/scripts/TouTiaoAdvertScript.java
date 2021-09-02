package com.cmlanche.scripts;

import android.graphics.Point;
import android.os.Debug;
import android.text.TextUtils;

import com.blankj.utilcode.util.ClipboardUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.cmlanche.application.MyApplication;
import com.cmlanche.core.utils.ActionUtils;
import com.cmlanche.core.utils.Constant;
import com.cmlanche.core.utils.Utils;
import com.cmlanche.jixieshou.BuildConfig;
import com.cmlanche.model.AppInfo;
import com.cmlanche.model.InviteEvent;
import com.cmlanche.model.RecognitionBean;
import com.cmlanche.model.ScreenShootEvet;
import com.google.gson.Gson;
import com.tencent.bugly.crashreport.CrashReport;

import org.greenrobot.eventbus.EventBus;

import static android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_TAKE_SCREENSHOT;
import static com.cmlanche.application.MyApplication.KEY_XY1;

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
    private int lastPageId = -1; //上次的页面
    private int samePageCount = 0; //同一个页面停留次数

    @Override
    protected boolean isTargetPkg() {
        if(MyApplication.getAppInstance().getAccessbilityService().isWrokFine()) {
            if(!MyApplication.getAppInstance().getAccessbilityService().containsPkg(Constant.PN_TOU_TIAO)) {
                return false;
            }
        }
        return true;
    }

    public TouTiaoAdvertScript(AppInfo appInfo) {
        super(appInfo);
    }

    @Override
    protected void executeScript() {
        LogUtils.d(TAG, "executeScript");
        if (!isTargetPkg()) {
            return;
        }

        if(samePageCount > 20){
            clickBack();
            clickBack();
            samePageCount =0;
        }

        if (count > 20) {
            if(BuildConfig.DEBUG){
                MyApplication.getAppInstance().getAccessbilityService().performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT);
                Utils.sleep(1500);
            }
            dealNoResponse();
        }

        if(count >30){
            MyApplication.getAppInstance().getAccessbilityService().performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT);
            Utils.sleep(1500);
            clickBack();
            clickBack();
            count = 0;
            return;
        }



        if (clickAdvert()) return;

        pageId = checkPageId();
        if(pageId == lastPageId){
            samePageCount ++;
        }else {
            samePageCount = 0;
        }
        lastPageId = pageId;
        LogUtils.d(TAG, "pageId:" + pageId + " count:" + count+ " samePageCount:" + samePageCount);

        if (pageId == 0) {

            doPageId0Things();

        } else if (pageId == 1) {

            if (KEY_XY1 == null) {
                EventBus.getDefault().post(new ScreenShootEvet());
                return;
            }

            doPageId1Things();

        } else if (pageId == 2) {

            doPageId2Things();

        } else if (pageId == 3) {
            baoXiangClickCount = 0;
            doPageId3Things();

        } else {
            if (findContent("邀请码")) {
                count++;
                if (autoInvite()) {
                    clickBack();
                    clickBack();
                }
                return;
            }

            clickBack();
            count++;
        }

    }

    private void doPageId0Things() {
        count++;

        LogUtils.d(TAG, "doPageId0Things");

        if (clickContent("领金币")) return;

        scrollUp();

        Utils.sleep(3000);

        if(findId("au9")){
            clickId("aew");return;
        }

        if (clickContent("0评论")) return;

        if (clickContent("继续阅读")) return;



    }

    int baoXiangClickCount = 0;
    private void doPageId1Things() {
        count++;
        LogUtils.d(TAG, "doPageId1Things");

        if(count > 10){
            if (clickContent("好的")) return;
            if (clickContent("知道了")) return;
            if (clickContent("去看看")) return;
            if (clickContent("去赚钱")) return;
            clickXY(MyApplication.KEY_XY2.x,MyApplication.KEY_XY2.y);
        }

        if (!findContent("看广告赚金币")) {
            scrollUp();
            return;
        }

        if (!findContent("已完成 10/10 次")) {
            if(findContent("领福利")){
                if (clickContent("看广告赚金币")) return;
            }
        } else {
            if (clickContent("填写邀请码")) {
                return;
            }
        }
        if (findContent("分") && findContent("秒")) {
            clickBack();
        }else {
            clickXY(KEY_XY1.x, KEY_XY1.y);
        }

        if(baoXiangClickCount == 0){
            clickXY(KEY_XY1.x, KEY_XY1.y);
            baoXiangClickCount ++;
            return;
        }
        clickBack();
        baoXiangClickCount = 0;

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

        if ((findContent("s") && findContent("关闭")) || findContent("试玩")) {
            return 3;
        }
        return -1;
    }


    private boolean isAdverting() {
        if (findContent("s") && findContent("关闭")) {
//        if (findContent("s关闭") || findContent("进入试玩")) {
            return true;
        }
        return false;
    }

    @Override
    protected int getMinSleepTime() {
        if (pageId == 2) {
            return 1000;
        } else if (pageId == 1) {
            return 4000;
        } else {
            return 4000;
        }

    }

    @Override
    protected int getMaxSleepTime() {
        if (pageId == 2) {
            return 1000;
        } else if (pageId == 1) {
            return 4000;
        } else {
            return 4000;
        }
    }


    public boolean isCurrentScipte(){
        return getAppInfo().getPkgName().equals(Constant.PN_TOU_TIAO) ? true : false;
    }

    int resumeCount = 0;
    @Override
    public boolean isDestinationPage() {
        // 检查当前包名是否有本年应用
        if (!isTargetPkg() && isCurrentScipte()) {
            resumeCount++;
            if (resumeCount > 50) {
                LogUtils.d(TAG, "自动恢复到头条极速版");
                CrashReport.postCatchedException(new Throwable("自动恢复到头条极速版"));
                startApp();
            }
            if (resumeCount > 60) {
                MyApplication.getAppInstance().getAccessbilityService().performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT);
                LogUtils.d(TAG, "头条极速版是不是anr了?");
                clickContent("允许");
                clickContent("确定");
                clickContent("取消");
                clickBack();
                clickBack();
            }
            return false;
        }
        resumeCount = 0;
        return true;
    }

    @Override
    public void destory() {
        clickBack();
        clickBack();
    }

    private boolean dealNoResponse() {
        clickBack();

        if (clickId("k2")) return true;
        if (clickContent("关闭")) return true;
        if (clickContent("打开签到提醒")) return true;

        if (clickContent("开心收下")) return true;

        if (clickId("permission_allow_button")) return true;

        if (clickContent("允许")) return true;

        if (clickContent("立即预约")) return true;

        if (clickContent("去拿奖励")) return true;

        if (clickContent("继续阅读")) return true;
        if (clickContent("去赚钱")) return true;
        if (clickContent("重试")) return true;
        if (clickContent("继续观看")) return true;
        if (clickContent("立即添加")) return true;

//        if (clickContent("新人金币礼包")) ;
        Utils.sleep(1000);

//        clickXY(540, 1500);
        return false;
    }

    private boolean autoInvite() {
        //[50,718][1150,838]
        String ky1 = SPUtils.getInstance().getString("findEdit", "");/**/
        if (TextUtils.isEmpty(ky1)) {
            SPUtils.getInstance().put("invitexy", "");
        }

        String ky2 = SPUtils.getInstance().getString("invitexy", "");
        if (!TextUtils.isEmpty(ky2)) {
            RecognitionBean recognitionBean = new Gson().fromJson(ky2, RecognitionBean.class);
            if (null != recognitionBean) {
                Point p0 = new Point();
                p0.x = (recognitionBean.getP1().x + recognitionBean.getP3().x) / 2;
                p0.y = (recognitionBean.getP1().y + recognitionBean.getP3().y) / 2;
                clickXY(p0.x, p0.y);
                Utils.sleep(1000);

                clickContent("马上提交");
                Utils.sleep(2000);
                CrashReport.postCatchedException(new Throwable("头条自动填写邀请码成功"));
                return true;

            }
        }

        if (!TextUtils.isEmpty(ky1)) {
            RecognitionBean recognitionBean = new Gson().fromJson(ky1, RecognitionBean.class);
            if (null != recognitionBean) {
                Point p0 = new Point();
                p0.x = (recognitionBean.getP1().x + recognitionBean.getP3().x) / 2;
                p0.y = (recognitionBean.getP1().y + recognitionBean.getP3().y) / 2;

                ActionUtils.longPress(p0.x, p0.y);
                Utils.sleep(1500);
                EventBus.getDefault().post(new InviteEvent(1));
            }
        } else {
            EventBus.getDefault().post(new InviteEvent(1));
            return false;
        }
        return false;
    }
}

