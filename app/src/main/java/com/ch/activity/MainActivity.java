package com.ch.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.ch.adapter.FragmentAdapter;
import com.ch.application.MyApplication;
import com.ch.common.CommonDialogManage;
import com.ch.common.PerMissionManage;
import com.ch.common.RecognitionManage;
import com.ch.common.RecommendCodeManage;
import com.ch.common.leancloud.CheckPayTask;
import com.ch.common.leancloud.InitTask;
import com.ch.core.utils.Constant;
import com.ch.core.utils.FragmentNavigator;
import com.ch.core.utils.SFUpdaterUtils;
import com.ch.core.utils.Utils;
import com.ch.fragment.CouponFragment;
import com.ch.fragment.MainPageFragment;
import com.ch.fragment.SettingFragment;
import com.ch.jixieshou.R;
import com.ch.model.ScreenShootEvet;
import com.ch.utils.AssetUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.tencent.bugly.crashreport.CrashReport;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.ThreadMode;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import static android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_TAKE_SCREENSHOT;

public class MainActivity extends AppCompatActivity {
    private String TAG = this.getClass().getSimpleName();

    private BottomNavigationView navigation;
    private TextView tv_version;
    private List<Fragment> fragments;
    private FragmentNavigator mNavigator;
    boolean permission = false;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                setCurrentTab(0);
                return true;
            case R.id.navigation_coupon:
                setCurrentTab(1);
                return true;
            case R.id.navigation_dashboard:
                setCurrentTab(2);
                return true;
        }
        return false;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        EventBus.getDefault().register(this);
        SFUpdaterUtils.checkVersion(this);
        MyApplication.getAppInstance().setMainActivity(this);
        permission = PerMissionManage.getSingleton().requestPermission(MainActivity.this);

        initView();
        initData();
        fragments = new ArrayList<>();
        fragments.add(MainPageFragment.newInstance());
        fragments.add(CouponFragment.newInstance());
        fragments.add(SettingFragment.newInstance());

        tv_version = findViewById(R.id.tv_version);
        tv_version.setText("捡豆子助手V" + AppUtils.getAppVersionName());
        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mNavigator = new FragmentNavigator(getSupportFragmentManager(), new FragmentAdapter(fragments), R.id.container);
        mNavigator.setDefaultPosition(0);
        mNavigator.onCreate(savedInstanceState);

        setCurrentTab(0);

        String userName = RecommendCodeManage.getSingleton().getMyCode();
        if (TextUtils.isEmpty(userName)) {
            String mac = DeviceUtils.getAndroidID();
            String mac2 = mac.substring(mac.length() - 8);
            RecommendCodeManage.getSingleton().saveMyCode(mac2);
            Constant.user = mac2;
        } else {
            Constant.user = userName;
        }
        CrashReport.setUserId(Constant.user);
        new InitTask().execute();
        if (TextUtils.isEmpty(Constant.parentCode) && !TextUtils.isEmpty(SPUtils.getInstance().getString("parentCode"))) {
            Constant.parentCode = SPUtils.getInstance().getString("parentCode");
        }
//        if(!TextUtils.isEmpty(Constant.parentCode)){
//            new InitCode().execute(Constant.parentCode);
//        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initView() {
        findViewById(R.id.img_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent().setClass(MainActivity.this,WebViewActivity.class));
//                CommonDialogManage.getSingleton().showShareAppDilaog(MainActivity.this);
            }
        });
    }

    private void initData() {
        MyApplication.recommendBean = RecommendCodeManage.getSingleton().getRecommendBean();
        MyApplication.appInfos = AssetUtils.getSingleton().getAppInfos(this);
        new CheckPayTask(this).execute();
    }


    private void setCurrentTab(int position) {
        mNavigator.showFragment(position, false, true, MainActivity.this);
        for (int i = 0; i < fragments.size(); i++) {
            if (i == position) {
                fragments.get(i).setUserVisibleHint(true);
            } else {
                fragments.get(i).setUserVisibleHint(false);
            }
        }
    }


    @org.greenrobot.eventbus.Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ScreenShootEvet event) {
        LogUtils.d(TAG, "ScreenShootEvet");
        if(RecognitionManage.getSingleton().isRecogniting()){
            return;
        }
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
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    permission = true;
                } else {
                    Toast.makeText(this, "软件退出，运行权限被禁止", Toast.LENGTH_SHORT).show();
                    permission = false;
                    showPermissionFailDialog();
                    Log.i("=======================", "权限" + permissions[i] + "申请失败");
//                    System.exit(0);
                }
            }
        }
    }

    private void showPermissionFailDialog() {
        CommonDialogManage.getSingleton().showPermissionFailDialog(MainActivity.this);
    }

    /**
     * 遍历循环所有的网络接口，找到接口是 wlan0
     * 必须的权限 <uses-permission android:name="android.permission.INTERNET" />
     *
     * @return
     */
    private static String getMacFromHardware() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "02:00:00:00:00:00";
    }

}
