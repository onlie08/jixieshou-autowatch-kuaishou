package com.cmlanche.scripts;

import com.cmlanche.model.AppInfo;

public class TouTiaoFastScript extends BaseScript {
    private String TAG = this.getClass().getSimpleName();

    private boolean isFasting = false;

    public TouTiaoFastScript(AppInfo appInfo) {
        super(appInfo);
    }

    @Override
    protected void executeScript() {

        if (!isTargetPkg()) {
            return;
        }

        if (clickContent("万次播放")) {
            isFasting = true;
            return;
        }

        if (isFasting) {
            scrollUp();
            return;
        }

        if (goPersonPage()) return;

//        if (clickContent("新人金币礼包")) ;

        if (clickContent("不再提示")) return;
        if (clickContent("知道了")) return;

        if (clickContent("看视频赚钱")) return;

    }

    //跳转个人中心
    private boolean goPersonPage() {
        if (clickId("at2")) {
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
        if (!isTargetPkg()) {
            return false;
        }
        return true;
    }

    @Override
    public void destory() {
        isFasting = false;
        clickBack();
        clickBack();
    }
}
