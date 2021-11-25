package com.ch.scripts;

import android.util.Log;

import com.blankj.utilcode.util.LogUtils;
import com.ch.application.MyApplication;
import com.ch.common.PackageUtils;
import com.ch.core.bus.BusEvent;
import com.ch.core.bus.BusManager;
import com.ch.core.bus.EventType;
import com.ch.core.utils.Constant;
import com.ch.core.utils.Utils;
import com.ch.model.AppInfo;
import com.ch.model.TaskInfo;
import com.google.gson.Gson;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.Calendar;
import java.util.List;

import static com.ch.core.bus.EventType.pause_becauseof_not_destination_page;

/**
 * 任务执行器
 */
public class TaskExecutor {
    private String TAG = this.getClass().getSimpleName();
    private boolean isStarted = false;
    private boolean pause = false;
    private boolean forcePause = false;
    private boolean isFinished = true;
    private AppInfo currentTaskApp;
    private IScript currentScript;

    private Thread scriptThread;
    private Thread monitorThread;

    long allTime;

    private static class TaskExecutorHolder {
        private static TaskExecutor instance = new TaskExecutor();
    }

    public TaskExecutor() {
    }

    public static TaskExecutor getInstance() {
        return TaskExecutorHolder.instance;
    }

    public void startTask(final AppInfo appInfo) {
        LogUtils.d(TAG, "startTask:" + new Gson().toJson(appInfo));
        currentTaskApp = appInfo;
        allTime = appInfo.getPeriod() * 60 * 1000;
//        allTime = 1 * 60 * 1000;

        this.initStartFlags();
        if (scriptThread == null) {
            LogUtils.d(TAG, "scriptThread == null");
            scriptThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        IScript script = null;
                        switch (appInfo.getPkgName()) {
                            case Constant.PN_DOU_YIN:
                                script = DouyinFastAdvertScript.getSingleton(appInfo);
                                break;
                            case Constant.PN_KUAI_SHOU:
                                script = KuaishouFastScript.getSingleton(appInfo);
                                break;
                            case Constant.PN_TOU_TIAO:
                                script = TouTiaoAdvertScript.getSingleton(appInfo);
                                break;
                            case Constant.PN_FENG_SHENG:
                                script = new FengShengFastScript(appInfo);
                                break;
                            case Constant.PN_DIAN_TAO:
                                script = DianTaoFastScript.getSingleton(appInfo);
                                break;
                            case Constant.PN_YING_KE:
                                script = new YingKeFastScript(appInfo);
                                break;
                            case Constant.PN_AI_QI_YI:
                                script = AiQiYiAdvertScript.getSingleton(appInfo);
                                break;
                            case Constant.PN_BAI_DU:
                                script = BaiDuAdvertScript.getSingleton(appInfo);
                                break;
                            case Constant.PN_JING_DONG:
                                script = JingDongAdvertScript.getSingleton(appInfo);
                                break;
                            case Constant.PN_TAO_TE:
                                script = TaoTeScript.getSingleton(appInfo);
                                break;
                            case Constant.PN_HUO_SHAN:
                                script = HuoShanAdvertScript.getSingleton(appInfo);
                                break;
                            case Constant.PN_MEI_TIAN_ZHUAN_DIAN:
                                script = MeiTianZhuanDianScript.getSingleton(appInfo);
                                break;
                            case Constant.PN_FAN_QIE:
                                script = FanQieScript.getSingleton(appInfo);
                                break;
                        }
                        if (script != null) {
                            currentScript = script;
                            script.execute();
                            LogUtils.d(TAG, "script.execute():" + new Gson().toJson(currentScript));
                        }
//                        }
                    } catch (Exception e) {
                        Log.e(TAG, "执行任务异常：" + e.getMessage());
                        CrashReport.postCatchedException(e);
                    } finally {
                        // 执行完成
//                        resetFlags();
//                        PackageUtils.startSelf();
                        Log.e(TAG, "执行完成，回到本程序");
                    }
                }
            });
            scriptThread.start();
            LogUtils.d(TAG, "scriptThread.start()");
            monitorThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    long st = System.currentTimeMillis();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            BusManager.getBus().post(new BusEvent<>(EventType.start_task, allTime));
                        }
                    });

                    while (System.currentTimeMillis() - st < allTime) {
                        try {
                            if (currentScript != null) {
                                if (isForcePause()) {
                                    setPause(true);
                                } else {
                                    boolean isDestinationPage = currentScript.isDestinationPage();
                                    setPause(!isDestinationPage);
                                    long finalSt = st;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (isPause()) {
                                                BusManager.getBus().post(new BusEvent<>(pause_becauseof_not_destination_page, currentScript.getAppInfo().getAppName()));
                                            } else {
                                                String s = Utils.getTimeDescription(System.currentTimeMillis() - finalSt);
                                                BusManager.getBus().post(new BusEvent<>(EventType.refresh_time, s));
                                            }
                                        }
                                    });
                                }
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "监控异常：" + e.getMessage());
                        } finally {
                            Utils.sleep(1000);
                        }
                    }

                    LogUtils.d(TAG, "currentScript.destory()");
                    currentScript.destory();
                    currentScript = null;
                    Utils.sleep(1000);

                    resetFlags();
                    scriptThread.interrupt();
                    scriptThread = null;

                    monitorThread.interrupt();
                    monitorThread = null;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            BusManager.getBus().post(new BusEvent<>(EventType.task_finish, currentTaskApp));
                        }
                    });
                    PackageUtils.startSelf();
                    Log.e(TAG, "到期了");
                }
            });
            monitorThread.start();
        } else {
            if (currentScript != null) {
                currentScript.resetStartTime();
                currentScript.startApp();
            }
        }
    }

    protected void runOnUiThread(Runnable runnable) {
        MyApplication.getAppInstance().getMainActivity().runOnUiThread(runnable);
    }

    /**
     * 初始化标记
     */
    private void initStartFlags() {
        this.isStarted = true;
        this.pause = false;
        this.isFinished = false;
        this.forcePause = false;
    }

    /**
     * 重置所有标记
     */
    private void resetFlags() {
        LogUtils.d(TAG, "resetFlags()");
        isFinished = true;
        isStarted = false;
        setPause(true);
        setForcePause(true);
    }

    /**
     * 停止任务
     */
    public void stop(boolean force) {
        LogUtils.d(TAG, "stop()" + force);
        setForcePause(force);
        setPause(true);
    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }

    public boolean isPause() {
        return pause;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public boolean isForcePause() {
        return forcePause;
    }

    public void setForcePause(boolean forcePause) {
        this.forcePause = forcePause;
    }

}
