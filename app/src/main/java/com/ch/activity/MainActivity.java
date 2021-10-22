package com.ch.activity;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.ch.adapter.TaskListAdapter;
import com.ch.application.MyApplication;
import com.ch.common.CommonDialogManage;
import com.ch.common.DownLoadAppManage;
import com.ch.common.PerMissionManage;
import com.ch.common.RecognitionManage;
import com.ch.common.SPService;
import com.ch.core.bus.BusEvent;
import com.ch.core.bus.BusManager;
import com.ch.core.service.MyAccessbilityService;
import com.ch.core.utils.AccessibilityUtils;
import com.ch.core.utils.Constant;
import com.ch.core.utils.SFUpdaterUtils;
import com.ch.core.utils.Utils;
import com.ch.event.AddTaskEvent;
import com.ch.event.DelectTaskEvent;
import com.ch.event.RefreshTaskEvent;
import com.ch.floatwindow.PermissionUtil;
import com.ch.jixieshou.R;
import com.ch.model.AppInfo;
import com.ch.model.ScreenShootEvet;
import com.ch.model.TaskInfo;
import com.ch.utils.LogcatFileManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.otto.Subscribe;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.ThreadMode;

import static android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_TAKE_SCREENSHOT;
import static com.ch.core.bus.EventType.task_finish;
import static com.ch.core.utils.BaseUtil.showRecommendDialog;
import static com.umeng.socialize.utils.DeviceConfigInternal.context;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private LinearLayout taskListLayout;
    private CardView cardView;
    private TextView tv_version;
    private ListView taskListView;
    private FloatingActionButton fab;
    private TaskListAdapter taskListAdapter;
    private MaterialButton startBtn;
    private MaterialButton btnShare;
    private List<AppInfo> appInfos = new ArrayList<>();
    private List<AppInfo> currentAppInfos = new ArrayList<>();

    boolean permission = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        BarUtils.setStatusBarColor(this, getResources().getColor(R.color.colorPrimary));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyApplication.getAppInstance().setMainActivity(this);
        EventBus.getDefault().register(this);
        permission = PerMissionManage.getSingleton().requestPermission(MainActivity.this);
        SFUpdaterUtils.checkVersion(this);
        BusManager.getBus().register(this);

        initView();
        initData();
        taskListView.setAdapter(taskListAdapter = new TaskListAdapter(this, appInfos));

        try {
            LogcatFileManager.getInstance().start(Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator + "01JianDouZi" + File.separator + "logcat");
        } catch (Exception e) {
            LogUtils.e(TAG,e.getMessage());
        }

    }

    private void initView() {
        tv_version = findViewById(R.id.tv_version);
        tv_version.setText("V"+AppUtils.getAppVersionName());
        tv_version.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                playInfo();
                SFUpdaterUtils.checkVersion(MainActivity.this);
            }
        });

        taskListLayout = findViewById(R.id.taskListLayout);
        cardView = findViewById(R.id.newTaskCardView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoAddNewTaskActivity();
            }
        });
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoAddNewTaskActivity();
            }
        });
        taskListView = findViewById(R.id.taskListView);
        taskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                gotoEditTaskActivity(taskListAdapter.getItem(i));
            }
        });

        btnShare = findViewById(R.id.btn_share);
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showRecommendDialog(Constant.PN_DOU_YIN,MainActivity.this);
                CommonDialogManage.getSingleton().showShareAppDilaog(MainActivity.this);
            }
        });

        startBtn = findViewById(R.id.startBtn);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentAppInfos.clear();
                currentAppInfos.addAll(appInfos);
                startTask();
            }
        });

        TextView textView = findViewById(R.id.deviceNo);
        textView.setText("设备号：" + EncryptUtils.encryptMD5ToString(DeviceUtils.getMacAddress()));

        findViewById(R.id.tv_describe_one_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playInfo(1);
            }
        });

        findViewById(R.id.tv_describe_two_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playInfo(2);
            }
        });

        findViewById(R.id.tv_describe_thire_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playInfo(3);
            }
        });

        findViewById(R.id.tv_describe_four_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playInfo(4);
            }
        });

    }

    private void setData() {
        if(!currentAppInfos.isEmpty()){
            TaskInfo taskInfo = new TaskInfo();
            taskInfo.setAppInfos(currentAppInfos);
            SPService.put(SPService.SP_TASK_LIST, taskInfo);
            return;
        }
        List<AppInfo> appInfos = new ArrayList<>();

        AppInfo appInfo = new AppInfo();
//        appInfo.setAppName("抖音极速版");
//        appInfo.setName("抖音极速版");
//        appInfo.setFree(true);
//        appInfo.setPeriod(4l);
//        appInfo.setPkgName(Constant.PN_DOU_YIN);
//        appInfos.add(appInfo);
//
        appInfo = new AppInfo();
        appInfo.setAppName("今日头条极速版");
        appInfo.setName("今日头条极速版");
        appInfo.setFree(true);
        appInfo.setPeriod(4l);
        appInfo.setPkgName(Constant.PN_TOU_TIAO);
        appInfos.add(appInfo);
//
//        appInfo = new AppInfo();
//        appInfo.setAppName("快手极速版");
//        appInfo.setName("快手极速版");
//        appInfo.setFree(true);
//        appInfo.setPeriod(4l);
//        appInfo.setPkgName(Constant.PN_KUAI_SHOU);
//
//        appInfos.add(appInfo);
//        appInfo = new AppInfo();
//        appInfo.setAppName("点淘");
//        appInfo.setName("点淘");
//        appInfo.setFree(true);
//        appInfo.setPeriod(4l);
//        appInfo.setPkgName(Constant.PN_DIAN_TAO);
//
//        appInfos.add(appInfo);
//        appInfo = new AppInfo();
//        appInfo.setAppName("爱奇艺极速版");
//        appInfo.setName("爱奇艺极速版");
//        appInfo.setFree(true);
//        appInfo.setPeriod(4l);
//        appInfo.setPkgName(Constant.PN_AI_QI_YI);
//        appInfos.add(appInfo);

//        appInfo = new AppInfo();
//        appInfo.setAppName("百度极速版");
//        appInfo.setName("百度极速版");
//        appInfo.setFree(true);
//        appInfo.setPeriod(4l);
//        appInfo.setPkgName(Constant.PN_BAI_DU);
//        appInfos.add(appInfo);

        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setAppInfos(appInfos);
        SPService.put(SPService.SP_TASK_LIST, taskInfo);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initData() {
        TaskInfo hisTaskInfo = SPService.get(SPService.SP_TASK_LIST, TaskInfo.class);
        if (hisTaskInfo == null || hisTaskInfo.getAppInfos() == null || hisTaskInfo.getAppInfos().isEmpty()) {
            setData();
        }

        TaskInfo taskInfo = SPService.get(SPService.SP_TASK_LIST, TaskInfo.class);
        if (taskInfo == null || taskInfo.getAppInfos() == null || taskInfo.getAppInfos().isEmpty()) {
            cardView.setVisibility(View.VISIBLE);
            taskListLayout.setVisibility(View.GONE);

            fab.setVisibility(View.GONE);
        } else {
            cardView.setVisibility(View.GONE);
            taskListLayout.setVisibility(View.VISIBLE);
            fab.setVisibility(View.VISIBLE);
            appInfos.addAll(taskInfo.getAppInfos());
//            taskListAdapter.notifyDataSetChanged();
        }

    }

    private void gotoAddNewTaskActivity() {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, TaskTypeListActivity.class);
        intent.putExtra("type", 1);
        startActivity(intent);
    }

    private void gotoEditTaskActivity(AppInfo appInfo) {
        Intent i = new Intent(this, EditTaskActivity.class);
        i.putExtra("appInfo", JSON.toJSONString(appInfo));
        startActivity(i);
//        startActivityForResult(i, 101);
    }

    /**
     * 删除某个任务
     *
     * @param appInfo
     */
    private void deleteAppInfo(AppInfo appInfo) {
        for (int i = 0; i < appInfos.size(); i++) {
            if (appInfo.getUuid().equals(appInfos.get(i).getUuid())) {
                appInfos.remove(i);
                taskListAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    /**
     * 删除某个任务
     *
     * @param uuid    替换某任务
     * @param appInfo
     */
    private void updateAppInfo(String uuid, AppInfo appInfo) {
        for (int i = 0; i < appInfos.size(); i++) {
            AppInfo curr = appInfos.get(i);
            if (uuid.equals(curr.getUuid())) {
                curr.setFree(appInfo.isFree());
                curr.setPkgName(appInfo.getPkgName());
                curr.setPeriod(appInfo.getPeriod());
                curr.setIcon(appInfo.getIcon());
                curr.setName(appInfo.getName());
                taskListAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    /**
     * 保存任务
     */
    private void saveTaskList() {
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setAppInfos(appInfos);
        SPService.put(SPService.SP_TASK_LIST, taskInfo);
    }

    @Subscribe
    public void subscribeEvent(BusEvent event) {
        switch (event.getType()) {
            case task_finish:
                Log.d(TAG, "当前任务完成");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AppInfo appInfo = (AppInfo) event.getData();
                        TaskInfo taskInfo1 = SPService.get(SPService.SP_TASK_LIST, TaskInfo.class);
                        List<AppInfo> appInfoList = taskInfo1.getAppInfos();
                        for(int i=0;i<appInfoList.size();i++){
                            if(appInfoList.get(i).getPkgName().equals(appInfo.getPkgName())){
                                appInfoList.remove(i);
                                Log.d(TAG, "移除当前任务");
                                break;
                            }
                        }
//                        Iterator<AppInfo> iterator = appInfoList.iterator();
//                        while (iterator.hasNext()) {
//                            if (iterator.next().getPkgName().equals(appInfo.getPkgName())) {
//                                iterator.remove();
//                                Log.d(TAG, "移除当前任务");
//                            }
//                        }
                        SPService.put(SPService.SP_TASK_LIST, taskInfo1);

                        if (taskInfo1 == null || taskInfo1.getAppInfos() == null || taskInfo1.getAppInfos().isEmpty()) {

//                            cardView.setVisibility(View.VISIBLE);
//                            fab.setVisibility(View.GONE);
//                            appInfos.clear();
//                            taskListAdapter.notifyDataSetChanged();

                            cardView.setVisibility(View.GONE);
                            taskListLayout.setVisibility(View.VISIBLE);
                            fab.setVisibility(View.VISIBLE);
                            setData();
                            TaskInfo taskInfo = SPService.get(SPService.SP_TASK_LIST, TaskInfo.class);
                            appInfos.clear();
                            appInfos.addAll(taskInfo.getAppInfos());
                            taskListAdapter.notifyDataSetChanged();
                        } else {
                            cardView.setVisibility(View.GONE);
                            taskListLayout.setVisibility(View.VISIBLE);
                            fab.setVisibility(View.VISIBLE);
                            appInfos.clear();
                            appInfos.addAll(taskInfo1.getAppInfos());
                            taskListAdapter.notifyDataSetChanged();
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startTask();
                            }
                        }, 2000);
                    }
                }, 2000);
                break;
        }
    }

    private void startTask() {
        if (appInfos.isEmpty()) {
            Toast.makeText(getApplicationContext(), "请选择一个任务", Toast.LENGTH_LONG).show();
            return;
        }

        boolean isAppExit = DownLoadAppManage.getSingleton().checkIsAppExit(MainActivity.this, appInfos);
        if (!isAppExit) {
            return;
        }

        if (!PermissionUtil.checkFloatPermission(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "没有悬浮框权限，为了保证任务能够持续，请授权", Toast.LENGTH_LONG).show();
            try {
                PermissionUtil.requestOverlayPermission(MainActivity.this);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return;
        }

        // 判断是否开启辅助服务
//        if (!AccessibilityUtils.isAccessibilitySettingsOn(getApplicationContext())) {
//            Toast.makeText(getApplicationContext(), "请打开「捡豆子」的辅助服务", Toast.LENGTH_LONG).show();
//            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
//            startActivity(intent);
//            return;
//        }
        if(!isServiceEnabled()){
            Toast.makeText(getApplicationContext(), "请打开「捡豆子」的辅助服务", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
            return;
        }

//        if(!isServiceEnabled()){
            startService(new Intent(getApplicationContext(), MyAccessbilityService.class));
//        }

        MyApplication.getAppInstance().startTask(appInfos);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    permission = true;
                } else {
                    Toast.makeText(this, "软件退出，运行权限被禁止", Toast.LENGTH_SHORT).show();
                    Log.i("=======================", "权限" + permissions[i] + "申请失败");
                    permission = false;
                    System.exit(0);
                }
            }
        }
    }


    @org.greenrobot.eventbus.Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ScreenShootEvet event) {
        LogUtils.d(TAG, "ScreenShootEvet");
        boolean success = MyApplication.getAppInstance().getAccessbilityService().performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT);
        if (!success) {
            CrashReport.postCatchedException(new Throwable("截图失败"));
            //个人中心弹出框去赚钱坐标范围[204,1335][876,1455]
            return;
        }
        Utils.sleep(1000);
        if (!permission) {
            Toast.makeText(getApplicationContext(), "读写权限被拒绝，请重启软件并允许权限", Toast.LENGTH_LONG).show();
            return;
        }
        RecognitionManage.getSingleton().getScreemPicFile(event.getPackageName(), event.getPageId());
    }

    private long mExitTime;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                ToastUtils.showLong("再按一次退出程序");
                mExitTime = System.currentTimeMillis();
            } else {
                CommonDialogManage.getSingleton().showExitDialog(MainActivity.this);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @org.greenrobot.eventbus.Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AddTaskEvent event) {
        LogUtils.d(TAG, "AddTaskEvent");
        cardView.setVisibility(View.GONE);
        taskListLayout.setVisibility(View.VISIBLE);
        fab.setVisibility(View.VISIBLE);
        appInfos.add(event.getAppInfo());
        taskListAdapter.notifyDataSetChanged();
        saveTaskList();
    }

    @org.greenrobot.eventbus.Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RefreshTaskEvent event) {
        LogUtils.d(TAG, "onMessageEvent");
        AppInfo appInfo = event.getAppInfo();
        // 2是编辑
        AppInfo editedAppInfo = event.getEditedAppInfo();
        updateAppInfo(editedAppInfo.getUuid(), appInfo);
        saveTaskList();
    }

    @org.greenrobot.eventbus.Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DelectTaskEvent event) {
        LogUtils.d(TAG, "DelectTaskEvent");
        AppInfo appInfo = event.getAppInfo();
        // 1是删除
        deleteAppInfo(appInfo);
        if (appInfos.isEmpty()) {
            cardView.setVisibility(View.VISIBLE);
            taskListLayout.setVisibility(View.GONE);
            fab.setVisibility(View.GONE);
        }
        saveTaskList();
    }

    //检查服务是否开启
    private boolean isServiceEnabled() {
        AccessibilityManager accessibilityManager = (AccessibilityManager)getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> accessibilityServices =
                accessibilityManager.getEnabledAccessibilityServiceList(
                        AccessibilityServiceInfo.FEEDBACK_ALL_MASK);
        for (AccessibilityServiceInfo info : accessibilityServices) {
            if (info.getId().contains("com.ch.core.service.MyAccessbilityService")) {
                return true;
            }
        }
        return false;
    }

    private void playInfo(int type){
        Uri uri = null;
        switch (type){
            case 1:
                uri = Uri.parse("https://v.kuaishouapp.com/s/ofnbLIJT");
                break;
            case 2:
                uri = Uri.parse("https://v.kuaishouapp.com/s/4NIVDpUa");
                break;
            case 3:
                uri = Uri.parse("https://v.kuaishouapp.com/s/49IDnusm");
                break;
            case 4:
                uri = Uri.parse("https://v.kuaishouapp.com/s/kMLqIurI");
                break;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData (uri);
        startActivity(intent);

    }

}
