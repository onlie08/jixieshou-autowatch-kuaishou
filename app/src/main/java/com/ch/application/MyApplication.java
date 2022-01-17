package com.ch.application;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.ch.activity.EditTaskActivity;
import com.ch.activity.MainActivity;
import com.ch.activity.TaskTypeListActivity;
import com.ch.common.PackageUtils;
import com.ch.common.SPService;
import com.ch.core.bus.BusEvent;
import com.ch.core.bus.BusManager;
import com.ch.core.service.MyAccessbilityService;
import com.ch.core.utils.Logger;
import com.ch.core.utils.SFUpdaterUtils;
import com.ch.floatwindow.FloatWindow;
import com.ch.floatwindow.MoveType;
import com.ch.floatwindow.PermissionListener;
import com.ch.floatwindow.ViewStateListener;
import com.ch.jixieshou.BuildConfig;
import com.ch.jixieshou.R;
import com.ch.model.AppInfo;
import com.ch.model.RecommendBean;
import com.ch.scripts.TaskExecutor;
import com.sf.appupdater.log.LogInfo;
import com.sf.appupdater.log.LogWriter;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.otto.Subscribe;
import com.tencent.bugly.crashreport.CrashReport;

import cn.leancloud.AVOSCloud;

import static com.ch.core.bus.EventType.accessiblity_connected;
import static com.ch.core.bus.EventType.no_roots_alert;
import static com.ch.core.bus.EventType.pause_becauseof_not_destination_page;
import static com.ch.core.bus.EventType.pause_byhand;
import static com.ch.core.bus.EventType.refresh_time;
import static com.ch.core.bus.EventType.roots_ready;
import static com.ch.core.bus.EventType.set_accessiblity;
import static com.ch.core.bus.EventType.start_task;
import static com.ch.core.bus.EventType.unpause_byhand;

public class MyApplication extends Application {

    private static final String TAG = "MyApplication";
    public static MyAccessbilityService accessbilityService;
    public static RecommendBean recommendBean;
    protected static MyApplication appInstance;
    private static int screenWidth;
    private static int screenHeight;
    private boolean isVip = false;
    private View floatView;
    private MainActivity mainActivity;
    private boolean isStarted = false;


    @Override
    public void onTerminate() {
        super.onTerminate();
        LogUtils.d(TAG, "onTerminate()");
        if (null != FloatWindow.get()) {
            if (FloatWindow.get().isShowing()) {
                FloatWindow.get().hide();
                LogUtils.d(TAG, "FloatWindow.get().hide()");
            }
        }
//        stopService(new Intent(getApplicationContext(), MyAccessbilityService.class));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.setDebug(true);
//        XUI.init(this); //初始化UI框架
//        XUI.debug(true);  //开启UI框架调试日志
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);

        CrashReport.initCrashReport(getApplicationContext(), "8aa7474c90", BuildConfig.DEBUG);
        initUmeng();
        initLeancloud();
        SPService.init(this);
        appInstance = this;

        LogWriter logWriter = new LogWriter() {
            @Override
            public void write(LogInfo logInfo) {
                Log.d("logInfo", "logInfo: " + logInfo);
            }
        };
        SFUpdaterUtils.setAppUpdaterInfo(this, "f6f24f301189059dbbd1046cfc20e12e", "2e69975d7e0348009687c4cbf7bcf954", true, com.sf.appupdater.Environment.PRODUCTION, false, logWriter);


        Display display = getDisplay(getApplicationContext());
        this.screenWidth = display.getWidth();
        this.screenHeight = display.getHeight();
        BusManager.getBus().register(this);

        showFloatWindow();
    }

    @Subscribe
    public void subscribeEvent(BusEvent event) {
        switch (event.getType()) {
            case set_accessiblity:
                Toast.makeText(getApplicationContext(), "服务启动成功！", Toast.LENGTH_LONG).show();
                this.accessbilityService = (MyAccessbilityService) event.getData();
                break;
            case start_task:
                this.isStarted = true;
                setFloatText("任务执行中");
                break;
            case pause_byhand:
                if (isStarted) {
                    setFloatText("已被您暂停");
                }
                break;
            case unpause_byhand:
                if (isStarted) {
                    setFloatText("捡豆子已开始");
                }
                break;
            case pause_becauseof_not_destination_page:
                if (isStarted) {
                    setFloatText("非任务页面");
                }
                break;
            case refresh_time:
                if (!TaskExecutor.getInstance().isForcePause()) {
//                    setFloatText("已执行：" + event.getData());
                    setFloatText("任务执行中");
                    if (!FloatWindow.get().isShowing()) {
                        FloatWindow.get().show();
                    }
                }
                break;
            case no_roots_alert:
                TaskExecutor.getInstance().setForcePause(true);
                setFloatText("无法获取界面信息，请重启手机！");
                break;
            case roots_ready:
                TaskExecutor.getInstance().setForcePause(false);
                setFloatText("重新准备就绪");
                break;
            case accessiblity_connected:
                setFloatText("准备就绪，点我去启动");
                break;
        }
    }

    private void initUmeng() {
        try {
//            UMConfigure.init(getApplicationContext(), "你的友盟appid", "main", UMConfigure.DEVICE_TYPE_PHONE, null);
//            MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
        } catch (Exception e) {
        }
    }

    private void initLeancloud() {
        try {
            AVOSCloud.initialize("15IzPzEVyONHdh2Sv6NgaY7N-gzGzoHsz", "FSW0TSuSrQ6sHHLwY4bsIxY7");
//            new InitTask().execute();
        } catch (Exception e) {
            Logger.e(e.getMessage(), e);
        }
    }

    /**
     * Get Display
     *
     * @param context Context for get WindowManager
     * @return Display
     */
    private static Display getDisplay(Context context) {
        WindowManager wm = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        if (wm != null) {
            return wm.getDefaultDisplay();
        } else {
            return null;
        }
    }

    public static MyApplication getAppInstance() {
        return appInstance;
    }

    public MyAccessbilityService getAccessbilityService() {
        return accessbilityService;
    }

    public boolean isAccessbilityServiceReady() {
        return accessbilityService != null;
    }

    public static int getScreenWidth() {
        return screenWidth;
    }

    public static int getScreenHeight() {
        return screenHeight;
    }

    public boolean isVip() {
        return isVip;
    }

    public void setVip(boolean vip) {
        isVip = vip;
    }

    public MainActivity getMainActivity() {
        return mainActivity;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void showFloatWindow() {
        floatView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.floatview, null);
        TextView tv_close = floatView.findViewById(R.id.tv_close);
        TextView tv_option = floatView.findViewById(R.id.tv_option);
        ImageView icon = floatView.findViewById(R.id.icon);
        FloatWindow
                .with(getApplicationContext())
                .setView(floatView)
                .setY(150)
                .setX(0)
                .setFilter(false, MainActivity.class, EditTaskActivity.class, TaskTypeListActivity.class)
                .setMoveType(MoveType.slide)
                .setMoveStyle(500, new BounceInterpolator())
                .setViewStateListener(mViewStateListener)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onSuccess() {
                        Logger.i("悬浮框授权成功");
                    }

                    @Override
                    public void onFail() {
                        Logger.i("悬浮框授权失败");
                    }
                })
                .setDesktopShow(true)
                .build();

        tv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(0);//正常退出App
                Toast.makeText(getApplicationContext(), "退出捡豆子", Toast.LENGTH_LONG).show();
            }
        });


        tv_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isStarted) {
                    if (TaskExecutor.getInstance().isForcePause()) {
                        TaskExecutor.getInstance().setForcePause(false);
                        BusManager.getBus().post(new BusEvent<>(unpause_byhand));
                    } else {
                        LogUtils.d(TAG, "setForcePause()");
                        TaskExecutor.getInstance().setForcePause(true);
                        BusManager.getBus().post(new BusEvent<>(pause_byhand));
                    }
                } else {
                    PackageUtils.startSelf();
                }
            }
        });

        icon.setOnClickListener(v -> {
            TaskExecutor.getInstance().setForcePause(true);
            BusManager.getBus().post(new BusEvent<>(pause_byhand));
            Toast.makeText(getApplicationContext(), "捡豆子已暂停", Toast.LENGTH_LONG).show();
            PackageUtils.startSelf();
        });
    }

    private void setFloatText(String text) {
        if (floatView != null) {
            TextView textView = floatView.findViewById(R.id.tv_option);
            textView.setText(text);
        }
    }

    private ViewStateListener mViewStateListener = new ViewStateListener() {
        @Override
        public void onPositionUpdate(int x, int y) {
            Log.d(TAG, "onPositionUpdate: x=" + x + " y=" + y);
        }

        @Override
        public void onShow() {
            Log.d(TAG, "onShow");
        }

        @Override
        public void onHide() {
            Log.d(TAG, "onHide");
        }

        @Override
        public void onDismiss() {
            Log.d(TAG, "onDismiss");
        }

        @Override
        public void onMoveAnimStart() {
            Log.d(TAG, "onMoveAnimStart");
        }

        @Override
        public void onMoveAnimEnd() {
            Log.d(TAG, "onMoveAnimEnd");
        }

        @Override
        public void onBackToDesktop() {
            Log.d(TAG, "onBackToDesktop");
            FloatWindow.get().show();
        }
    };

    /**
     * 开始执行任务
     */
    public void startTask(AppInfo appInfo) {
        TaskExecutor.getInstance().startTask(appInfo);
    }


}
