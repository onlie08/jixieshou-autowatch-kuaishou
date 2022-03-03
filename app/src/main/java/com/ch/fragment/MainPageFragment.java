package com.ch.fragment;

import android.Manifest;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.ch.activity.AddTaskActivity;
import com.ch.activity.EditTaskActivity;
import com.ch.adapter.TaskListAdapter1;
import com.ch.application.MyApplication;
import com.ch.common.CommonDialogManage;
import com.ch.common.DeviceUtils;
import com.ch.common.DownLoadAppManage;
import com.ch.common.SPService;
import com.ch.core.bus.BusEvent;
import com.ch.core.bus.BusManager;
import com.ch.core.service.MyAccessbilityService;
import com.ch.core.utils.AccessibilityUtils;
import com.ch.core.utils.Constant;
import com.ch.event.AddAllTaskEvent;
import com.ch.event.AddTaskEvent;
import com.ch.event.DelectTaskEvent;
import com.ch.event.RefreshTaskEvent;
import com.ch.floatwindow.PermissionUtil;
import com.ch.jixieshou.BuildConfig;
import com.ch.jixieshou.R;
import com.ch.model.AppInfo;
import com.ch.model.TaskInfo;
import com.ch.scripts.FSRedPackageScript;
import com.ch.scripts.TaskExecutor;
import com.ch.scripts.WXPackageScript;
import com.ch.scripts.WeiXinScript;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.squareup.otto.Subscribe;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.ch.core.bus.EventType.task_finish;
import static com.ch.core.bus.EventType.task_weixin;


public class MainPageFragment extends Fragment {
    private String TAG = this.getClass().getSimpleName();
    private CardView newTaskCardView;
    private CardView listCardView;
    private CardView f_view;
    private RecyclerView taskListView;
    private TextView tv_wx_statue;
    private TextView tv_qq_statue;
    private TextView tv_add_task;
    private TextView tv_acceess_enable;
    private TaskListAdapter1 taskListAdapter1;
    private MaterialButton startBtn;
    private List<AppInfo> appInfos = new ArrayList<>();
    private AppInfo currentAppInfo;
    private boolean accessEnable = false;
    private boolean tasking = false;
    private int currentPos = 0;

    public static MainPageFragment newInstance() {
        Bundle args = new Bundle();
        MainPageFragment fragment = new MainPageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mainpage, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initView(view);
        initData();
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        BusManager.getBus().register(this);
    }

    private void initView(View view) {
        Button btn_skip_task = view.findViewById(R.id.btn_skip_task);
        if(BuildConfig.DEBUG){
            btn_skip_task.setVisibility(View.VISIBLE);
        }

        view.findViewById(R.id.btn_skip_task).setOnClickListener(view14 -> TaskExecutor.getInstance().setAllTime(0));
        view.findViewById(R.id.tv_describe).setOnClickListener(view15 -> {
//                startWXTask();
            playInfo(1);
        });
        view.findViewById(R.id.f_view3).setOnClickListener(view16 -> {
            if (f_view.getVisibility() == View.VISIBLE) {
                gotoAccessSetting();
                return;
            }
            if (tv_wx_statue.getText().equals("未开启")) {
                CommonDialogManage.getSingleton().showWeiXinTipDialog(getActivity(), (dialog, which) -> {
                    tv_wx_statue.setText("已开启");
                    startWeiXinTask();
                });

            } else {
                tv_wx_statue.setText("未开启");
                stopWeiXinTask();
            }

        });
        view.findViewById(R.id.f_view4).setOnClickListener(view17 -> ToastUtils.showLong("程序员开发中。。。"));
        view.findViewById(R.id.tv_clear_task).setOnClickListener(v -> {
            newTaskCardView.setVisibility(View.VISIBLE);
            listCardView.setVisibility(View.GONE);
            appInfos.clear();
            if (null != taskListAdapter1) {
                taskListAdapter1.notifyDataSetChanged();
            }
            saveTaskList();
        });

        tv_wx_statue = view.findViewById(R.id.tv_wx_statue);
        tv_qq_statue = view.findViewById(R.id.tv_qq_statue);
        f_view = view.findViewById(R.id.f_view);
        f_view.setOnClickListener(view1 -> gotoAccessSetting());

        listCardView = view.findViewById(R.id.listCardView);
        newTaskCardView = view.findViewById(R.id.newTaskCardView);
        newTaskCardView.setOnClickListener(view12 -> gotoAddNewTaskActivity());

        tv_acceess_enable = view.findViewById(R.id.tv_acceess_enable);
        tv_add_task = view.findViewById(R.id.tv_add_task);
        tv_add_task.setOnClickListener(view13 -> gotoAddNewTaskActivity());
        taskListView = view.findViewById(R.id.taskListView);
        LinearLayoutManager layout = new LinearLayoutManager(getContext());
        layout.setOrientation(LinearLayoutManager.VERTICAL);//设置为横向排列
        taskListView.setLayoutManager(layout);

        startBtn = view.findViewById(R.id.startBtn);
        startBtn.setOnClickListener(view18 -> {
            if (appInfos.isEmpty()) {
                Toast.makeText(getActivity(), "请选择一个任务", Toast.LENGTH_LONG).show();
                return;
            }

            if (!PermissionUtils.isGranted(PERMISSIONS_REQUEST)) {
                CommonDialogManage.getSingleton().showPermissionFailDialog(getActivity());
                return;
            }

            TaskInfo taskInfo = new TaskInfo();
            taskInfo.setAppInfos(appInfos);
            SPService.put(SPService.SP_HIS_TASK_LIST, taskInfo);

            currentPos = 0;
            currentAppInfo = appInfos.get(currentPos);
            startTask();

        });
    }

    private static String[] PERMISSIONS_REQUEST = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    private void startWeiXinTask() {
        new Thread(() -> {
            AppInfo appInfo = new AppInfo();
            appInfo.setPkgName(Constant.PN_WEI_XIN);
            appInfo.setTaskName("微信抢红包");
            WeiXinScript.getSingleton(appInfo).stop = false;
            WeiXinScript.getSingleton(appInfo).execute();
            BusManager.getBus().post(new BusEvent<>(task_weixin));
        }).start();

    }

    private void stopWeiXinTask() {
//        new Thread(() -> {
        AppInfo appInfo = new AppInfo();
        appInfo.setPkgName(Constant.PN_WEI_XIN);
        appInfo.setTaskName("微信抢红包");
        WeiXinScript.getSingleton(appInfo).stop = true;
//        }).start();

    }

    private void startFengShengTask() {
        new Thread(() -> {
            AppInfo appInfo = new AppInfo();
            appInfo.setPkgName(Constant.PN_FENG_SHENG);
            appInfo.setTaskName("丰声抢红包");
            FSRedPackageScript.getSingleton(appInfo).stop = false;
            FSRedPackageScript.getSingleton(appInfo).execute();
        }).start();

    }

    private void startWXTask() {
        new Thread(() -> {
            AppInfo appInfo = new AppInfo();
            appInfo.setPkgName(Constant.PN_WEI_XIN);
            appInfo.setTaskName("微信抢红包");
            WXPackageScript.getSingleton(appInfo).stop = false;
            WXPackageScript.getSingleton(appInfo).execute();
        }).start();

    }

    private void stopFengShengTask() {
//        new Thread(() -> {
        AppInfo appInfo = new AppInfo();
        appInfo.setPkgName(Constant.PN_FENG_SHENG);
        appInfo.setTaskName("丰声抢红包");
        FSRedPackageScript.getSingleton(appInfo).stop = true;
//        }).start();

    }

    private void startTask() {
        boolean isAppExit = DownLoadAppManage.getSingleton().checkIsAppExit(getActivity(), appInfos);
        if (!isAppExit) {
            return;
        }

        if (!PermissionUtil.checkFloatPermission(getActivity())) {
            Toast.makeText(getActivity(), "没有悬浮框权限，为了保证任务能够持续，请授权", Toast.LENGTH_LONG).show();
            try {
                PermissionUtil.requestOverlayPermission(getActivity());
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return;
        }

        if (!accessEnable) {
            gotoAccessSetting();
            return;
        }
        tasking = true;
        getActivity().startService(new Intent(getActivity(), MyAccessbilityService.class));
        MyApplication.getAppInstance().startTask(currentAppInfo);
    }

    private void gotoAccessSetting() {
        Toast.makeText(getActivity(), "请打开「捡豆子」的辅助服务", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
    }

    private void initData() {
        TaskInfo taskInfo = SPService.get(SPService.SP_HIS_TASK_LIST, TaskInfo.class);
        if (taskInfo == null || taskInfo.getAppInfos() == null || taskInfo.getAppInfos().isEmpty()) {
            //TODO 自动添加已安装的任务
            List<AppInfo> appInfoList = new ArrayList<>();
            for (AppInfo appInfo : MyApplication.appInfos) {
                if (AppUtils.isAppInstalled(appInfo.getPkgName())) {
                    if (appInfo.isFree()) {
                        appInfoList.add(appInfo);
                    } else {
                        if (MyApplication.getAppInstance().isVip()) {
                            appInfoList.add(appInfo);
                        }
                    }
                }
            }
            if (appInfoList.isEmpty()) {
                newTaskCardView.setVisibility(View.VISIBLE);
                listCardView.setVisibility(View.GONE);
                return;
            }
            newTaskCardView.setVisibility(View.GONE);
            listCardView.setVisibility(View.VISIBLE);
            appInfos.addAll(appInfoList);

        } else {
            newTaskCardView.setVisibility(View.GONE);
            listCardView.setVisibility(View.VISIBLE);
            appInfos.addAll(taskInfo.getAppInfos());
        }

        taskListAdapter1 = new TaskListAdapter1(getActivity(), appInfos);
        taskListAdapter1.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                gotoEditTaskActivity(appInfos.get(position));
            }
        });
        taskListView.setAdapter(taskListAdapter1);
        checkAccessEnable();
    }

    private void gotoAddNewTaskActivity() {
        Intent intent = new Intent();
        intent.setClass(getActivity(), AddTaskActivity.class);
        intent.putExtra("type", 1);
        startActivity(intent);
    }

    private void gotoEditTaskActivity(AppInfo appInfo) {
        Intent i = new Intent(getActivity(), EditTaskActivity.class);
        i.putExtra("appInfo", JSON.toJSONString(appInfo));
        startActivity(i);
    }


    @org.greenrobot.eventbus.Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AddTaskEvent event) {
        LogUtils.d(TAG, "AddTaskEvent");
        newTaskCardView.setVisibility(View.GONE);
        listCardView.setVisibility(View.VISIBLE);
//        fab.setVisibility(View.VISIBLE);
        appInfos.add(event.getAppInfo());
        taskListAdapter1.notifyDataSetChanged();
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
    public void onMessageEvent(AddAllTaskEvent event) {
        LogUtils.d(TAG, "onMessageEvent");
        newTaskCardView.setVisibility(View.GONE);
        listCardView.setVisibility(View.VISIBLE);
        List<AppInfo> appInfo = event.getAppInfo();
        appInfos.clear();
        appInfos.addAll(appInfo);
        if (null != taskListAdapter1) {
            taskListAdapter1.notifyDataSetChanged();
        }
        saveTaskList();
    }

    @org.greenrobot.eventbus.Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DelectTaskEvent event) {
        LogUtils.d(TAG, "DelectTaskEvent");
        AppInfo appInfo = event.getAppInfo();
        // 1是删除
        deleteAppInfo(appInfo);
        if (appInfos.isEmpty()) {
            newTaskCardView.setVisibility(View.VISIBLE);
            listCardView.setVisibility(View.GONE);
//            fab.setVisibility(View.GONE);
        }
        saveTaskList();
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
                taskListAdapter1.notifyDataSetChanged();
                break;
            }
        }
    }

    /**
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
                curr.setTaskName(appInfo.getTaskName());
                taskListAdapter1.notifyDataSetChanged();
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
//        SPService.put(SPService.SP_TASK_LIST, taskInfo);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!tasking) {
            checkAccessEnable();
        }
        if (null != taskListAdapter1) {
            taskListAdapter1.notifyDataSetChanged();
        }
    }

    private void checkAccessEnable() {
        accessEnable = AccessibilityUtils.isServiceEnabled(getActivity());
        if (accessEnable) {
            tv_acceess_enable.setText("捡豆子无障碍权限已开启");
            f_view.setVisibility(View.GONE);
            Drawable drawable = getResources().getDrawable(R.drawable.done);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tv_acceess_enable.setCompoundDrawables(drawable, null, null, null);
        } else {
            tv_acceess_enable.setText("请先点击此处，去激活无障碍权限");
            f_view.setVisibility(View.VISIBLE);
            Drawable drawable = getResources().getDrawable(R.drawable.warming);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tv_acceess_enable.setCompoundDrawables(drawable, null, null, null);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        BusManager.getBus().unregister(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void playInfo(int type) {
        Uri uri = null;
        switch (type) {
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
            case 5:
                uri = Uri.parse("https://v.kuaishouapp.com/s/kMLqIurI");
                break;
            case 6:
                uri = Uri.parse("https://promotion-waimai.meituan.com/invite/r2x/coupon/?inviteCode=NnOIp-QOs8SiYF1dcSlL5r8phPrCf6qkH7evMyjIoureqol0OXXaopfjjblE0yPgVDQI9oO7zzULG0YhAlZWjSBHCU5Sg8wPJ54uw3IJOTKxyYNrSDuyNENpsOQvFoGQVLxrwXj_hojaGSHcn87IUTjane8UmtDBPyRXIs_GLNk&lq_source=2");
                break;
            case 7:
                uri = Uri.parse("https://h5.ele.me/ant/qrcode2?open_type=miniapp&url_id=35&inviterId=3b72f5fa&actId=1&_ltracker_f=hjb_app_jgwzfb&chInfo=ch_share__chsub_CopyLink&apshareid=7816ec01-60af-46db-8640-4f8ccf3b4b7d");
                break;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        startActivity(intent);

    }

    @Subscribe
    public void subscribeEvent(BusEvent event) {
        switch (event.getType()) {
            case task_finish:
                Log.d(TAG, "当前任务完成");
                new Handler().postDelayed(() -> {

                    LogUtils.d(TAG, "当前任务：" + new Gson().toJson(currentAppInfo));

                    checkIfNewDay();

                    checkIfAllTaskDone();

                    currentAppInfo = getNextUnDoneTask();

                    LogUtils.d(TAG, "下一个任务：" + new Gson().toJson(currentAppInfo));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startTask();
                        }
                    }, 1000);
                }, 1000);
                break;
        }
    }

    /**
     * 如果今天所有任务都已完成 重置状态接着做
     */
    private void checkIfAllTaskDone() {
        boolean allTaskDone = true;
        for (AppInfo appInfo : appInfos) {
            if (!appInfo.isTodayDone()) {
                allTaskDone = false;
            }
        }

        if (allTaskDone) {
            for (AppInfo appInfo : appInfos) {
                appInfo.setTodayDone(false);
            }
        }
    }


    /**
     * 是否转点的一天
     */
    private void checkIfNewDay() {
        boolean isNewDayDone = SPUtils.getInstance().getBoolean(DeviceUtils.getToday());

        if (!isNewDayDone) {
            for (AppInfo appInfo : appInfos) {
                appInfo.setTodayDone(false);
            }
            SPUtils.getInstance().put(DeviceUtils.getToday(), true);
        }

    }

    /*
    查找下一个未完成的任务
     */
    private AppInfo getNextUnDoneTask() {
        AppInfo info;
        if (currentPos == (appInfos.size() - 1)) {
            currentPos = -1;
            info = getNextUnDoneTask();
        } else {
            currentPos++;
            if (appInfos.get(currentPos).isTodayDone()) {
                info = getNextUnDoneTask();
            } else {
                info = appInfos.get(currentPos);
            }
        }
        return info;
    }
}
