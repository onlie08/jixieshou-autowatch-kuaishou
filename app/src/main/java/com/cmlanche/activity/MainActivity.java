package com.cmlanche.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Process;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.ClipboardUtils;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.cmlanche.adapter.TaskListAdapter;
import com.cmlanche.application.MyApplication;
import com.cmlanche.common.SPService;
import com.cmlanche.core.bus.BusEvent;
import com.cmlanche.core.bus.BusManager;
import com.cmlanche.core.service.MyAccessbilityService;
import com.cmlanche.core.utils.AccessibilityUtils;
import com.cmlanche.core.utils.BaseUtil;
import com.cmlanche.core.utils.Constant;
import com.cmlanche.core.utils.SFUpdaterUtils;
import com.cmlanche.core.utils.Utils;
import com.cmlanche.floatwindow.PermissionUtil;
import com.cmlanche.jixieshou.R;
import com.cmlanche.model.AppInfo;
import com.cmlanche.model.InviteEvent;
import com.cmlanche.model.RecognitionBean;
import com.cmlanche.model.ScreenShootEvet;
import com.cmlanche.model.TaskInfo;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.squareup.otto.Subscribe;
import com.tencent.bugly.crashreport.CrashReport;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.ThreadMode;

import static android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_TAKE_SCREENSHOT;
import static com.cmlanche.core.bus.EventType.task_finish;
import static com.cmlanche.core.utils.Constant.PN_DIAN_TAO;
import static com.cmlanche.core.utils.Constant.PN_DOU_YIN;
import static com.cmlanche.core.utils.Constant.PN_FENG_SHENG;
import static com.cmlanche.core.utils.Constant.PN_KUAI_SHOU;
import static com.cmlanche.core.utils.Constant.PN_TOU_TIAO;
import static com.cmlanche.core.utils.Constant.PN_YING_KE;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private CardView cardView;
    private ListView taskListView;
    private FloatingActionButton fab;
    private TaskListAdapter taskListAdapter;
    private MaterialButton startBtn;
    private MaterialButton btnShare;
    private TextView descriptionView;
    private List<AppInfo> appInfos = new ArrayList<>();
    private boolean isInstallKuaiShou = false;
    private boolean isInstallFengSheng = false;
    private boolean isInstallDouyin = false;
    private boolean isInstallTouTiao = false;
    private boolean isInstallDianTao = false;
    private boolean isInstallYingKe = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        BarUtils.setStatusBarColor(this, getResources().getColor(R.color.colorPrimary));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyApplication.getAppInstance().setMainActivity(this);
        EventBus.getDefault().register(this);
        requestPermission();
        SFUpdaterUtils.checkVersion(this);
        BusManager.getBus().register(this);
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
        taskListView.setAdapter(taskListAdapter = new TaskListAdapter(this, appInfos));
        taskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                gotoEditTaskActivity(taskListAdapter.getItem(i));
            }
        });
        descriptionView = findViewById(R.id.description);

        btnShare = findViewById(R.id.btn_share);
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShareAppDilaog();
            }
        });

        startBtn = findViewById(R.id.startBtn);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTask();
            }
        });

        TextView textView = findViewById(R.id.deviceNo);
        textView.setText("设备号：" + EncryptUtils.encryptMD5ToString(DeviceUtils.getMacAddress()));

        TaskInfo hisTaskInfo = SPService.get(SPService.SP_TASK_LIST, TaskInfo.class);
        if (hisTaskInfo == null || hisTaskInfo.getAppInfos() == null || hisTaskInfo.getAppInfos().isEmpty()) {
            setData();
        }
        this.initData();
        String ky1 = SPUtils.getInstance().getString("key1xy","");
        if(!TextUtils.isEmpty(ky1)){
            RecognitionBean recognitionBean = new Gson().fromJson(ky1,RecognitionBean.class);
            if(null != recognitionBean){
                Point p0 = new Point();
                p0.x = (recognitionBean.getP1().x + recognitionBean.getP3().x)/2;
                p0.y = (recognitionBean.getP1().y + recognitionBean.getP3().y)/2;
                MyApplication.KEY_XY1 = p0;
            }
        }
    }

    private void setData() {
        List<AppInfo> appInfos = new ArrayList<>();

//        AppInfo appInfo = new AppInfo();
//        appInfo.setAppName("抖音极速版");
//        appInfo.setName("抖音极速版");
//        appInfo.setFree(true);
//        appInfo.setPeriod(4l);
//        appInfo.setPkgName(Constant.PN_DOU_YIN);
//        appInfos.add(appInfo);

        AppInfo appInfo = new AppInfo();
        appInfo.setAppName("今日头条极速版");
        appInfo.setName("今日头条极速版");
        appInfo.setFree(true);
        appInfo.setPeriod(8l);
        appInfo.setPkgName(Constant.PN_TOU_TIAO);
        appInfos.add(appInfo);

//        appInfo = new AppInfo();
//        appInfo.setAppName("快手极速版");
//        appInfo.setName("快手极速版");
//        appInfo.setFree(true);
//        appInfo.setPeriod(4l);
//        appInfo.setPkgName(Constant.PN_KUAI_SHOU);
//        appInfos.add(appInfo);

        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setAppInfos(appInfos);
        SPService.put(SPService.SP_TASK_LIST, taskInfo);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isInstallFengSheng = BaseUtil.isInstallPackage(PN_FENG_SHENG);
        isInstallKuaiShou = BaseUtil.isInstallPackage(PN_KUAI_SHOU);
        isInstallDouyin = BaseUtil.isInstallPackage(PN_DOU_YIN);
        isInstallTouTiao = BaseUtil.isInstallPackage(PN_TOU_TIAO);
        isInstallDianTao = BaseUtil.isInstallPackage(PN_DIAN_TAO);
        isInstallYingKe = BaseUtil.isInstallPackage(PN_YING_KE);
    }

    private void initData() {

        TaskInfo taskInfo = SPService.get(SPService.SP_TASK_LIST, TaskInfo.class);
        if (taskInfo == null || taskInfo.getAppInfos() == null || taskInfo.getAppInfos().isEmpty()) {
            cardView.setVisibility(View.VISIBLE);
            fab.setVisibility(View.GONE);
        } else {
            cardView.setVisibility(View.GONE);
            fab.setVisibility(View.VISIBLE);
            appInfos.addAll(taskInfo.getAppInfos());
            taskListAdapter.notifyDataSetChanged();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == 1) {
            // 100是新增任务
            AppInfo appInfo = JSON.parseObject(data.getStringExtra("appInfo"), AppInfo.class);
            cardView.setVisibility(View.GONE);
            fab.setVisibility(View.VISIBLE);
            appInfos.add(appInfo);
            taskListAdapter.notifyDataSetChanged();
            saveTaskList();
        }

        // 编辑任务成功
        if (requestCode == 101) {
            // 101是更新
            if (resultCode == 1) {
                AppInfo appInfo = JSON.parseObject(data.getStringExtra("appInfo"), AppInfo.class);
                // 1是删除
                deleteAppInfo(appInfo);
                if (appInfos.isEmpty()) {
                    cardView.setVisibility(View.VISIBLE);
                    fab.setVisibility(View.GONE);
                }
                saveTaskList();
            } else if (resultCode == 2) {
                AppInfo appInfo = JSON.parseObject(data.getStringExtra("appInfo"), AppInfo.class);
                // 2是编辑
                AppInfo editedAppInfo = JSON.parseObject(data.getStringExtra("editedAppInfo"), AppInfo.class);
                updateAppInfo(editedAppInfo.getUuid(), appInfo);
                saveTaskList();
            }
        }
    }

    private void gotoAddNewTaskActivity() {
        startActivityForResult(new Intent(this, NewOrEditTaskActivity.class), 100);
    }

    private void gotoEditTaskActivity(AppInfo appInfo) {
        Intent i = new Intent(this, NewOrEditTaskActivity.class);
        i.putExtra("appInfo", JSON.toJSONString(appInfo));
        startActivityForResult(i, 101);
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


    protected boolean isAppExist(String pkgName) {
        ApplicationInfo info;
        try {
            info = getPackageManager().getApplicationInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            info = null;
        }
        return info != null;
    }

    private void showExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("注意");
        builder.setMessage("确定要退出捡豆任务吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.exit(0);//正常退出App
                Toast.makeText(getApplicationContext(), "退出捡豆子", Toast.LENGTH_LONG).show();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showShareAppDilaog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View inflate = LayoutInflater.from(this).inflate(R.layout.dialog_share_app, null);
        builder.setView(inflate);
        AlertDialog dialog = builder.create();
        dialog.show();
        MaterialButton btnColse = inflate.findViewById(R.id.btn_colse);
        MaterialButton btn_copy = inflate.findViewById(R.id.btn_copy);
        btnColse.setOnClickListener(v -> dialog.dismiss());
        btn_copy.setOnClickListener(v -> {
            //获取剪贴板管理器：
            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            // 创建普通字符型ClipData
            ClipData mClipData = ClipData.newPlainText("Label", "https://marm-core.sf-express.com/app-download/2e69975d7e0348009687c4cbf7bcf954");
            // 将ClipData内容放到系统剪贴板里。
            cm.setPrimaryClip(mClipData);
            Toast.makeText(this, "下载链接已复制,可粘贴微信QQ进行分享", Toast.LENGTH_LONG).show();
            dialog.dismiss();
        });
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
                        Iterator<AppInfo> iterator = appInfoList.iterator();
                        while (iterator.hasNext()) {
                            if (iterator.next().getPkgName().equals(appInfo.getPkgName())) {
                                iterator.remove();
                                Log.d(TAG, "移除当前任务");
                            }
                        }
                        SPService.put(SPService.SP_TASK_LIST, taskInfo1);

                        if (taskInfo1 == null || taskInfo1.getAppInfos() == null || taskInfo1.getAppInfos().isEmpty()) {

//                            cardView.setVisibility(View.VISIBLE);
//                            fab.setVisibility(View.GONE);
//                            appInfos.clear();
//                            taskListAdapter.notifyDataSetChanged();

                            cardView.setVisibility(View.GONE);
                            fab.setVisibility(View.VISIBLE);
                            setData();
                            TaskInfo taskInfo = SPService.get(SPService.SP_TASK_LIST, TaskInfo.class);
                            appInfos.clear();
                            appInfos.addAll(taskInfo.getAppInfos());
                            taskListAdapter.notifyDataSetChanged();
                        } else {
                            cardView.setVisibility(View.GONE);
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

        for (AppInfo appInfo : appInfos) {
            if (appInfo.getPkgName().equals(Constant.PN_KUAI_SHOU)) {
                if (!isInstallKuaiShou) {
                    BaseUtil.showDownLoadDialog(PN_KUAI_SHOU, MainActivity.this);
                    return;
                }

            }
            if (appInfo.getPkgName().equals(Constant.PN_YING_KE)) {
                if (!isInstallYingKe) {
                    BaseUtil.showDownLoadDialog(PN_YING_KE, MainActivity.this);
                    return;
                }

            }
            if (appInfo.getPkgName().equals(Constant.PN_FENG_SHENG)) {
                if (!isInstallFengSheng) {
                    BaseUtil.showDownLoadDialog(PN_FENG_SHENG, MainActivity.this);
                    return;
                }

            } else if (appInfo.getPkgName().equals(Constant.PN_DOU_YIN)) {
                if (!isInstallDouyin) {
                    BaseUtil.showDownLoadDialog(PN_DOU_YIN, MainActivity.this);
                    return;
                }

            } else if (appInfo.getPkgName().equals(Constant.PN_TOU_TIAO)) {
                if (!isInstallTouTiao) {
                    BaseUtil.showDownLoadDialog(PN_TOU_TIAO, MainActivity.this);
                    return;
                }

            } else if (appInfo.getPkgName().equals(Constant.PN_DIAN_TAO)) {
                if (!isInstallDianTao) {
                    BaseUtil.showDownLoadDialog(PN_DIAN_TAO, MainActivity.this);
                    return;
                }

            }
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
        if (!AccessibilityUtils.isAccessibilitySettingsOn(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "请打开「捡豆子」的辅助服务", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
            return;
        }

        startService(new Intent(getApplicationContext(), MyAccessbilityService.class));
        MyApplication.getAppInstance().startTask(appInfos);
    }

    private static String[] PERMISSIONS_REQUEST = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    boolean permission = false;

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (this.checkPermission(Manifest.permission.READ_PHONE_STATE, Process.myPid(), Process.myUid())
                    != PackageManager.PERMISSION_GRANTED || this.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Process.myPid(), Process.myUid())
                    != PackageManager.PERMISSION_GRANTED || this.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, Process.myPid(), Process.myUid())
                    != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(PERMISSIONS_REQUEST, 1);
            } else {
                permission = true;
            }
        } else {
            permission = true;
        }
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
    public void onMessageEvent(InviteEvent event) {
        LogUtils.d(TAG, "ScreenShootEvet");
        switch (event.getAppType()){
            case 1:
                ClipboardUtils.copyText("1634396786");
                break;
            case 2:
                ClipboardUtils.copyText("446859698");
                break;
            case 3:
                ClipboardUtils.copyText("8161779848");
                break;
            case 4:
                ClipboardUtils.copyText("LRHN7T5O");
                break;
        }
        boolean success = MyApplication.getAppInstance().getAccessbilityService().performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT);
        if(!success){
            CrashReport.postCatchedException(new Throwable("填写验证码截图失败"));
           return;
        }
        Utils.sleep(1000);
        getScreemPicFile();
    }

    @org.greenrobot.eventbus.Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ScreenShootEvet event) {
        LogUtils.d(TAG, "ScreenShootEvet");

        boolean success = MyApplication.getAppInstance().getAccessbilityService().performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT);
        if(!success){
            CrashReport.postCatchedException(new Throwable("截图失败"));
            //[750,1758][1038,2049]
            Point point = new Point();
            point.x = 850;
            point.y = 2000;
            MyApplication.KEY_XY1 = point;

            Point point1 = new Point();
            point1.x = 500;
            point1.y = 1400;
            MyApplication.KEY_XY2 = point1;
            //个人中心弹出框去赚钱坐标范围[204,1335][876,1455]
            return;
        }
        Utils.sleep(1000);
        getScreemPicFile();

    }

    private void getScreemPicFile(){
        if (permission) {
            String dicmPath1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + File.separator + "Screenshots";
            List<File> fileList1 = FileUtils.listFilesInDir(dicmPath1);
            if(fileList1 != null && fileList1.size()>0){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        startRecognition(fileList1.get(fileList1.size()-1).getPath());
                    }
                }).start();
            }else {
                String dicmPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + File.separator + "Screenshots";
                List<File> fileList = FileUtils.listFilesInDir(dicmPath);
                if(fileList != null && fileList.size()>0){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            startRecognition(fileList.get(fileList.size()-1).getPath());
                        }
                    }).start();
                }
            }
        }
    }

    private void startRecognition(String photoPath) {
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.authenticator();

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("type", "det_and_rec");

        File file = new File(photoPath);
        builder.addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("image/jpeg"), file));

        //构建请求体
        Request request = new Request.Builder()
                .url("https://bcpapi.sense-map.cn/ppdocr/recognition?ak=IkSrNfvbmmLOWzgG218HzYin")
                .post(builder.build())
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                LogUtils.d(TAG,"Recognition onFailure");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response != null) {
                    if (response.isSuccessful()) {
                        //打印服务端返回结果
                        final String res = response.body().string();
                        LogUtils.d(TAG,"Recognition Successful: "+res);
                        List<RecognitionBean> recognitionBeans = new ArrayList<>();

                        Log.d(TAG, "onResponse: " + res);
                        try {
                            JSONObject jsonObject = new JSONObject(res);
                            // 返回json的数组
                            JSONArray jsonArray = jsonObject.getJSONArray("results");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                RecognitionBean recognitionBean = new RecognitionBean();
                                String point = jsonArray.getJSONArray(i).getString(0);
                                String point1 = point.substring(1, point.length() - 1);
                                String[] point2 = point1.split("],");

                                Point p1 = new Point();
                                String point3 = point2[0].substring(1, point2[0].length());
                                String[] point4 = point3.split(",");
                                p1.x = Integer.parseInt(point4[0]);
                                p1.y = Integer.parseInt(point4[1]);
                                recognitionBean.setP1(p1);

                                Point p2 = new Point();
                                String point5 = point2[1].substring(1, point2[1].length());
                                String[] point6 = point5.split(",");
                                p2.x = Integer.parseInt(point6[0]);
                                p2.y = Integer.parseInt(point6[1]);
                                recognitionBean.setP2(p2);

                                Point p3 = new Point();
                                String point7 = point2[2].substring(1, point2[2].length());
                                String[] point8 = point7.split(",");
                                p3.x = Integer.parseInt(point8[0]);
                                p3.y = Integer.parseInt(point8[1]);
                                recognitionBean.setP3(p3);

                                Point p4 = new Point();
                                String point9 = point2[3].substring(1, point2[3].length() - 1);
                                String[] point10 = point9.split(",");
                                p4.x = Integer.parseInt(point10[0]);
                                p4.y = Integer.parseInt(point10[1]);
                                recognitionBean.setP4(p4);

                                String result = jsonArray.getJSONArray(i).getString(1);
                                LogUtils.d(TAG,result);
                                String probability = jsonArray.getJSONArray(i).getString(2);
                                recognitionBean.setRes(result);
                                recognitionBean.setProbability(probability);

                                recognitionBeans.add(recognitionBean);
                                if(result.equals("开宝箱得金币")){
                                    Point p0 = new Point();
                                    p0.x = (recognitionBean.getP1().x + recognitionBean.getP3().x)/2;
                                    p0.y = (recognitionBean.getP1().y + recognitionBean.getP3().y)/2;
                                    MyApplication.KEY_XY1 = p0;
                                    SPUtils.getInstance().put("key1xy",new Gson().toJson(recognitionBean));
                                }
                                if(result.equals("開")){
                                    SPUtils.getInstance().put("key2xy",new Gson().toJson(recognitionBean));
                                }
                                if(result.equals("粘贴")){
                                    SPUtils.getInstance().put("invitexy",new Gson().toJson(recognitionBean));
                                }
                                if(result.equals("粘贴")){
                                    SPUtils.getInstance().put("ks_invitexy",new Gson().toJson(recognitionBean));
                                }
                                if(result.contains("输入好友的邀请码")){
                                    SPUtils.getInstance().put("findEdit",new Gson().toJson(recognitionBean));
                                }
                                if(result.contains("向好友询问邀请码")){
                                    SPUtils.getInstance().put("ks_findEdit",new Gson().toJson(recognitionBean));
                                }

                            }

                        } catch (Exception e) {
                            Log.d(TAG, "Exception: " + e.getMessage());
                            CrashReport.postCatchedException(new Throwable("PhotoTagPresenter:" + e.getMessage()));
                        }

                    }
                }
            }
        });

    }
}
